# Threat Model and Security Architecture

## 1. System Overview and Scope
This document outlines the threat modeling, trust boundaries, cryptographic posture, and vulnerability mitigations for the Multi-Protocol Smart VPN system encompassing:
- Android Client Application (Kotlin / Jetpack Compose / WireGuard GoBackend / OpenVPN Adapter)
- Django REST Framework Control Plane (Token auth, Device Registry, Session Provisioner, Node Sync)
- VPS Gateway Nodes (Linux Kernel WireGuard `wg0`, OpenVPN daemons, HMAC-secured Node Agent)

---

## 2. Trust Boundaries & Data Flow

```
+-----------------------------------------------------------------------------------+
| CLIENT DEVICE (Untrusted Network / Public Wi-Fi / LTE)                            |
|                                                                                   |
|  +--------------------+     Curve25519 Gen      +------------------------------+  |
|  | Private Key Gen    | ----------------------> | Encrypted Android Keystore   |  |
|  | (Never exported)   |                         | EncryptedSharedPreferences   |  |
|  +--------------------+                         +------------------------------+  |
|            | Public Key Only                                                      |
+------------|----------------------------------------------------------------------+
             | TLS 1.3 / Certificate Pinning
             v
+-----------------------------------------------------------------------------------+
| CONTROL PLANE (Django REST Framework / PostgreSQL / Redis)                        |
|                                                                                   |
|  +-----------------------------------------------------------------------------+  |
|  | Device & Session Manager                                                    |  |
|  | - Verifies JWT Bearer Token                                                 |  |
|  | - Allocates dual-stack IP (/32 IPv4, /128 IPv6)                             |  |
|  | - Dispatches peer update to Node Agent via HMAC-SHA256 authenticated socket  |  |
|  +-----------------------------------------------------------------------------+  |
+------------|----------------------------------------------------------------------+
             | HMAC-SHA256 Command Sync (Mutual Auth)
             v
+-----------------------------------------------------------------------------------+
| VPS DATA PLANE GATEWAY (Linux Kernel 5.10+ / WireGuard / OpenVPN)                 |
|                                                                                   |
|  +--------------------+   iptables NAT   +-------------------------------------+  |
|  | wg0 / tun0 Engine  | ---------------> | WAN Interface (Leak-Protected Egress)|  |
|  +--------------------+                  +-------------------------------------+  |
+-----------------------------------------------------------------------------------+
```

---

## 3. Threat Identification & Mitigations (STRIDE Matrix)

| Threat Category | Potential Attack Vector | Impact | Engineering Mitigation |
| :--- | :--- | :--- | :--- |
| **Spoofing** | Rogue access point intercepts control traffic | MITM, credential theft | TLS 1.3 with certificate pinning on API endpoints; JWT bearer token expiration. |
| **Tampering** | Man-in-the-middle alters `.ovpn` or WireGuard configs | Redirection to malicious server, traffic inspection | WireGuard config endpoint signed; client computes sha256 checksum; OpenVPN embedded CA certificate verification (`<ca>` bundle pinned). |
| **Repudiation** | Malicious client claims legitimate session or exceeds device quota | Abuse, service exhaustion | Cryptographic device registration with unique UUID and hardware fingerprint; database records active sessions per user account. |
| **Information Disclosure** | Private key extracted from client APK or file system | User identity compromise, traffic decryption | Client generates Curve25519 private key strictly locally on the Android device. Private key is never sent to the Django server. Only public key is transmitted. |
| **Information Disclosure** | IPv6 / WebRTC / DNS leak bypasses VPN interface | User location & destination disclosure | Strict routing rules: WireGuard sets `AllowedIPs = 0.0.0.0/0, ::/0`; Android `VpnService.Builder` routes all IPv4 (`0.0.0.0/0`) and IPv6 (`::/0`), sets private DNS (`1.1.1.1`, `1.0.0.1`, `2606:4700:4700::1111`), and blocks non-VPN traffic. |
| **Denial of Service** | DDoS against single gateway node or API endpoint | Inability to connect | Smart Failover Policy: client detects heartbeat timeouts (3 consecutive ping failures or handshake timeout > 180s) and automatically migrates to next ranked server node. |
| **Elevation of Privilege** | Node Agent arbitrary command execution on VPS | Full VPS host compromise | Node agent uses restricted HMAC-SHA256 signature verification with a strict, parameterized command whitelist (`wg set wg0 peer <pubkey> allowed-ips <ip>`). No arbitrary shell strings are executed. |

---

## 4. Leak Prevention Architecture

### 4.1 DNS Leak Prevention
- Android's native `VpnService.Builder.addDnsServer()` assigns designated secure resolvers directly to the virtual network interface.
- Local system DNS configurations are bypassed because routes for all addresses (`0.0.0.0/0` and `::/0`) point directly to the tunnel interface.
- Django control plane provisions DNS addresses located inside the private VPN subnet or quad-9/cloudflare secure resolvers (`10.8.0.1`, `1.1.1.1`, `9.9.9.9`).

### 4.2 IPv6 Leak Prevention
- Even if the host physical network is pure IPv4 or dual-stack, the tunnel configures IPv6 subnet routing (`::/0`).
- If a server node does not support IPv6 routing, the client interface drops outbound IPv6 packets rather than allowing them to bypass the tunnel onto the cellular/Wi-Fi physical adapter.

### 4.3 WebRTC Leak Prevention
- Modern Chromium and Gecko engines on Android query the OS routing table. Because all interfaces except the `tun0`/WireGuard interface have higher routing metrics or are excluded via `underlyingNetworks(null)` when VPN is active, WebRTC STUN/TURN binding requests are constrained to the encrypted tunnel.

---

## 5. Key Lifecycle & Ephemeral Cryptography

1. **Generation**:
   - Device invokes `com.wireguard.crypto.KeyPair()` locally.
   - Generates 32-byte Curve25519 secret key using `SecureRandom`.
2. **Registration**:
   - Public key is derived via Curve25519 scalar multiplication (`X25519(sk, basepoint)`).
   - Public key is sent to Django `/api/v1/sessions/create/` over TLS.
   - Server returns assigned tunnel IP (e.g., `10.8.0.42/32`) and Server Public Key.
3. **Storage**:
   - Private key is stored temporarily in memory or Android Keystore / MasterKey encrypted preferences.
4. **Destruction**:
   - On disconnect or session termination, in-memory keys are zeroized (`Arrays.fill(keyBytes, 0.toByte())`).
   - Server revokes peer from WireGuard kernel table via node agent.
