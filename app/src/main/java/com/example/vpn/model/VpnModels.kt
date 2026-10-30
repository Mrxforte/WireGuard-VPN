package com.example.vpn.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Supported VPN protocols with security and transport specifications.
 */
enum class VpnProtocol(
    val displayName: String,
    val defaultPort: Int,
    val transport: String,
    val isSupported: Boolean,
    val cipher: String,
    val description: String
) {
    WIREGUARD(
        displayName = "WireGuard",
        defaultPort = 51820,
        transport = "UDP",
        isSupported = true,
        cipher = "ChaCha20-Poly1305",
        description = "Modern, high-performance crypto tunnel with minimal latency and battery impact"
    ),
    OPENVPN_UDP(
        displayName = "OpenVPN (UDP)",
        defaultPort = 1194,
        transport = "UDP",
        isSupported = true,
        cipher = "AES-256-GCM",
        description = "Industry standard protocol optimized for high-throughput streaming and gaming"
    ),
    OPENVPN_TCP(
        displayName = "OpenVPN (TCP)",
        defaultPort = 443,
        transport = "TCP",
        isSupported = true,
        cipher = "AES-256-GCM",
        description = "Resilient transport over port 443 that bypasses restrictive firewalls and DPI filters"
    )
}

/**
 * Authoritative VPN connection state machine per Architecture Specification:
 * - DISCONNECTED
 * - PREPARING
 * - CONNECTING
 * - VERIFYING_TUNNEL
 * - VERIFYING_HANDSHAKE
 * - VERIFYING_ROUTING
 * - VERIFYING_INTERNET
 * - VERIFYING_DNS
 * - VERIFYING_IP_LEAKS
 * - CONNECTED
 * - DEGRADED
 * - RECONNECTING
 * - SWITCHING_SERVER
 * - SWITCHING_PROTOCOL
 * - FAILED
 * (With AWAITING_VPN_PERMISSION and DISCONNECTING for compatibility)
 */
enum class VpnSessionState(val isStableConnected: Boolean = false, val isProgressing: Boolean = false) {
    DISCONNECTED(isStableConnected = false, isProgressing = false),
    PREPARING(isStableConnected = false, isProgressing = true),
    AWAITING_VPN_PERMISSION(isStableConnected = false, isProgressing = true),
    CONNECTING(isStableConnected = false, isProgressing = true),
    VERIFYING_TUNNEL(isStableConnected = false, isProgressing = true),
    VERIFYING_HANDSHAKE(isStableConnected = false, isProgressing = true),
    VERIFYING_ROUTING(isStableConnected = false, isProgressing = true),
    VERIFYING_INTERNET(isStableConnected = false, isProgressing = true),
    VERIFYING_DNS(isStableConnected = false, isProgressing = true),
    VERIFYING_IP_LEAKS(isStableConnected = false, isProgressing = true),
    VERIFYING_CONNECTIVITY(isStableConnected = false, isProgressing = true), // Legacy alias
    CONNECTED(isStableConnected = true, isProgressing = false),
    DEGRADED(isStableConnected = true, isProgressing = false),
    RECONNECTING(isStableConnected = false, isProgressing = true),
    SWITCHING_SERVER(isStableConnected = false, isProgressing = true),
    SWITCHING_PROTOCOL(isStableConnected = false, isProgressing = true),
    DISCONNECTING(isStableConnected = false, isProgressing = true),
    FAILED(isStableConnected = false, isProgressing = false),
    ERROR(isStableConnected = false, isProgressing = false)
}

// Backward compatibility alias for UI and service layers
typealias VpnConnectionState = VpnSessionState

/**
 * Individual stages of the mandatory Quality Gate verification pipeline.
 */
enum class ConnectionVerificationStage(val stageName: String, val isMandatory: Boolean = true) {
    CONFIG_VALIDATION("Config & Credentials Validation", true),
    TUNNEL_INTERFACE("Android VPN Interface Established", true),
    PROTOCOL_HANDSHAKE("Cryptographic Handshake Freshness", true),
    ROUTING_BINDING("Tunnel Routing & Interface Binding", true),
    INTERNET_ACCESS("End-to-End Tunnel HTTPS Reachability", true),
    DNS_INTEGRITY("DNS Resolution & Leak Prevention", true),
    EGRESS_IP_VALIDATION("Expected Egress IP & Gateway Identity", true),
    LEAK_PROTECTION("IPv4/IPv6 Full Tunnel Leak Check", true)
}

