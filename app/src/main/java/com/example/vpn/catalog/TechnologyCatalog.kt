package com.example.vpn.catalog

import androidx.compose.ui.graphics.Color

enum class TechnologyGroup(val title: String, val description: String) {
    VPN_PROTOCOLS("VPN Protocols", "L3/L4 encrypted virtual network tunnels"),
    PROXIES_AND_TRANSPORTS("Proxies & Transports", "Encrypted transport layers and proxy protocols"),
    OBFUSCATION_AND_ANTI_CENSORSHIP("Obfuscation & Anti-Censorship", "DPI evasion, camouflage, and encapsulations"),
    RELAYS_MESH_ANONYMITY("Relays, Mesh & Anonymity", "Decentralized mesh networks and onion routing"),
    TUNNEL_TOOLS_UTILITIES("Tunnel Tools & Utilities", "Port-forwarding, ingress, and universal TLS wrappers")
}

enum class ImplementationStatus(
    val label: String,
    val badgeColor: Long,
    val isActionableInApp: Boolean
) {
    SUPPORTED("Supported (Native Engine)", 0xFF00E5FF, true),
    PARTIALLY_SUPPORTED("Partially Supported", 0xFF64B5F6, true),
    AVAILABLE_AS_SERVER_INFRASTRUCTURE("Server Infrastructure", 0xFF81C784, false),
    REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE("Enterprise Infrastructure", 0xFFFFB74D, false),
    REQUIRES_SPECIALIZED_CLIENT("Specialized Client Required", 0xFFBA68C8, false),
    PLANNED("Roadmap / Planned", 0xFF90A4AE, false),
    DEPRECATED_OR_INSECURE("Deprecated / Insecure", 0xFFE57373, false)
}

data class NetworkTechnology(
    val id: String,
    val name: String,
    val group: TechnologyGroup,
    val status: ImplementationStatus,
    val osiLayer: String,
    val defaultPort: String,
    val description: String,
    val cipherSuite: String,
    val deploymentRole: String
)

object TechnologyCatalog {

