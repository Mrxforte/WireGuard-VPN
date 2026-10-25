package com.example.vpn.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Handles actions from the persistent VPN notification (e.g. quick Disconnect).
 */
class VpnActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        var onDisconnectRequested: (() -> Unit)? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_DISCONNECT) {
            onDisconnectRequested?.invoke()
        }
    }
}
