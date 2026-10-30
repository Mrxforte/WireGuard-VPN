package com.example.vpn.protocol

import android.content.Context
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnSessionState
import com.example.vpn.model.VpnTunnelStats
import com.example.vpn.wireguard.WireGuardVpnManager
import kotlinx.coroutines.flow.StateFlow

/**
 * Protocol adapter implementing WireGuard via official GoBackend.
 */
class WireGuardProtocolAdapter(
    private val vpnManager: WireGuardVpnManager
) : ProtocolAdapter {

    override val protocol: VpnProtocol = VpnProtocol.WIREGUARD

    override fun isAvailable(): Boolean = true

    override fun validateServer(server: VpnServer): Boolean {
        return server.publicKey.isNotBlank() && server.host.isNotBlank() && server.port > 0
    }

    override suspend fun startTunnel(server: VpnServer, config: PeerConfigResponse): Result<Unit> {
        return vpnManager.startTunnel(config)
    }

    override suspend fun stopTunnel(): Result<Unit> {
        return vpnManager.stopTunnel()
    }

    override val state: StateFlow<VpnSessionState> = vpnManager.connectionState
    override val stats: StateFlow<VpnTunnelStats> = vpnManager.stats
    override val lastError: StateFlow<String?> = vpnManager.lastErrorMessage

    fun getClientPublicKey(): String = vpnManager.getOrCreateClientPublicKey()
    fun rotateClientKeyPair() = vpnManager.rotateClientKeyPair()
    fun prepareVpn() = vpnManager.prepareVpn()
}
