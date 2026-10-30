package com.example.vpn.protocol

import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnSessionState
import com.example.vpn.model.VpnTunnelStats
import kotlinx.coroutines.flow.StateFlow

/**
 * Common protocol adapter interface for multi-protocol VPN integration.
 * WireGuard, OpenVPN UDP, and OpenVPN TCP adhere to this lifecycle contract.
 */
interface ProtocolAdapter {
    val protocol: VpnProtocol

    /**
     * Verifies if this protocol engine is functional on this Android device.
     */
    fun isAvailable(): Boolean

    /**
     * Validates that the server provides all required parameters for this protocol.
     */
    fun validateServer(server: VpnServer): Boolean

    /**
     * Asynchronously starts the tunnel session using the provided negotiated credentials.
     */
    suspend fun startTunnel(server: VpnServer, config: PeerConfigResponse): Result<Unit>

    /**
     * Cleanly stops the tunnel session and tears down the network routing table.
     */
    suspend fun stopTunnel(): Result<Unit>

    /**
     * Observes real connection state transitions from the protocol engine.
     */
    val state: StateFlow<VpnSessionState>

    /**
     * Polls or emits live tunnel throughput metrics.
     */
    val stats: StateFlow<VpnTunnelStats>

    /**
     * Last diagnostic error message if any occurred.
     */
    val lastError: StateFlow<String?>
}
