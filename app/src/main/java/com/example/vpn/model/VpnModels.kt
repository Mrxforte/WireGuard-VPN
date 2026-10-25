package com.example.vpn.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Representation of a WireGuard VPN Server.
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
    val isPremiumOnly: Boolean = false
)

/**
 * FastAPI Authentication request and response models.
 */
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
    @Json(name = "user_id") val userId: String? = null
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
 * FastAPI WireGuard Dynamic Peer configuration negotiation models.
 */
@JsonClass(generateAdapter = true)
data class CreatePeerRequest(
    @Json(name = "server_id") val serverId: String,
    @Json(name = "client_public_key") val clientPublicKey: String,
    @Json(name = "device_name") val deviceName: String = "Android WireGuard Client",
    @Json(name = "request_ipv6") val requestIpv6: Boolean = true
)

@JsonClass(generateAdapter = true)
data class PeerConfigResponse(
    @Json(name = "peer_id") val peerId: String,
    @Json(name = "server_id") val serverId: String,
    @Json(name = "server_public_key") val serverPublicKey: String,
    val endpoint: String,
    @Json(name = "assigned_ipv4") val assignedIpv4: String,
    @Json(name = "assigned_ipv6") val assignedIpv6: String? = "fd00::2/128",
    @Json(name = "dns_servers") val dnsServers: List<String> = listOf("1.1.1.1", "1.0.0.1", "2606:4700:4700::1111"),
    val mtu: Int = 1420,
    @Json(name = "preshared_key") val presharedKey: String? = null,
    @Json(name = "expires_at") val expiresAtEpochSeconds: Long = (System.currentTimeMillis() / 1000) + 86400,
    @Json(name = "keepalive_seconds") val keepaliveSeconds: Int = 25
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
 * Real VPN connection state machine.
 */
enum class VpnConnectionState {
    DISCONNECTED,
    AUTHENTICATING,
    FETCHING_CONFIG,
    PREPARING_VPN,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    DISCONNECTING,
    ERROR
}

/**
 * Real-time WireGuard tunnel telemetry.
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
    val killSwitchEnabled: Boolean = true
)
