package com.example.vpn.data

import com.example.vpn.model.CreatePeerRequest
import com.example.vpn.model.DiagnosticEvent
import com.example.vpn.model.HealthCheckResponse
import com.example.vpn.model.LoginRequest
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.PeerStatusResponse
import com.example.vpn.model.RegisterRequest
import com.example.vpn.model.RegisteredDevice
import com.example.vpn.model.RenewPeerResponse
import com.example.vpn.model.TokenResponse
import com.example.vpn.model.UserAccount
import com.example.vpn.model.UserProfile
import com.example.vpn.model.VpnServer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Django REST Framework API interface for smart multi-protocol VPN backend.
 * Corresponds to /api/v1/ versioned endpoints.
 */
interface DjangoVpnApi {

    // Auth & Identity (/api/v1/auth/)
    @POST("auth/token/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @POST("auth/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<TokenResponse>

    // Profile & Devices (/api/v1/me/)
    @GET("me/")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserAccount>

    @GET("me/devices/")
    suspend fun getDevices(
        @Header("Authorization") token: String
    ): Response<List<RegisteredDevice>>

    @DELETE("me/devices/{deviceId}/")
    suspend fun revokeDevice(
        @Header("Authorization") token: String,
        @Path("deviceId") deviceId: String
    ): Response<Unit>

    // Server Registry (/api/v1/servers/)
    @GET("servers/")
    suspend fun getServers(
        @Query("protocol") protocol: String? = null,
        @Query("tier") tier: String? = null
    ): Response<List<VpnServer>>

    // VPN Sessions & Peer Provisioning (/api/v1/vpn/sessions/)
    @POST("vpn/sessions/")
    suspend fun createSession(
        @Header("Authorization") token: String,
        @Body request: CreatePeerRequest
    ): Response<PeerConfigResponse>

    @POST("vpn/sessions/{sessionId}/renew/")
    suspend fun renewSession(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: String
    ): Response<RenewPeerResponse>

    @DELETE("vpn/sessions/{sessionId}/")
    suspend fun revokeSession(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: String
    ): Response<Unit>

    @GET("vpn/sessions/{sessionId}/status/")
    suspend fun getSessionStatus(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: String
    ): Response<PeerStatusResponse>

    // Diagnostics Audit Ingestion (/api/v1/diagnostics/)
    @POST("diagnostics/")
    suspend fun submitDiagnostics(
        @Header("Authorization") token: String,
        @Body event: DiagnosticEvent
    ): Response<Unit>

    @GET("diagnostics/health/")
    suspend fun probeHealth(
        @Query("correlation_id") correlationId: String
    ): Response<HealthCheckResponse>
}
