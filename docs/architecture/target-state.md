# Architecture Target State: Enterprise Multi-Protocol Platform

## 1. System Vision & Modularity
The target state elevates the Android VPN application into an enterprise-grade platform capable of indexing, managing, and connecting through diverse network tunneling, overlay, proxy, and enterprise VPN technologies.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Android Client UI Layer                         │
│  Home Dashboard • Server Browser • 100-Tech Catalog • Diagnostics • Settings  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ StateFlow & Intents
┌───────────────────────────────────▼────────────────────────────────────┐
│                    Smart Connection Orchestrator                       │
│  State Machine (11 States) • ServerSelector • FailoverPolicy • LeakShield│
└───────────────────┬────────────────────────────────┬───────────────────┘
                    │                                │
    ┌───────────────▼──────────────┐   ┌─────────────▼───────────────┐
    │  Unified VpnEngine Layer     │   │   Django REST Client        │
    │  • WireGuardEngine           │   │   • /api/v1/auth/           │
    │  • OpenVpnEngine (UDP/TCP)   │   │   • /api/v1/capabilities/   │
    │  • ProxyTransportEngine      │   │   • /api/v1/servers/        │
    │  • Technology Registry (100) │   │   • /api/v1/vpn/sessions/   │
    └───────────────┬──────────────┘   └─────────────┬───────────────┘
                    │                                │
┌───────────────────▼────────────────────────────────▼───────────────────┐
│                 Self-Hosted Django & VPS Infrastructure                │
│  Master API • PostgreSQL • Redis • Node Agent Daemon • Gateway Nodes  │
└────────────────────────────────────────────────────────────────────────┘
```

## 2. 100-Technology Catalog Structure
The 100 requested technologies are classified into 5 distinct architectural categories:
1. **Group A — VPN Protocols & Overlays**: Full-device encrypted IP tunnels (WireGuard, OpenVPN, IPsec, Tailscale, ZeroTier, etc.).
2. **Group B — Proxy Protocols & Transports**: Application-level proxies and circumvention protocols (Shadowsocks, SOCKS5, VLESS, Trojan, Hysteria 2, etc.).
3. **Group C — Tunnel & Transport Mechanisms**: Encapsulation and framing building blocks (TLS, DTLS, WebSocket, stunnel, wstunnel, gRPC, QUIC).
4. **Group D — Enterprise Networking & Legacy Tunnels**: Enterprise SSL gateways and point-to-point encapsulations (Cisco AnyConnect, Fortinet, GRE, VXLAN, PPTP).
5. **Group E — Relays, Overlays & Routing**: Mesh routing, anonymization, and reverse tunnels (Tor, I2P, Yggdrasil, HAProxy, Envoy, FRP, gost).

## 3. Truthful Capability System
Every entry exposes a verified state:
* `SUPPORTED`: Fully implemented, configured, and test-verified client tunnel.
* `PARTIALLY_SUPPORTED`: Verified subset (e.g. SOCKS5 / HTTP Proxy wrapper).
* `AVAILABLE_AS_SERVER_INFRASTRUCTURE`: Configured on gateway/backend nodes, not directly selectable as an Android client engine.
* `REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE`: Enterprise gateways requiring specialized licensed infrastructure.
* `REQUIRES_SPECIALIZED_CLIENT`: Requires dedicated platform client or native daemon.
* `PLANNED`: Validated architectural boundary ready for future engine integration.
* `DEPRECATED_OR_INSECURE`: Legacy protocols prohibited from production connections (e.g. PPTP, MPPE).
