package com.example

import com.example.vpn.catalog.ImplementationStatus
import com.example.vpn.catalog.TechnologyCatalog
import com.example.vpn.catalog.TechnologyGroup
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.SmartConnectionSettings
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnSessionState
import com.example.vpn.orchestrator.FailoverPolicy
import com.example.vpn.orchestrator.ServerSelector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VpnQualityGateTest {

    @Test
    fun `verify 100 networking technologies catalog completeness`() {
        val all = TechnologyCatalog.ALL_100_TECHNOLOGIES
        assertEquals("Catalog must contain exactly 100 entries", 100, all.size)

        // Ensure all IDs are unique
        val ids = all.map { it.id }.toSet()
        assertEquals("All 100 catalog IDs must be unique", 100, ids.size)

        // Ensure all 5 groups are populated
        TechnologyGroup.values().forEach { group ->
            val itemsInGroup = TechnologyCatalog.getTechnologiesByGroup(group)
            assertTrue("Group ${group.title} must have items", itemsInGroup.isNotEmpty())
        }

        // Verify native supported protocols exist
        val supported = TechnologyCatalog.getSupportedProtocols()
        assertTrue(supported.any { it.id == "wireguard" })
        assertTrue(supported.any { it.id == "openvpn_udp" })
        assertTrue(supported.any { it.id == "openvpn_tcp" })
    }

    @Test
    fun `verify server ranking algorithm and quarantine`() {
        val selector = ServerSelector()

        val fastServer = VpnServer(
            id = "fast-node",
            name = "Frankfurt 18ms",
            country = "Germany",
            countryCode = "DE",
            city = "Frankfurt",
            host = "de.smartvpn.cloud",
            publicKey = "pub1",
            pingMs = 18,
            loadPercentage = 20,
            supportedProtocols = listOf("WIREGUARD", "OPENVPN_UDP", "OPENVPN_TCP")
        )

        val slowServer = VpnServer(
            id = "slow-node",
            name = "Sydney 110ms",
            country = "Australia",
            countryCode = "AU",
            city = "Sydney",
            host = "au.smartvpn.cloud",
            publicKey = "pub2",
            pingMs = 110,
            loadPercentage = 80,
            supportedProtocols = listOf("WIREGUARD", "OPENVPN_UDP", "OPENVPN_TCP")
        )

        val ranked = selector.rankServers(
            servers = listOf(slowServer, fastServer),
            protocol = VpnProtocol.WIREGUARD,
            preferredServer = null
        )
        assertEquals(2, ranked.size)
        assertEquals("Fast server with low ping and load must rank first", "fast-node", ranked[0].id)

        // Quarantine fast server
        selector.quarantineServer("fast-node", 5)
        val rankedAfterQuarantine = selector.rankServers(
            servers = listOf(slowServer, fastServer),
            protocol = VpnProtocol.WIREGUARD,
            preferredServer = null
        )
        assertEquals(2, rankedAfterQuarantine.size)
        assertEquals("Healthy node must rank ahead of quarantined node", "slow-node", rankedAfterQuarantine[0].id)
        assertEquals("Quarantined node is placed at the end", "fast-node", rankedAfterQuarantine[1].id)
    }

    @Test
    fun `verify protocol fallback sequence WireGuard to OpenVPN UDP to TCP`() {
        val settings = SmartConnectionSettings(
            autoProtocolFallback = true,
            preferredProtocol = VpnProtocol.WIREGUARD
        )
        val policy = FailoverPolicy(settings)

        val attempted = mutableSetOf(VpnProtocol.WIREGUARD)
        val fallback1 = policy.getFallbackProtocol(VpnProtocol.WIREGUARD, attempted)
        assertEquals(VpnProtocol.OPENVPN_UDP, fallback1)

        attempted.add(VpnProtocol.OPENVPN_UDP)
        val fallback2 = policy.getFallbackProtocol(VpnProtocol.OPENVPN_UDP, attempted)
        assertEquals(VpnProtocol.OPENVPN_TCP, fallback2)

        attempted.add(VpnProtocol.OPENVPN_TCP)
        val fallback3 = policy.getFallbackProtocol(VpnProtocol.OPENVPN_TCP, attempted)
        assertNull("All protocols exhausted", fallback3)
    }

    @Test
    fun `verify exponential backoff with bounded jitter`() {
        val policy = FailoverPolicy(SmartConnectionSettings())

        val backoff1 = policy.computeBackoffMillis(1)
        assertTrue("Attempt 1 backoff >= 2000ms", backoff1 in 2000..3000)

        val backoff3 = policy.computeBackoffMillis(3)
        assertTrue("Attempt 3 backoff should scale exponentially", backoff3 >= 6000)
    }

    @Test
    fun `verify session state properties for quality gate`() {
        assertFalse(VpnSessionState.DISCONNECTED.isStableConnected)
        assertFalse(VpnSessionState.PREPARING.isStableConnected)
        assertFalse(VpnSessionState.VERIFYING_TUNNEL.isStableConnected)
        assertFalse(VpnSessionState.VERIFYING_HANDSHAKE.isStableConnected)
        assertFalse(VpnSessionState.VERIFYING_ROUTING.isStableConnected)
        assertFalse(VpnSessionState.VERIFYING_INTERNET.isStableConnected)
        assertFalse(VpnSessionState.VERIFYING_DNS.isStableConnected)
        assertFalse(VpnSessionState.VERIFYING_IP_LEAKS.isStableConnected)
        assertFalse(VpnSessionState.FAILED.isStableConnected)

        assertTrue(VpnSessionState.CONNECTED.isStableConnected)
        assertTrue(VpnSessionState.DEGRADED.isStableConnected)
    }
}
