package com.example.vpn.data

import com.example.vpn.model.CreatePeerRequest
import com.example.vpn.model.LoginRequest
import com.example.vpn.model.PeerConfigResponse
import com.example.vpn.model.PeerStatusResponse
import com.example.vpn.model.RegisterRequest
import com.example.vpn.model.RenewPeerResponse
import com.example.vpn.model.TokenResponse
import com.example.vpn.model.UserProfile
import com.example.vpn.model.VpnServer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * REST API client for the FastAPI WireGuard VPN controller.
 */
interface FastApiVpnApi {

    @POST("auth/token")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<TokenResponse>

    @GET("auth/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserProfile>

    @GET("servers")
    suspend fun getServers(): Response<List<VpnServer>>

    @POST("vpn/peer")
    suspend fun registerPeer(
        @Header("Authorization") token: String,
        @Body request: CreatePeerRequest
    ): Response<PeerConfigResponse>

    @POST("vpn/peer/{peerId}/renew")
    suspend fun renewPeer(
        @Header("Authorization") token: String,
        @Path("peerId") peerId: String
    ): Response<RenewPeerResponse>

    @DELETE("vpn/peer/{peerId}")
    suspend fun revokePeer(
        @Header("Authorization") token: String,
        @Path("peerId") peerId: String
    ): Response<Unit>

    @GET("vpn/peer/{peerId}/status")
    suspend fun getPeerStatus(
        @Header("Authorization") token: String,
        @Path("peerId") peerId: String
    ): Response<PeerStatusResponse>
}
