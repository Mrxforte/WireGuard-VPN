# Architecture Discovery: Current State

## 1. Environment & Build Configuration
* **Language**: Kotlin 2.2.10
* **Build System**: Gradle 9.1.1 (Kotlin DSL), Android Gradle Plugin 9.1.1
* **Compile SDK**: 36 (Android 15+ compatible)
* **Min SDK**: 24 (Android 7.0+)
* **Target SDK**: 36
* **UI Framework**: Jetpack Compose with Material Design 3
* **Java Compatibility**: Java 17 with Core Library Desugaring (`com.android.tools:desugar_jdk_libs:2.1.4`)

## 2. Existing VPN & Protocol Implementation
* **WireGuard Tunnel**: Official `com.wireguard.android:tunnel:1.0.20230706` library integrated via `com.wireguard.android.backend.GoBackend`.
* **VpnService Binding**: `com.wireguard.android.backend.GoBackend$VpnService` registered in `AndroidManifest.xml` with `android.permission.BIND_VPN_SERVICE`.
* **Cryptography**: Device-local Curve25519 `KeyPair` generation via `com.wireguard.crypto.KeyPair`. Private keys never leave the Android client.
* **OpenVPN Protocol Adapter**: Configuration compiler generating standard `.ovpn` profiles (UDP 1194 and TCP 443), CA cert validation, AES-256-GCM cipher, and routing directives (`redirect-gateway def1`, `route-ipv6 ::/0`).
* **Connection Orchestration**: State machine managing candidate ranking, server failover, protocol fallback, exponential backoff with jitter, and connectivity verification.
* **Leak Prevention**: Dual-stack full tunneling (`0.0.0.0/0` IPv4, `::/0` IPv6) and DNS servers (`1.1.1.1`, `1.0.0.1`, `2606:4700:4700::1111`).
* **Foreground Service**: `VpnNotificationManager` managing persistent notification with session duration, Rx/Tx throughput, and quick Disconnect action.

## 3. Backend & Data Plane
* **Django REST Framework Control Plane**: Located in `/django_vpn_backend/` with versioned endpoints (`/api/v1/auth/`, `/api/v1/me/`, `/api/v1/servers/`, `/api/v1/vpn/sessions/`, `/api/v1/diagnostics/`).
* **Database**: PostgreSQL 16 ready with SQLite fallback.
* **VPS Node Gateway**: Automated provisioning script `/django_vpn_backend/setup_vpn_node.sh` configuring WireGuard (`wg0` UDP 51820), OpenVPN UDP (1194), OpenVPN TCP (443), and iptables NAT masquerade.
* **Management Agent**: `/django_vpn_backend/agent/node_agent.py` using HMAC authentication to sync active sessions without arbitrary shell execution.
