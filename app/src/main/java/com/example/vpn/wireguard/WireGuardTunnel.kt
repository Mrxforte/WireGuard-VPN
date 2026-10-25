package com.example.vpn.wireguard

import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of WireGuard's [Tunnel] interface.
 * Tracks and emits the native backend's state transitions.
 */
class WireGuardTunnel(
    private val tunnelName: String = "wg_client_tunnel"
) : Tunnel {

    private val _state = MutableStateFlow(Tunnel.State.DOWN)
    val state: StateFlow<Tunnel.State> = _state.asStateFlow()

    override fun getName(): String = tunnelName

    override fun onStateChange(newState: Tunnel.State) {
        _state.value = newState
    }
}