enum class VerificationStatus {
    PASSED,
    FAILED,
    SKIPPED,
    IN_PROGRESS,
    DEGRADED
}

data class StageResult(
    val stage: ConnectionVerificationStage,
    val status: VerificationStatus,
    val latencyMs: Long = 0L,
    val detailMessage: String = "",
    val sanitizedError: String? = null
)

/**
 * Structured, auditable verification report recorded upon every connection attempt.
 * Never stores private keys or sensitive credentials.
 */
data class ConnectionVerificationReport(
    val sessionId: String,
    val serverId: String,
    val serverName: String,
    val protocol: VpnProtocol,
    val startedEpoch: Long = System.currentTimeMillis(),
    val finishedEpoch: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val isSuccess: Boolean = false,
    val isDegraded: Boolean = false,
    val failureStage: ConnectionVerificationStage? = null,
    val sanitizedFailureReason: String? = null,
    val stages: List<StageResult> = emptyList(),
    val verifiedEgressIp: String? = null,
    val verifiedDnsServer: String? = null
)

/**
 * Representation of a multi-protocol VPN server endpoint.
 */
@JsonClass(generateAdapter = true)
data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val city: String,
    val host: String,
    val port: Int = 51820,
    val publicKey: String,
    val pingMs: Int = 35,
    val loadPercentage: Int = 42,
    val isOnline: Boolean = true,
    val flagEmoji: String = "🌐",
    val serviceCategory: String = "general",
    val serviceLabel: String = "Ultra Fast",
    val isPremiumOnly: Boolean = false,
    val wireguardPort: Int = 51820,
    val openvpnUdpPort: Int = 1194,
    val openvpnTcpPort: Int = 443,
    val supportedProtocols: List<String> = listOf("WIREGUARD", "OPENVPN_UDP", "OPENVPN_TCP"),
    val openvpnCaCert: String = "-----BEGIN CERTIFICATE-----\nMIIDQjCCAiqgAwIBAgIU...\n-----END CERTIFICATE-----",
    val tier: String = "free",
    val isFavorite: Boolean = false
)

/**
 * User smart connection settings and failover policies.
 */
data class SmartConnectionSettings(
    val autoServerSelection: Boolean = true,
    val preferredProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val autoProtocolFallback: Boolean = true,
    val autoServerFailover: Boolean = true,
    val maxRetryCount: Int = 3,
    val connectionTimeoutSeconds: Int = 15,
    val reconnectOnNetworkChange: Boolean = true,
    val killSwitchEnabled: Boolean = false,
    val exitNodeRotationMinutes: Int = 0, // 0 = disabled
    // Quality Gate specific policies
    val strictPrivacyMode: Boolean = true,
    val allowDegradedConnection: Boolean = false,
    val requireEgressIpMatch: Boolean = false,
    val verificationTimeoutMs: Long = 8000L
)

/**
 * Sanitized diagnostic event entry.
 */
data class DiagnosticEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val level: String = "INFO",
    val category: String,
    val message: String
)

/**
 * Real-time connection diagnostic telemetry.
 */
data class ConnectionDiagnostics(
    val activeServerName: String = "None",
    val activeProtocol: VpnProtocol? = null,
    val sessionState: VpnSessionState = VpnSessionState.DISCONNECTED,
    val lastHandshakeEpoch: Long = 0L,
    val lastConnectivityCheckMs: Long = 0L,
    val dnsHealthStatus: String = "Secure (1.1.1.1)",
    val tunnelHealthStatus: String = "Optimal",
    val retryCount: Int = 0,
    val failoverReason: String? = null,
    val lastVerificationReport: ConnectionVerificationReport? = null,
    val events: List<DiagnosticEvent> = emptyList()
)

/**
 * Registered user device model for multi-device management.
 */
@JsonClass(generateAdapter = true)
data class RegisteredDevice(
    val id: String,
    @Json(name = "device_name") val deviceName: String,
    @Json(name = "device_model") val deviceModel: String,
    @Json(name = "registered_at") val registeredAt: String,
    @Json(name = "last_seen_at") val lastSeenAt: String,
    @Json(name = "is_current") val isCurrentDevice: Boolean = false
)