    val ALL_100_TECHNOLOGIES: List<NetworkTechnology> = listOf(
        // Group A: VPN Protocols (1-33)
        NetworkTechnology(
            id = "wireguard",
            name = "WireGuard",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 3 (Network)",
            defaultPort = "UDP 51820",
            description = "High-speed, state-of-the-art cryptographic tunnel using Noise protocol framework, ChaCha20-Poly1305, and Curve25519.",
            cipherSuite = "ChaCha20-Poly1305, Curve25519, BLAKE2s",
            deploymentRole = "Primary high-performance client tunnel engine"
        ),
        NetworkTechnology(
            id = "openvpn_udp",
            name = "OpenVPN (UDP)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 3/4",
            defaultPort = "UDP 1194",
            description = "Industry standard SSL/TLS tunnel protocol operating over high-performance UDP datagrams.",
            cipherSuite = "AES-256-GCM, TLS 1.3, SHA-384, RSA 4096",
            deploymentRole = "Secondary protocol with robust CA certificate validation"
        ),
        NetworkTechnology(
            id = "openvpn_tcp",
            name = "OpenVPN (TCP)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 3/4",
            defaultPort = "TCP 443",
            description = "Firewall-bypassing TCP tunnel operating on standard HTTPS port 443 for restrictive networks.",
            cipherSuite = "AES-256-GCM, TLS 1.3, SHA-384, RSA 4096",
            deploymentRole = "Reliable fallback engine for restricted Wi-Fi & firewalls"
        ),
        NetworkTechnology(
            id = "ikev2_ipsec",
            name = "IKEv2 / IPsec",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 3 (Network)",
            defaultPort = "UDP 500, 4500",
            description = "Internet Key Exchange v2 with MOBIKE support for fast cellular-to-Wi-Fi network switching.",
            cipherSuite = "AES-256-GCM, SHA-256, Diffie-Hellman Group 14+",
            deploymentRole = "Planned client adapter for carrier-grade network roaming"
        ),
        NetworkTechnology(
            id = "ipsec_l2tp",
            name = "IPsec / L2TP",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 2/3",
            defaultPort = "UDP 1701, 500",
            description = "Legacy tunneling protocol with double encapsulation; replaced by modern authenticated encryption.",
            cipherSuite = "3DES/AES-128 (Legacy)",
            deploymentRole = "Deprecated legacy protocol"
        ),
        NetworkTechnology(
            id = "ipsec_xauth",
            name = "IPsec / Xauth",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 500",
            description = "Legacy Cisco IPsec phase 1 extended authentication protocol.",
            cipherSuite = "Legacy DES/3DES/AES",
            deploymentRole = "Deprecated enterprise remote access"
        ),
        NetworkTechnology(
            id = "pptp",
            name = "PPTP",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 2",
            defaultPort = "TCP 1723, IP 47 (GRE)",
            description = "Point-to-Point Tunneling Protocol. Known cryptographically broken MS-CHAPv2.",
            cipherSuite = "MPPE 128 (Insecure)",
            deploymentRole = "Disabled for security; insecure obsolete standard"
        ),
        NetworkTechnology(
            id = "sstp",
            name = "SSTP",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "Secure Socket Tunneling Protocol routing PPP traffic through an SSL 3.0/TLS channel over port 443.",
            cipherSuite = "AES-256-CBC, TLS 1.2+",
            deploymentRole = "Planned Microsoft Windows gateway bridge"
        ),
        NetworkTechnology(
            id = "softether_native",
            name = "SoftEther (Native)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 2 (Ethernet)",
            defaultPort = "TCP 443, 992, 1194",
            description = "Multi-protocol server engine simulating Layer 2 Ethernet over HTTPS with ultra-high throughput.",
            cipherSuite = "AES-256-GCM, RC4-MD5 (Custom)",
            deploymentRole = "Multi-protocol server infrastructure backend"
        ),
        NetworkTechnology(
            id = "softether_openvpn",
            name = "SoftEther (OpenVPN Clone)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 3/4",
            defaultPort = "UDP 1194",
            description = "SoftEther's high-speed OpenVPN server implementation compatible with standard OpenVPN clients.",
            cipherSuite = "AES-256-GCM, TLS 1.3",
            deploymentRole = "Compatible with Android OpenVpnProtocolAdapter"
        ),
        NetworkTechnology(
            id = "softether_sstp",
            name = "SoftEther (SSTP Clone)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "SoftEther implementation of the SSTP server protocol for native Windows clients.",
            cipherSuite = "AES-256-GCM, TLS 1.2+",
            deploymentRole = "Server infrastructure module"
        ),
        NetworkTechnology(
            id = "anyconnect_ocserv",
            name = "AnyConnect / OpenConnect (ocserv)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Layer 3/4",
            defaultPort = "TCP 443, UDP 443 (DTLS)",
            description = "OpenConnect SSL VPN server using Cisco AnyConnect protocol with DTLS acceleration.",
            cipherSuite = "AES-256-GCM, TLS 1.3, DTLS 1.2",
            deploymentRole = "Enterprise SSL gateway"
        ),
        NetworkTechnology(
            id = "cisco_ipsec",
            name = "Cisco IPSec",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 500, 4500",
            description = "Proprietary enterprise VPN solution for Cisco ASA and Catalyst gateways.",
            cipherSuite = "AES-256, SHA-256",
            deploymentRole = "Enterprise corporate gateway"
        ),
        NetworkTechnology(
            id = "fortinet_ssl",
            name = "Fortinet SSL VPN",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443, 10443",
            description = "Proprietary SSL VPN tunnel implemented on FortiGate hardware appliances.",
            cipherSuite = "TLS 1.3, AES-256",
            deploymentRole = "Fortinet enterprise appliance connector"
        ),
        NetworkTechnology(
            id = "pulse_secure",
            name = "Pulse Secure / Ivanti Connect",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "Enterprise corporate SSL VPN solution with policy posture assessment.",
            cipherSuite = "TLS 1.3, AES-GCM",
            deploymentRole = "Ivanti enterprise gateway"
        ),
        NetworkTechnology(
            id = "checkpoint_mobile",
            name = "Check Point Mobile Access",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "Check Point gateway SSL VPN for enterprise remote access.",
            cipherSuite = "TLS 1.3, AES-256",
            deploymentRole = "Check Point perimeter appliance connector"
        ),
        NetworkTechnology(
            id = "paloalto_globalprotect",
            name = "Palo Alto GlobalProtect",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 3/4",
            defaultPort = "TCP 443, UDP 4501",
            description = "Palo Alto Networks enterprise next-generation firewall VPN client and portal.",
            cipherSuite = "IPsec ESP / TLS 1.3, AES-256-GCM",
            deploymentRole = "Palo Alto corporate perimeter"
        ),
        NetworkTechnology(
            id = "sonicwall_netextender",
            name = "SonicWall NetExtender",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 4433",
            description = "SonicWall SSL-VPN protocol for secure access to corporate networks.",
            cipherSuite = "TLS 1.2/1.3, AES-256",
            deploymentRole = "SonicWall appliance connection"
        ),
        NetworkTechnology(
            id = "strongswan",
            name = "StrongSwan",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 500, 4500",
            description = "Open source IPsec-based VPN solution for Linux kernel routing.",
            cipherSuite = "IKEv2, ChaCha20-Poly1305, AES-GCM",
            deploymentRole = "VPS server IPsec termination gateway"
        ),
        NetworkTechnology(
            id = "libreswan",
            name = "Libreswan",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 500, 4500",
            description = "Open source IPsec implementation maintaining RFC-compliant internet key exchange.",
            cipherSuite = "IKEv1/IKEv2, AES-GCM",
            deploymentRole = "Linux server IPsec daemon"
        ),
        NetworkTechnology(
            id = "tinc_vpn",
            name = "tinc VPN",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 2/3",
            defaultPort = "TCP/UDP 655",
            description = "Automatic full mesh peer-to-peer virtual private network daemon.",
            cipherSuite = "RSA/ECDSA, AES-256-CBC / GCM",
            deploymentRole = "Planned peer-to-peer mesh connector"
        ),
        NetworkTechnology(
            id = "zerotier",
            name = "ZeroTier",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Layer 2 (Ethernet)",
            defaultPort = "UDP 9993",
            description = "Software-defined global virtual Ethernet switch and mesh network.",
            cipherSuite = "Salsa20/12, Poly1305, Curve25519",
            deploymentRole = "Virtual LAN virtualization engine"
        ),
        NetworkTechnology(
            id = "tailscale",
            name = "Tailscale (WireGuard Mesh)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 41641 / DERP",
            description = "Zero-config WireGuard mesh network orchestrated through a coordination server.",
            cipherSuite = "WireGuard (ChaCha20-Poly1305, Curve25519)",
            deploymentRole = "Server inter-cluster interconnect"
        ),
        NetworkTechnology(
            id = "nebula_slack",
            name = "Nebula (Slack)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 4242",
            description = "Mutually authenticated overlay mesh network designed for scalable cloud infrastructure.",
            cipherSuite = "AES-256-GCM / ChaCha20-Poly1305, Noise Protocol",
            deploymentRole = "Private gateway mesh overlay"
        ),
        NetworkTechnology(
            id = "innernet",
            name = "Innernet",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 51820",
            description = "WireGuard-based internal network management system creating peer-to-peer CIDR trees.",
            cipherSuite = "WireGuard Protocol",
            deploymentRole = "CIDR subnet management daemon"
        ),
        NetworkTechnology(
            id = "netmaker",
            name = "Netmaker",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "UDP 51821-51830",
            description = "Automated high-speed WireGuard overlay network platform for Kubernetes and clouds.",
            cipherSuite = "Kernel WireGuard",
            deploymentRole = "Cloud gateway management framework"
        ),
        NetworkTechnology(
            id = "rosenpass",
            name = "Rosenpass",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 3",
            defaultPort = "UDP 9999",
            description = "Post-quantum secure key exchange protocol for WireGuard using Classic McEliece.",
            cipherSuite = "Classic McEliece, Kyber-512, WireGuard PSK",
            deploymentRole = "Experimental post-quantum key agreement"
        ),
        NetworkTechnology(
            id = "fastd",
            name = "fastd",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 2/3",
            defaultPort = "UDP 10000",
            description = "Fast and Secure Tunneling Daemon optimized for community and mesh routers.",
            cipherSuite = "Salsa2012+umac, null",
            deploymentRole = "Freifunk and lightweight router gateway"
        ),
        NetworkTechnology(
            id = "badvpn",
            name = "BadVPN (tun2socks)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 3 to 4",
            defaultPort = "Internal Loopback",
            description = "Virtual network interface packet forwarder translating IP packets into TCP/UDP SOCKS5 connections.",
            cipherSuite = "Host Cipher Pass-through",
            deploymentRole = "Android packet bridge for proxy fallbacks"
        ),
        NetworkTechnology(
            id = "peervpn",
            name = "PeerVPN",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 2",
            defaultPort = "UDP 7000",
            description = "Open source, decentralized, peer-to-peer virtual private network with full mesh capability.",
            cipherSuite = "AES-256-CBC, SHA-256",
            deploymentRole = "Decentralized mesh tunnel"
        ),
        NetworkTechnology(
            id = "fretless_n2n",
            name = "n2n (ntop)",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 2",
            defaultPort = "UDP 7654",
            description = "Peer-to-peer Layer 2 VPN software allowing users to exploit features usually only found on L2 switches.",
            cipherSuite = "Twofish, AES-CBC, ChaCha20",
            deploymentRole = "Layer 2 peer-to-peer bridge"
        ),
        NetworkTechnology(
            id = "opennhrp_dmvpn",
            name = "OpenNHRP / DMVPN",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 3",
            defaultPort = "GRE / IPsec",
            description = "Dynamic Multipoint VPN implementation using Next Hop Resolution Protocol.",
            cipherSuite = "IPsec ESP, AES-256",
            deploymentRole = "Enterprise hub-and-spoke dynamic routing"
        ),
        NetworkTechnology(
            id = "comi_dtls",
            name = "CoMI over DTLS",
            group = TechnologyGroup.VPN_PROTOCOLS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 4/7",
            defaultPort = "UDP 5684",
            description = "IETF CoAP Management Interface constrained application protocol secured by Datagram TLS.",
            cipherSuite = "DTLS 1.2/1.3, AES-CCM-8",
            deploymentRole = "Ultra-low power IoT tunnel"
        ),

        // Group B: Proxies and Encrypted Transports (34-59)
        NetworkTechnology(
            id = "shadowsocks",
            name = "Shadowsocks (SS)",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4/5 (Transport)",
            defaultPort = "TCP/UDP 8388",
            description = "Fast, lightweight encrypted SOCKS5 proxy protocol designed to protect internet traffic.",
            cipherSuite = "AES-256-CFB (Legacy)",
            deploymentRole = "Encrypted proxy transport"
        ),
        NetworkTechnology(
            id = "shadowsocks_aead",
            name = "Shadowsocks-AEAD",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP/UDP 8388",
            description = "Modern Shadowsocks specification utilizing Authenticated Encryption with Associated Data.",
            cipherSuite = "AEAD_CHACHA20_POLY1305, AEAD_AES_256_GCM",
            deploymentRole = "Anti-replay secure proxy transport"
        ),
        NetworkTechnology(
            id = "shadowsocks_2022",
            name = "Shadowsocks 2022",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP/UDP 8388",
            description = "Latest standard with deterministic header sizing, blake3 subkey derivation, and session tracking.",
            cipherSuite = "2022-blake3-aes-256-gcm, 2022-blake3-chacha20-poly1305",
            deploymentRole = "Next-gen DPI-resistant transport"
        ),
        NetworkTechnology(
            id = "shadowsocksr",
            name = "ShadowsocksR (SSR)",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP/UDP 8388",
            description = "Shadowsocks fork adding obfuscation plugins. Largely superseded by V2Ray and Shadowsocks-AEAD.",
            cipherSuite = "AES-256-CFB, auth_aes128_md5",
            deploymentRole = "Deprecated proxy variant"
        ),
        NetworkTechnology(
            id = "vmess",
            name = "VMess",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4/7",
            defaultPort = "TCP 443",
            description = "V2Ray original encrypted transport protocol using UUID user identification and dynamic timestamps.",
            cipherSuite = "AES-128-GCM, ChaCha20-Poly1305",
            deploymentRole = "Anti-censorship overlay transport"
        ),
        NetworkTechnology(
            id = "vless",
            name = "VLESS",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4/7",
            defaultPort = "TCP 443",
            description = "Lightweight, zero-encryption transport protocol relying entirely on outer TLS/REALITY for protection.",
            cipherSuite = "Passthrough to TLS 1.3 / REALITY",
            deploymentRole = "High-efficiency TLS transport"
        ),
        NetworkTechnology(
            id = "trojan",
            name = "Trojan",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 (Application)",
            defaultPort = "TCP 443",
            description = "Camouflaged proxy protocol masquerading as standard HTTPS web traffic to legitimate web servers.",
            cipherSuite = "Standard TLS 1.3 / OpenSSL",
            deploymentRole = "HTTPS disguise proxy"
        ),
        NetworkTechnology(
            id = "trojan_go",
            name = "Trojan-Go",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7",
            defaultPort = "TCP 443",
            description = "Trojan implementation in Go supporting multiplexing, WebSocket transport, and AEAD encryption.",
            cipherSuite = "TLS 1.3, smux, WebSocket",
            deploymentRole = "Multiplexed stealth proxy"
        ),
        NetworkTechnology(
            id = "snell",
            name = "Snell",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "Proprietary encrypted proxy protocol created for Surge iOS/macOS with minimal overhead.",
            cipherSuite = "ChaCha20-Poly1305 / AES-128-GCM",
            deploymentRole = "Surge ecosystem proxy"
        ),
        NetworkTechnology(
            id = "naiveproxy",
            name = "NaiveProxy",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 7",
            defaultPort = "TCP 443",
            description = "Uses Chrome network stack to tunnel traffic through standard HTTP/2 or HTTP/3 HTTPS proxies.",
            cipherSuite = "BoringSSL TLS 1.3 / QUIC",
            deploymentRole = "Zero-fingerprint Chromium transport"
        ),
        NetworkTechnology(
            id = "hysteria",
            name = "Hysteria",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4 (UDP)",
            defaultPort = "UDP 443",
            description = "Feature-packed proxy optimized for lossy and congested network links using custom QUIC congestion control.",
            cipherSuite = "TLS 1.3 over QUIC",
            deploymentRole = "High-loss network accelerator"
        ),
        NetworkTechnology(
            id = "hysteria_2",
            name = "Hysteria 2",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4 (UDP)",
            defaultPort = "UDP 443",
            description = "Complete rewrite of Hysteria with native port hopping, Brutal congestion control, and DPI evasion.",
            cipherSuite = "TLS 1.3 over custom UDP/QUIC protocol",
            deploymentRole = "Next-gen UDP accelerator and bypass"
        ),
        NetworkTechnology(
            id = "tuic",
            name = "TUIC (v4 / v5)",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 4 (UDP)",
            defaultPort = "UDP 443",
            description = "High-performance proxy protocol built on top of native quiche/QUIC with 0-RTT handshakes.",
            cipherSuite = "QUIC / TLS 1.3",
            deploymentRole = "Zero round-trip latency transport"
        ),
        NetworkTechnology(
            id = "xray_core",
            name = "Xray-core",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Multi-Layer Framework",
            defaultPort = "Configurable",
            description = "Superset of V2Ray providing XTLS, REALITY, VLESS, and advanced routing engines.",
            cipherSuite = "Modular (TLS 1.3, REALITY, AEAD)",
            deploymentRole = "Server-side multi-protocol proxy engine"
        ),
        NetworkTechnology(
            id = "v2ray_core",
            name = "V2Ray-core",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Multi-Layer Framework",
            defaultPort = "Configurable",
            description = "Modular platform for building customized network proxies with complex routing rules.",
            cipherSuite = "Modular (VMess, VLESS, SOCKS5)",
            deploymentRole = "Server proxy daemon"
        ),
        NetworkTechnology(
            id = "sing_box",
            name = "Sing-box",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Multi-Layer Framework",
            defaultPort = "Configurable",
            description = "Universal proxy platform in Go supporting WireGuard, ShadowTLS, Hysteria 2, and REALITY.",
            cipherSuite = "All modern proxy ciphers",
            deploymentRole = "Universal gateway routing daemon"
        ),
        NetworkTechnology(
            id = "clash_meta",
            name = "Clash / Mihomo",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Rule-based Proxy Engine",
            defaultPort = "TCP 7890",
            description = "Rule-based tunnel core supporting outbound groups, fallbacks, and health checking.",
            cipherSuite = "Modular client ciphers",
            deploymentRole = "Rule-based proxy orchestration"
        ),
        NetworkTechnology(
            id = "brook",
            name = "Brook",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP/UDP 9999",
            description = "Cross-platform strong encryption proxy protocol designed for simplicity and zero setup.",
            cipherSuite = "AES-256-GCM / ChaCha20",
            deploymentRole = "Simple cross-platform proxy"
        ),
        NetworkTechnology(
            id = "gost",
            name = "Gost (GO Simple Tunnel)",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Multi-Layer Tunnel",
            defaultPort = "Configurable",
            description = "Multi-node tunnel proxy capable of chaining SOCKS5, HTTP, TLS, KCP, and Shadowsocks.",
            cipherSuite = "Configurable TLS, AEAD",
            deploymentRole = "Tunnel forwarder and node chainer"
        ),
        NetworkTechnology(
            id = "socks4",
            name = "SOCKS4",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 5 (Session)",
            defaultPort = "TCP 1080",
            description = "Unencrypted legacy proxy protocol with no authentication and no IPv6 or UDP support.",
            cipherSuite = "None (Plaintext)",
            deploymentRole = "Obsolete plaintext protocol"
        ),
        NetworkTechnology(
            id = "socks5",
            name = "SOCKS5",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 5 (Session)",
            defaultPort = "TCP 1080",
            description = "Standard circuit-level proxy protocol supporting IPv4, IPv6, UDP relay, and username/password authentication.",
            cipherSuite = "Plaintext / Tunnel-Encapsulated",
            deploymentRole = "Loopback proxy bridge inside client"
        ),
        NetworkTechnology(
            id = "socks5_tls",
            name = "SOCKS5 over TLS",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 5 + 6",
            defaultPort = "TCP 1081",
            description = "SOCKS5 protocol wrapped within TLS encryption for confidentiality across public networks.",
            cipherSuite = "TLS 1.3 (AES-256-GCM / ChaCha20)",
            deploymentRole = "Encrypted application proxy"
        ),
        NetworkTechnology(
            id = "http_connect",
            name = "HTTP CONNECT Proxy",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 (Application)",
            defaultPort = "TCP 8080",
            description = "HTTP proxy tunnel establishing a raw TCP byte stream to destination ports via HTTP CONNECT method.",
            cipherSuite = "None (Plaintext headers)",
            deploymentRole = "Forward proxy transport"
        ),
        NetworkTechnology(
            id = "https_proxy",
            name = "HTTPS (Secure) Proxy",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.SUPPORTED,
            osiLayer = "Layer 7 (Application)",
            defaultPort = "TCP 443, 8443",
            description = "HTTP CONNECT proxy encapsulated entirely within an outer TLS connection.",
            cipherSuite = "TLS 1.3, AES-GCM",
            deploymentRole = "Secure enterprise egress proxy"
        ),
        NetworkTechnology(
            id = "http2_proxy",
            name = "HTTP/2 Proxy",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 (Application)",
            defaultPort = "TCP 443",
            description = "Multiplexed secure forward proxy over HTTP/2 avoiding head-of-line blocking.",
            cipherSuite = "TLS 1.3, ALPN h2",
            deploymentRole = "Multiplexed proxy stream"
        ),
        NetworkTechnology(
            id = "http3_quic_proxy",
            name = "HTTP/3 (QUIC) Proxy",
            group = TechnologyGroup.PROXIES_AND_TRANSPORTS,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 over UDP",
            defaultPort = "UDP 443",
            description = "Modern forward proxy running over QUIC UDP transport for rapid loss recovery.",
            cipherSuite = "QUIC / TLS 1.3, ALPN h3",
            deploymentRole = "Loss-resistant edge proxy"
        ),

        // Group C: Obfuscation and Anti-Censorship (60-79)
        NetworkTechnology(
            id = "obfsproxy",
            name = "Obfsproxy (obfs4)",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/5 (Obfuscation)",
            defaultPort = "TCP Configurable",
            description = "Pluggable transport making network flows look random and resistant to active probing.",
            cipherSuite = "Curve25519, Elligator2, HMAC-SHA256",
            deploymentRole = "Tor pluggable transport bridge"
        ),
        NetworkTechnology(
            id = "meek",
            name = "Meek (Domain Fronting)",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 7 (Application)",
            defaultPort = "TCP 443",
            description = "Uses mismatched SNI and HTTP Host headers via CDNs to bypass state-level censors.",
            cipherSuite = "Standard CDN TLS 1.3",
            deploymentRole = "Anti-censorship CDN fronting"
        ),
        NetworkTechnology(
            id = "snowflake",
            name = "Snowflake",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.PLANNED,
            osiLayer = "WebRTC (UDP)",
            defaultPort = "WebRTC Dynamic",
            description = "Tor pluggable transport routing traffic through temporary volunteer WebRTC proxies.",
            cipherSuite = "DTLS-SRTP / WebRTC",
            deploymentRole = "Planned ephemeral peer proxy"
        ),
        NetworkTechnology(
            id = "scramblesuit",
            name = "ScrambleSuit",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP Configurable",
            description = "Early polymorphic network flow obfuscator. Predecessor to obfs4.",
            cipherSuite = "UniformDH, AES-128-CTR",
            deploymentRole = "Superseded by obfs4"
        ),
        NetworkTechnology(
            id = "cloak",
            name = "Cloak",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "Cryptographic obfuscator disguising proxy traffic as web browsing to real websites.",
            cipherSuite = "ECDH, ChaCha20-Poly1305",
            deploymentRole = "Stealth TLS camouflage plugin"
        ),
        NetworkTechnology(
            id = "shadowtls",
            name = "ShadowTLS",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/5",
            defaultPort = "TCP 443",
            description = "Proxies the TLS handshake to legitimate websites during probing, while intercepting validated client traffic.",
            cipherSuite = "Target site TLS / ChaCha20",
            deploymentRole = "Active-probing defense gateway"
        ),
        NetworkTechnology(
            id = "v2ray_ws_tls",
            name = "V2Ray WebSocket + TLS",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 (Application)",
            defaultPort = "TCP 443",
            description = "Tunnels VMess or VLESS over standard WebSockets with TLS, routable through Cloudflare/Nginx reverse proxies.",
            cipherSuite = "TLS 1.3, WSS",
            deploymentRole = "Reverse-proxy compatible bypass"
        ),
        NetworkTechnology(
            id = "v2ray_grpc_tls",
            name = "V2Ray gRPC + TLS",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 (HTTP/2)",
            defaultPort = "TCP 443",
            description = "High-performance gRPC multiplexed transport over TLS camouflaged as standard API traffic.",
            cipherSuite = "TLS 1.3, HTTP/2 gRPC",
            deploymentRole = "Enterprise API mimicry transport"
        ),
        NetworkTechnology(
            id = "trojan_ws_tls",
            name = "Trojan + WebSocket + TLS",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7",
            defaultPort = "TCP 443",
            description = "Trojan protocol encapsulated in WebSockets allowing multi-path CDN routing and standard reverse proxies.",
            cipherSuite = "TLS 1.3, WSS",
            deploymentRole = "CDN-routable camouflage tunnel"
        ),
        NetworkTechnology(
            id = "reality_vless",
            name = "VLESS-REALITY",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/7",
            defaultPort = "TCP 443",
            description = "Eliminates need for server TLS certificates by borrowing public certificates from arbitrary HTTPS domains.",
            cipherSuite = "Borrowing Target TLS 1.3",
            deploymentRole = "Certificate-less DPI evasion"
        ),
        NetworkTechnology(
            id = "plain_http_obfs",
            name = "PlainHTTP Obfuscator",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Layer 7",
            defaultPort = "TCP 80",
            description = "Wraps traffic in mock HTTP GET/POST headers. Vulnerable to deep packet inspection.",
            cipherSuite = "None (Obsolete)",
            deploymentRole = "Deprecated DPI circumvention"
        ),
        NetworkTechnology(
            id = "tls_sni_mux",
            name = "TLS SNI Mux / FakeTLS",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 5/6",
            defaultPort = "TCP 443",
            description = "Injects benign Server Name Indication values into TLS Client Hello frames.",
            cipherSuite = "TLS 1.3 Handshake Mux",
            deploymentRole = "Firewall SNI spoofing plugin"
        ),
        NetworkTechnology(
            id = "dns_tunneling",
            name = "DNS Tunneling (iodine, dnscat2)",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 7 (DNS)",
            defaultPort = "UDP 53",
            description = "Encapsulates IP packets or command shells into DNS TXT/NULL queries to escape captive portals.",
            cipherSuite = "Custom Base32/Base64, AES",
            deploymentRole = "Captive portal emergency bypass"
        ),
        NetworkTechnology(
            id = "icmp_tunneling",
            name = "ICMP Tunneling (ptunnel, Hans)",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3 (ICMP)",
            defaultPort = "ICMP Echo",
            description = "Transfers arbitrary IP packets inside ICMP Echo Request and Reply payloads.",
            cipherSuite = "Blowfish / Custom",
            deploymentRole = "Emergency restricted network bypass"
        ),
        NetworkTechnology(
            id = "ip_in_ip",
            name = "IP over IP (IPIP)",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3 (IP)",
            defaultPort = "IP Protocol 4",
            description = "Linux kernel network protocol encapsulating one IP packet inside another.",
            cipherSuite = "Plaintext (Kernel Encapsulation)",
            deploymentRole = "Server inter-router tunneling"
        ),
        NetworkTechnology(
            id = "gre_tunneling",
            name = "GRE Encapsulation",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3 (GRE)",
            defaultPort = "IP Protocol 47",
            description = "Generic Routing Encapsulation protocol capable of wrapping a wide variety of network protocols.",
            cipherSuite = "Unencrypted (Requires IPsec)",
            deploymentRole = "Carrier router point-to-point trunk"
        ),
        NetworkTechnology(
            id = "sit_tunneling",
            name = "SIT (IPv6 in IPv4)",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3 (IP)",
            defaultPort = "IP Protocol 41",
            description = "Simple Internet Transition tunneling IPv6 packets over legacy IPv4 networks.",
            cipherSuite = "Plaintext Transition Protocol",
            deploymentRole = "Dual-stack IPv6 gateway transit"
        ),
        NetworkTechnology(
            id = "geneve_overlay",
            name = "Geneve Overlay",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 3 over UDP",
            defaultPort = "UDP 6081",
            description = "Generic Network Virtualization Encapsulation providing extensible metadata options in cloud datacenters.",
            cipherSuite = "Datacenter Overlay Encapsulation",
            deploymentRole = "Server virtualization fabric"
        ),
        NetworkTechnology(
            id = "vxlan",
            name = "VXLAN",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 2 over UDP",
            defaultPort = "UDP 4789",
            description = "Virtual Extensible LAN encapsulating Layer 2 Ethernet frames within UDP packets.",
            cipherSuite = "Underlay UDP Encapsulation",
            deploymentRole = "Cloud datacenter SDN fabric"
        ),
        NetworkTechnology(
            id = "stun_turn_tunnel",
            name = "STUN / TURN Tunneling",
            group = TechnologyGroup.OBFUSCATION_AND_ANTI_CENSORSHIP,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4 (UDP/TCP)",
            defaultPort = "UDP 3478, 5349",
            description = "Traversal Using Relays around NAT (TURN) providing media and data relay across symmetric NAT firewalls.",
            cipherSuite = "TLS / DTLS / HMAC-SHA1",
            deploymentRole = "NAT traversal gateway relay"
        ),

        // Group D: Relays, Mesh, and Anonymity (80-89)
        NetworkTechnology(
            id = "tor_onion",
            name = "Tor (Onion Routing)",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Layer 7 (Multi-hop Overlay)",
            defaultPort = "TCP 9001, 9050",
            description = "Decentralized anonymity network routing traffic through three volunteer nodes with layered encryption.",
            cipherSuite = "Curve25519, Ed25519, AES-CTR, SHA-256",
            deploymentRole = "Decentralized anonymity overlay"
        ),
        NetworkTechnology(
            id = "i2p_network",
            name = "I2P (Invisible Internet Project)",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Layer 4/7 (Garlic Routing)",
            defaultPort = "TCP/UDP Dynamic",
            description = "Anonymizing peer-to-peer distributed communication network using garlic encryption and unidirectional tunnels.",
            cipherSuite = "ElGamal, AES-256-CBC, Ed25519",
            deploymentRole = "Decentralized garlic-routed darknet"
        ),
        NetworkTechnology(
            id = "freenet_hyphanet",
            name = "Freenet / Hyphanet",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Distributed Storage Mesh",
            defaultPort = "UDP Dynamic",
            description = "Peer-to-peer platform for censorship-resistant communication and distributed data publication.",
            cipherSuite = "AES-256, RSA 2048",
            deploymentRole = "Decentralized anonymous datastore"
        ),
        NetworkTechnology(
            id = "lokinet",
            name = "Lokinet (LLARP / Oxen)",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 3 (Onion Routing)",
            defaultPort = "UDP Dynamic",
            description = "Low-latency onion routing protocol operating at Layer 3 to route real-time voice, video, and gaming traffic.",
            cipherSuite = "XChaCha20-Poly1305, Ed25519",
            deploymentRole = "Planned Layer 3 onion router"
        ),
        NetworkTechnology(
            id = "yggdrasil",
            name = "Yggdrasil Network",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 3 (Global Mesh)",
            defaultPort = "TCP/UDP Dynamic",
            description = "End-to-end encrypted IPv6 mesh network using spanning trees to assign routable cryptographic addresses.",
            cipherSuite = "X25519, Poly1305, Ed25519",
            deploymentRole = "Cryptographic IPv6 mesh network"
        ),
        NetworkTechnology(
            id = "cjdns",
            name = "Cjdns",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.PLANNED,
            osiLayer = "Layer 3 (IPv6 Mesh)",
            defaultPort = "UDP Dynamic",
            description = "Routing protocol that uses public-key cryptography for network address allocation and packet routing.",
            cipherSuite = "Salsa20, Poly1305, Curve25519",
            deploymentRole = "Decentralized encrypted mesh"
        ),
        NetworkTechnology(
            id = "retroshare",
            name = "RetroShare",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Friend-to-Friend Mesh",
            defaultPort = "TCP/UDP Dynamic",
            description = "Decentralized, encrypted friend-to-friend communications network using GnuPG keys.",
            cipherSuite = "OpenPGP, TLS 1.3, AES-256",
            deploymentRole = "F2F encrypted communications network"
        ),
        NetworkTechnology(
            id = "gnunet",
            name = "GNUnet",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.REQUIRES_SPECIALIZED_CLIENT,
            osiLayer = "Autonomous Stack",
            defaultPort = "Configurable",
            description = "GNU Project framework for secure peer-to-peer networking focusing on censorship resistance and privacy.",
            cipherSuite = "ECDH, EdDSA, AES-256",
            deploymentRole = "Privacy-focused software framework"
        ),
        NetworkTechnology(
            id = "socks_chaining",
            name = "Anonymizing SOCKS Chain",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 5 Chaining",
            defaultPort = "Chained Ports",
            description = "Sequential chaining of multiple distinct SOCKS proxies to eliminate single points of egress trust.",
            cipherSuite = "Multi-hop Nested TLS",
            deploymentRole = "Multi-hop proxy cascade"
        ),
        NetworkTechnology(
            id = "mixminion",
            name = "Mixminion / Mixmaster",
            group = TechnologyGroup.RELAYS_MESH_ANONYMITY,
            status = ImplementationStatus.DEPRECATED_OR_INSECURE,
            osiLayer = "Type II/III Mix Network",
            defaultPort = "TCP 48099",
            description = "Anonymous remailer protocol using batching and pooling to foil traffic analysis. High latency, asynchronous.",
            cipherSuite = "RSA 2048, Triple-DES / AES",
            deploymentRole = "Historical batching mixnet"
        ),

        // Group E: Tunnel Tools and Utilities (90-100)
        NetworkTechnology(
            id = "ssh_tunnel_socks",
            name = "SSH Tunnel (Dynamic SOCKS)",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 over SSH",
            defaultPort = "TCP 22",
            description = "OpenSSH dynamic port forwarding creating a local SOCKS5 proxy tunneled through an encrypted SSH session.",
            cipherSuite = "ChaCha20-Poly1305, OpenSSH KEX Curve25519",
            deploymentRole = "Ad-hoc encrypted secure proxy"
        ),
        NetworkTechnology(
            id = "ssh_port_forward",
            name = "SSH Local/Remote Forwarding",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.PARTIALLY_SUPPORTED,
            osiLayer = "Layer 7 over SSH",
            defaultPort = "TCP 22",
            description = "Port mapping tunneling specific local ports to remote destinations over standard SSH connections.",
            cipherSuite = "SSH Protocol 2, AES-GCM",
            deploymentRole = "Specific service port forwarder"
        ),
        NetworkTechnology(
            id = "chisel_tunnel",
            name = "Chisel (HTTP/WebSockets)",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4 over HTTP/WS",
            defaultPort = "TCP 8080",
            description = "Fast TCP/UDP tunnel transported over HTTP and secured via SSH, perfect for traversing firewalls.",
            cipherSuite = "SSH Protocol over WebSockets / TLS",
            deploymentRole = "Firewall-piercing reverse tunnel"
        ),
        NetworkTechnology(
            id = "stunnel",
            name = "stunnel (TLS Wrapper)",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 6 (Presentation)",
            defaultPort = "Configurable",
            description = "Universal proxy designed to add TLS encryption to arbitrary existing TCP daemons without code modification.",
            cipherSuite = "OpenSSL TLS 1.3 / FIPS compliant",
            deploymentRole = "Server-side TLS encryption wrapper"
        ),
        NetworkTechnology(
            id = "wstunnel",
            name = "wstunnel",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4 over WebSocket",
            defaultPort = "TCP 443",
            description = "Tunnels all arbitrary TCP or UDP traffic over WebSocket connections to bypass deep packet inspection.",
            cipherSuite = "TLS 1.3 over WSS",
            deploymentRole = "WebSocket transport tunnel"
        ),
        NetworkTechnology(
            id = "pagekite",
            name = "PageKite",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 7 (Reverse Tunnel)",
            defaultPort = "TCP 443, 80",
            description = "Open source reverse-proxy tunnel connecting internal servers to the public internet.",
            cipherSuite = "TLS 1.3, SHA-256",
            deploymentRole = "Localhost public exposure tool"
        ),
        NetworkTechnology(
            id = "ngrok_tunnel",
            name = "ngrok Reverse Tunnel",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 4/7",
            defaultPort = "TCP 443",
            description = "Managed reverse tunnel ingress service providing public URLs to internal services.",
            cipherSuite = "TLS 1.3 / Managed Ingress",
            deploymentRole = "Managed developer ingress"
        ),
        NetworkTechnology(
            id = "frp_tunnel",
            name = "frp (Fast Reverse Proxy)",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/7",
            defaultPort = "TCP 7000",
            description = "High-performance reverse proxy to expose NAT/firewalled local servers to the internet using TCP/UDP/HTTP.",
            cipherSuite = "Custom token authentication + TLS",
            deploymentRole = "Self-hosted reverse proxy gateway"
        ),
        NetworkTechnology(
            id = "rathole",
            name = "Rathole",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4",
            defaultPort = "TCP 2333",
            description = "Lightweight, ultra-fast reverse proxy written in Rust with minimal memory footprint and high concurrency.",
            cipherSuite = "Noise Protocol (NK, KK)",
            deploymentRole = "High-throughput Rust reverse tunnel"
        ),
        NetworkTechnology(
            id = "cloudflare_tunnel",
            name = "Cloudflare Tunnel (cloudflared)",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE,
            osiLayer = "Layer 4/7 (Anycast)",
            defaultPort = "TCP/UDP 7844 (QUIC)",
            description = "Creates outbound-only connections to Cloudflare's global anycast edge without opening public server ports.",
            cipherSuite = "Cloudflare Edge TLS 1.3 / QUIC",
            deploymentRole = "Anycast edge ingress connector"
        ),
        NetworkTechnology(
            id = "haproxy_proxy",
            name = "HAProxy TCP/TLS Proxy",
            group = TechnologyGroup.TUNNEL_TOOLS_UTILITIES,
            status = ImplementationStatus.AVAILABLE_AS_SERVER_INFRASTRUCTURE,
            osiLayer = "Layer 4/7",
            defaultPort = "TCP 443, 80",
            description = "High-availability, ultra-high-performance load balancer and reverse proxy for enterprise TLS termination.",
            cipherSuite = "OpenSSL TLS 1.3, ALPN, PROXY protocol v2",
            deploymentRole = "VPS entry gateway load balancer"
        )
    )

    fun getTechnologiesByGroup(group: TechnologyGroup): List<NetworkTechnology> {
        return ALL_100_TECHNOLOGIES.filter { it.group == group }
    }

    fun getSupportedProtocols(): List<NetworkTechnology> {
        return ALL_100_TECHNOLOGIES.filter { it.status == ImplementationStatus.SUPPORTED }
    }

    fun searchTechnologies(query: String): List<NetworkTechnology> {
        if (query.isBlank()) return ALL_100_TECHNOLOGIES
        val clean = query.trim().lowercase()
        return ALL_100_TECHNOLOGIES.filter {
            it.name.lowercase().contains(clean) ||
                    it.description.lowercase().contains(clean) ||
                    it.cipherSuite.lowercase().contains(clean) ||
                    it.defaultPort.lowercase().contains(clean) ||
                    it.id.lowercase().contains(clean)
        }
    }
}
