package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.VpnConnectionState
import com.example.vpn.model.VpnServer
import com.example.vpn.wireguard.WireGuardVpnManager
import com.wireguard.crypto.KeyPair
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Satellite VPN", appName)
  }

  @Test
  fun `verify wireguard config builder and leak protection`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val vpnManager = WireGuardVpnManager(context)

    val clientPubKey = vpnManager.getOrCreateClientPublicKey()
    assertNotNull(clientPubKey)
    assertTrue(clientPubKey.isNotBlank())

    val peerConfig = PeerConfigResponse(
      peerId = "peer_test_1",
      serverId = "us-east-va",
      serverPublicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wMbCXo=",
      endpoint = "us-va.wireguard.cloud:51820",
      assignedIpv4 = "10.8.0.42/32",
      assignedIpv6 = "fd00:8::42/128",
      dnsServers = listOf("1.1.1.1", "1.0.0.1", "2606:4700:4700::1111"),
      mtu = 1420,
      expiresAtEpochSeconds = (System.currentTimeMillis() / 1000) + 86400,
      keepaliveSeconds = 25
    )

    val wgConfig = vpnManager.buildWireGuardConfig(peerConfig)
    assertNotNull(wgConfig)
    assertEquals(1, wgConfig.peers.size)

    val peer = wgConfig.peers.first()
    // Verify zero-leak full tunnel allowed IPs: both 0.0.0.0/0 (IPv4) and ::/0 (IPv6)
    val allowedIps = peer.allowedIps
    assertTrue("Should contain IPv4 full route", allowedIps.any { it.address is java.net.Inet4Address && it.mask == 0 })
    assertTrue("Should contain IPv6 full route", allowedIps.any { it.address is java.net.Inet6Address && it.mask == 0 })

    // Verify interface MTU and DNS
    val iface = wgConfig.`interface`
    assertEquals(1420, iface.mtu.orElse(0))
    assertEquals(3, iface.dnsServers.size)
  }
}