/**
 * Django Account & Entitlement models.
 */
@JsonClass(generateAdapter = true)
data class UserAccount(
    val id: String,
    val username: String,
    val email: String,
    val tier: String = "Pro",
    @Json(name = "max_devices") val maxDevices: Int = 5,
    @Json(name = "active_devices") val activeDevices: List<RegisteredDevice> = emptyList(),
    val token: String? = null,
    @Json(name = "expires_at") val expiresAt: Long = 0L
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class TokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String = "bearer",
    @Json(name = "expires_in") val expiresIn: Long = 86400,
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    val username: String,
    val email: String? = null,
    val tier: String = "Pro",
    val maxPeers: Int = 5,
    val activePeers: Int = 1
)

/**
 * Session provisioning request and response models.
 */
@JsonClass(generateAdapter = true)
data class CreatePeerRequest(
    @Json(name = "server_id") val serverId: String,
    @Json(name = "client_public_key") val clientPublicKey: String,
    @Json(name = "protocol") val protocol: String = "WIREGUARD",
    @Json(name = "device_name") val deviceName: String = "Android Device",
    @Json(name = "request_ipv6") val requestIpv6: Boolean = true
)

@JsonClass(generateAdapter = true)
data class PeerConfigResponse(
    @Json(name = "peer_id") val peerId: String,
    @Json(name = "server_id") val serverId: String,
    @Json(name = "server_public_key") val serverPublicKey: String,
    val endpoint: String,
    @Json(name = "assigned_ipv4") val assignedIpv4: String,
    @Json(name = "assigned_ipv6") val assignedIpv6: String? = "fd00:8::2/128",
    @Json(name = "dns_servers") val dnsServers: List<String> = listOf("1.1.1.1", "1.0.0.1", "2606:4700:4700::1111"),
    val mtu: Int = 1420,
    @Json(name = "preshared_key") val presharedKey: String? = null,
    @Json(name = "expires_at") val expiresAtEpochSeconds: Long = (System.currentTimeMillis() / 1000) + 86400,
    @Json(name = "keepalive_seconds") val keepaliveSeconds: Int = 25,
    @Json(name = "openvpn_config") val openvpnConfig: String? = null
)

@JsonClass(generateAdapter = true)
data class RenewPeerResponse(
    @Json(name = "peer_id") val peerId: String,
    @Json(name = "expires_at") val expiresAtEpochSeconds: Long,
    val status: String = "active"
)

@JsonClass(generateAdapter = true)
data class PeerStatusResponse(
    @Json(name = "peer_id") val peerId: String,
    val status: String,
    @Json(name = "bytes_received") val bytesReceived: Long = 0,
    @Json(name = "bytes_sent") val bytesSent: Long = 0,
    @Json(name = "last_handshake_epoch") val lastHandshakeEpoch: Long = 0,
    @Json(name = "is_revoked") val isRevoked: Boolean = false
)

/**
 * Health check response from controlled endpoint.
 */
@JsonClass(generateAdapter = true)
data class HealthCheckResponse(
    val status: String,
    @Json(name = "correlation_id") val correlationId: String? = null,
    @Json(name = "timestamp_ms") val timestampMs: Long = 0L,
    @Json(name = "client_ip") val clientIp: String? = null,
    val service: String? = null
)

/**
 * Real-time tunnel telemetry.
 */
data class VpnTunnelStats(
    val rxBytes: Long = 0L,
    val txBytes: Long = 0L,
    val rxSpeedBps: Long = 0L,
    val txSpeedBps: Long = 0L,
    val sessionDurationSeconds: Long = 0L,
    val lastHandshakeTime: Long = 0L
)

/**
 * Comprehensive leak protection status.
 */
data class SecurityLeakStatus(
    val isIpv4FullTunnel: Boolean = true,
    val isIpv6LeakProtected: Boolean = true,
    val isDnsLeakProtected: Boolean = true,
    val dnsResolvers: List<String> = listOf("1.1.1.1", "1.0.0.1", "2606:4700:4700::1111"),
    val killSwitchEnabled: Boolean = false
)
