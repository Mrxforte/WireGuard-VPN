package com.example.vpn.orchestrator

import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import java.util.concurrent.ConcurrentHashMap

/**
 * Intelligent server selector that ranks candidates using measured latency,
 * server load capacity, protocol compatibility, and quarantine health status.
 */
class ServerSelector {

    // Quarantine map: serverId -> expiration epoch millis
    private val quarantinedServers = ConcurrentHashMap<String, Long>()

    /**
     * Marks a server as temporarily quarantined due to connection failure.
     */
    fun quarantineServer(serverId: String, durationMinutes: Long = 5) {
        val expiry = System.currentTimeMillis() + (durationMinutes * 60 * 1000)
        quarantinedServers[serverId] = expiry
    }

    /**
     * Clears quarantine for a server or prunes expired quarantines.
     */
    fun pruneQuarantines() {
        val now = System.currentTimeMillis()
        val expired = quarantinedServers.filterValues { it < now }.keys
        expired.forEach { quarantinedServers.remove(it) }
    }

    fun isQuarantined(serverId: String): Boolean {
        pruneQuarantines()
        return quarantinedServers.containsKey(serverId)
    }

    /**
     * Ranks candidate servers for connection.
     * Evaluates health, online status, protocol support, capacity, and latency.
     */
    fun rankServers(
        servers: List<VpnServer>,
        protocol: VpnProtocol,
        userTier: String = "Pro",
        preferredServer: VpnServer? = null
    ): List<VpnServer> {
        pruneQuarantines()

        // Filter eligible servers
        val eligible = servers.filter { server ->
            server.isOnline &&
            server.supportedProtocols.contains(protocol.name) &&
            (!server.isPremiumOnly || userTier.equals("Pro", ignoreCase = true) || userTier.equals("Enterprise", ignoreCase = true))
        }

        if (eligible.isEmpty()) {
            return emptyList()
        }

        // Partition into non-quarantined and quarantined (quarantined placed at end as last resort)
        val healthy = eligible.filter { !isQuarantined(it.id) }
        val quarantined = eligible.filter { isQuarantined(it.id) }

        // Ranking algorithm: score = (ping * 0.6) + (load * 0.4)
        val comparator = Comparator<VpnServer> { a, b ->
            val scoreA = (a.pingMs * 0.6) + (a.loadPercentage * 0.4)
            val scoreB = (b.pingMs * 0.6) + (b.loadPercentage * 0.4)
            scoreA.compareTo(scoreB)
        }

        val sortedHealthy = healthy.sortedWith(comparator)
        val sortedQuarantined = quarantined.sortedWith(comparator)

        // If preferred server is healthy, boost it to index 0
        if (preferredServer != null && sortedHealthy.any { it.id == preferredServer.id }) {
            val boost = sortedHealthy.filter { it.id == preferredServer.id }
            val rest = sortedHealthy.filter { it.id != preferredServer.id }
            return boost + rest + sortedQuarantined
        }

        return sortedHealthy + sortedQuarantined
    }
}
