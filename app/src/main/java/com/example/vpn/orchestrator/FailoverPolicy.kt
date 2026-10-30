package com.example.vpn.orchestrator

import com.example.vpn.model.DiagnosticEvent
import com.example.vpn.model.SmartConnectionSettings
import com.example.vpn.model.VpnProtocol
import com.example.vpn.model.VpnServer
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Encapsulates smart connection failover and protocol fallback policies.
 */
class FailoverPolicy(
    private val settings: SmartConnectionSettings
) {
    /**
     * Computes exponential backoff with random jitter to prevent thundering herd.
     */
    fun computeBackoffMillis(retryAttempt: Int): Long {
        val baseDelay = 1000L
        val maxDelay = 8000L
        val exponential = (baseDelay * 2.0.pow(min(retryAttempt, 4).toDouble())).toLong()
        val jitter = Random.nextLong(200, 600)
        return min(exponential + jitter, maxDelay)
    }

    /**
     * Determines whether another server candidate should be attempted.
     */
    fun shouldFailoverServer(currentAttempt: Int, remainingServers: List<VpnServer>): Boolean {
        return settings.autoServerFailover &&
                currentAttempt < settings.maxRetryCount &&
                remainingServers.isNotEmpty()
    }

    /**
     * Determines if a protocol fallback should be triggered when server candidates are exhausted.
     */
    fun getFallbackProtocol(
        currentProtocol: VpnProtocol,
        attemptedProtocols: Set<VpnProtocol>
    ): VpnProtocol? {
        if (!settings.autoProtocolFallback) return null

        val fallbackOrder = listOf(
            VpnProtocol.WIREGUARD,
            VpnProtocol.OPENVPN_UDP,
            VpnProtocol.OPENVPN_TCP
        )

        return fallbackOrder.firstOrNull { it != currentProtocol && !attemptedProtocols.contains(it) && it.isSupported }
    }

    /**
     * Generates a sanitized diagnostic event for auditing.
     */
    fun createDiagnosticEvent(
        category: String,
        message: String,
        level: String = "INFO"
    ): DiagnosticEvent {
        return DiagnosticEvent(
            timestamp = System.currentTimeMillis(),
            level = level,
            category = category,
            message = message
        )
    }
}
