# 100-Technology Feature & Support Matrix

This matrix documents the canonical identity, category, technical role, platform compatibility, and truthful implementation status for all 100 networking technologies.

| # | ID | Technology | Category | Technical Role | Implementation Status |
|:---:|:---|:---|:---|:---|:---|
| 1 | `wireguard` | WireGuard | Group A (VPN Protocol) | Modern Kernel/Userspace Layer 3 VPN | **SUPPORTED** (GoBackend) |
| 2 | `ikev2_ipsec` | IKEv2/IPsec | Group A (VPN Protocol) | Mobile-Optimized Strong Encryption Tunnel | PLANNED |
| 3 | `ipsec` | IPsec | Group A (VPN Protocol) | Layer 3 Security Architecture | PLANNED |
| 4 | `strongswan` | strongSwan | Group A (VPN Platform) | Complete IPsec Implementation | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 5 | `libreswan` | Libreswan | Group A (VPN Platform) | Linux IPsec Daemon | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 6 | `softether` | SoftEther VPN | Group A (VPN Platform) | Multi-Protocol Virtual Network Engine | PLANNED |
| 7 | `sstp` | SSTP | Group A (VPN Protocol) | SSL-Wrapped PPP Tunnel | PLANNED |
| 8 | `l2tp_ipsec` | L2TP/IPsec | Group A (VPN Protocol) | Layer 2 Tunnel Over IPsec | PLANNED |
| 9 | `openconnect` | OpenConnect | Group A (VPN Protocol) | SSL VPN Client (AnyConnect compatible) | PLANNED |
| 10 | `ocserv` | ocserv | Group A (VPN Platform) | OpenConnect SSL VPN Server | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 11 | `tinc` | Tinc | Group A (Mesh Overlay) | Fully Meshed P2P VPN Daemon | REQUIRES_SPECIALIZED_CLIENT |
| 12 | `zerotier` | ZeroTier | Group A (Mesh Overlay) | Global Layer 2 Virtual Ethernet | REQUIRES_SPECIALIZED_CLIENT |
| 13 | `tailscale` | Tailscale | Group A (Mesh Overlay) | WireGuard-based Zero Config Mesh | REQUIRES_SPECIALIZED_CLIENT |
| 14 | `headscale` | Headscale | Group A (Mesh Overlay) | Open Source Tailscale Control Plane | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 15 | `nebula` | Nebula | Group A (Mesh Overlay) | Scalable Overlay Mesh Network | REQUIRES_SPECIALIZED_CLIENT |
| 16 | `netbird` | NetBird | Group A (Mesh Overlay) | WireGuard Zero-Trust Mesh | REQUIRES_SPECIALIZED_CLIENT |
| 17 | `innernet` | Innernet | Group A (Mesh Overlay) | WireGuard-based Access Network | REQUIRES_SPECIALIZED_CLIENT |
| 18 | `netmaker` | Netmaker | Group A (Mesh Overlay) | WireGuard Virtual Mesh Automation | REQUIRES_SPECIALIZED_CLIENT |
| 19 | `firezone` | Firezone | Group A (Mesh Overlay) | WireGuard Zero Trust Access | REQUIRES_SPECIALIZED_CLIENT |
| 20 | `amneziawg` | AmneziaWG | Group A (VPN Protocol) | WireGuard with DPI Obfuscation | REQUIRES_SPECIALIZED_CLIENT |
| 21 | `shadowsocks` | Shadowsocks | Group B (Proxy Protocol) | Fast Asymmetric Encrypted Proxy | **PARTIALLY_SUPPORTED** |
| 22 | `shadowsocks_2022` | Shadowsocks 2022 | Group B (Proxy Protocol) | Modernized AEAD-2022 Proxy Spec | PLANNED |
| 23 | `shadowsocks_r` | ShadowsocksR | Group B (Proxy Protocol) | Obfuscated Shadowsocks Variant | DEPRECATED_OR_INSECURE |
| 24 | `socks5` | SOCKS5 | Group B (Proxy Protocol) | Authenticated TCP/UDP Proxy Protocol | **PARTIALLY_SUPPORTED** |
| 25 | `socks4` | SOCKS4 | Group B (Proxy Protocol) | Legacy Unauthenticated Proxy | DEPRECATED_OR_INSECURE |
| 26 | `http_proxy` | HTTP Proxy | Group B (Proxy Protocol) | Standard Web HTTP Proxy | **PARTIALLY_SUPPORTED** |
| 27 | `https_proxy` | HTTPS Proxy | Group B (Proxy Protocol) | TLS-Encrypted HTTP Proxy (CONNECT) | **PARTIALLY_SUPPORTED** |
| 28 | `naiveproxy` | NaiveProxy | Group B (Proxy Protocol) | Chromium Network Stack Obfuscation | PLANNED |
| 29 | `outline` | Outline | Group B (Proxy Platform) | Shadowsocks-based Managed VPN | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 30 | `brook` | Brook | Group B (Proxy Protocol) | Zero-Config Cross-Platform Proxy | PLANNED |
| 31 | `vless` | VLESS | Group B (Proxy Protocol) | Lightweight Stateless Proxy Protocol | PLANNED |
| 32 | `vless_reality` | VLESS + REALITY | Group B (Proxy Protocol) | Direct TLS Impersonation without Cert | PLANNED |
| 33 | `vmess` | VMess | Group B (Proxy Protocol) | Encrypted Multi-Hop Proxy Protocol | PLANNED |
| 34 | `trojan` | Trojan | Group B (Proxy Protocol) | HTTPS Impersonation Proxy | PLANNED |
| 35 | `trojan_go` | Trojan-Go | Group B (Proxy Protocol) | Multiplexed Trojan Transport | PLANNED |
| 36 | `hysteria` | Hysteria | Group B (Proxy Protocol) | Custom Congestion-Controlled UDP Proxy | PLANNED |
| 37 | `hysteria2` | Hysteria 2 | Group B (Proxy Protocol) | Refined High-Speed QUIC Proxy | PLANNED |
| 38 | `tuic` | TUIC | Group B (Proxy Protocol) | High-Performance QUIC Proxy | PLANNED |
| 39 | `anytls` | AnyTLS | Group B (Proxy Protocol) | Flexible TLS Encapsulation Transport | PLANNED |
| 40 | `mieru` | Mieru | Group B (Proxy Protocol) | Encrypted SOCKS5-Like Proxy | PLANNED |
| 41 | `tcp_tunnel` | TCP Tunneling | Group C (Transport Mechanism) | Raw TCP Stream Tunneling | **SUPPORTED** (OpenVPN TCP) |
| 42 | `udp_tunnel` | UDP Tunneling | Group C (Transport Mechanism) | Datagram Encapsulation Transport | **SUPPORTED** (WireGuard / OpenVPN UDP) |
| 43 | `tls_tunnel` | TLS Tunneling | Group C (Transport Mechanism) | TLS v1.3 Transport Security | **SUPPORTED** |
| 44 | `dtls` | DTLS | Group C (Transport Mechanism) | Datagram TLS Security | PLANNED |
| 45 | `websocket` | WebSocket | Group C (Transport Mechanism) | Full-Duplex Framing Protocol | PLANNED |
| 46 | `wss` | Secure WebSocket (WSS) | Group C (Transport Mechanism) | Encrypted WebSocket over TLS | PLANNED |
| 47 | `stunnel` | stunnel | Group C (Tunnel Tool) | TLS Wrapper for Arbitrary Sockets | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 48 | `wstunnel` | wstunnel | Group C (Tunnel Tool) | Tunneling Traffic through WebSockets | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 49 | `udp2raw` | udp2raw | Group C (Tunnel Tool) | UDP over FakeTCP Tunneling | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 50 | `xray_transport` | Xray Transport System | Group C (Transport System) | Modular Pluggable Transport Router | PLANNED |
| 51 | `http2_transport` | HTTP/2 Transport | Group C (Transport Mechanism) | Multiplexed Binary Stream Framing | PLANNED |
| 52 | `http3_transport` | HTTP/3 Transport | Group C (Transport Mechanism) | QUIC-based Transport Layer | PLANNED |
| 53 | `quic_transport` | QUIC Transport | Group C (Transport Mechanism) | Fast UDP Multiplexed Protocol | PLANNED |
| 54 | `grpc_transport` | gRPC Transport | Group C (Transport Mechanism) | Protocol Buffers over HTTP/2 | PLANNED |
| 55 | `http_upgrade` | HTTP Upgrade | Group C (Transport Mechanism) | Protocol Switching Mechanism | PLANNED |
| 56 | `ws_tunneling` | WebSocket Tunneling | Group C (Transport Mechanism) | Encapsulation via WebSocket Frames | PLANNED |
| 57 | `ssh_port_fwd` | SSH Port Forwarding | Group C (Tunnel Tool) | Local and Remote TCP Port Tunneling | **PARTIALLY_SUPPORTED** |
| 58 | `ssh_socks` | SSH SOCKS Tunnel | Group C (Tunnel Tool) | Dynamic Port Forwarding (-D) | **PARTIALLY_SUPPORTED** |
| 59 | `redsocks` | redsocks | Group C (Tunnel Tool) | Transparent TCP to SOCKS Forwarder | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 60 | `gost` | gost | Group C (Tunnel Tool) | Go Simple Tunneling Tool | PLANNED |
| 61 | `gre` | GRE | Group D (Enterprise/Legacy) | Generic Routing Encapsulation | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 62 | `gretap` | GRETAP | Group D (Enterprise/Legacy) | Ethernet over GRE Tunneling | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 63 | `ip_in_ip` | IP-in-IP | Group D (Enterprise/Legacy) | IP Encapsulation Protocol (IP Protocol 4) | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 64 | `sit_tunnel` | SIT (IPv6-in-IPv4) | Group D (Enterprise/Legacy) | IPv6 Transition Mechanism | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 65 | `vxlan` | VXLAN | Group D (Enterprise/Legacy) | Virtual Extensible LAN Overlay | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 66 | `geneve` | Geneve | Group D (Enterprise/Legacy) | Generic Network Virtualization Encapsulation | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 67 | `geneve_overlay` | GENEVE Overlay | Group D (Enterprise/Legacy) | Overlay Network Fabric | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 68 | `mpls_l3vpn` | MPLS L3VPN | Group D (Enterprise) | BGP/MPLS IP Virtual Private Network | REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE |
| 69 | `dmvpn` | DMVPN | Group D (Enterprise) | Dynamic Multipoint VPN (Cisco) | REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE |
| 70 | `openbsd_isakmpd` | OpenBSD isakmpd/IPsec | Group D (Enterprise) | OpenBSD IKE Daemon Architecture | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 71 | `pptp` | PPTP | Group D (Legacy) | Point-to-Point Tunneling Protocol | DEPRECATED_OR_INSECURE |
| 72 | `mppe` | MPPE | Group D (Legacy) | Microsoft Point-to-Point Encryption | DEPRECATED_OR_INSECURE |
| 73 | `ppp_tunnel` | PPP Tunnel | Group D (Legacy) | Point-to-Point Protocol Encapsulation | DEPRECATED_OR_INSECURE |
| 74 | `pppoe` | PPPoE | Group D (Legacy) | PPP over Ethernet Carrier Access | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 75 | `l2tp` | L2TP | Group D (Legacy) | Layer 2 Tunneling Protocol (without IPsec) | DEPRECATED_OR_INSECURE |
| 76 | `sstp_tunnel` | SSTP-based Tunnel | Group D (Enterprise) | Secure Socket Tunneling Profile | PLANNED |
| 77 | `cisco_anyconnect` | Cisco AnyConnect | Group D (Enterprise) | Proprietary Enterprise SSL Gateway | REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE |
| 78 | `pulse_secure` | Juniper/Pulse Secure | Group D (Enterprise) | Enterprise NAC and SSL Access | REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE |
| 79 | `fortinet_ssl` | Fortinet FortiClient SSL | Group D (Enterprise) | FortiGate SSL VPN Gateway | REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE |
| 80 | `sonicwall_ssl` | SonicWall NetExtender | Group D (Enterprise) | SonicWall Mobile Connect Gateway | REQUIRES_EXTERNAL_ENTERPRISE_INFRASTRUCTURE |
| 81 | `tor` | Tor | Group E (Anonymity/Overlay) | Onion Routing Privacy Network | REQUIRES_SPECIALIZED_CLIENT |
| 82 | `i2p` | I2P | Group E (Anonymity/Overlay) | Invisible Internet Project Garlic Mesh | REQUIRES_SPECIALIZED_CLIENT |
| 83 | `yggdrasil` | Yggdrasil | Group E (Mesh Routing) | End-to-End Encrypted IPv6 Mesh | REQUIRES_SPECIALIZED_CLIENT |
| 84 | `babel_routing` | Babel Routing | Group E (Routing Protocol) | Loop-Free Distance-Vector Mesh Routing | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 85 | `bgp_mpls` | BGP/MPLS Overlay | Group E (Carrier Routing) | Multi-Autonomous System Routing Fabric | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 86 | `openssh_tunnel` | OpenSSH Tunneling | Group E (Tunnel Tool) | Secure Shell TCP/TUN Encapsulation | **PARTIALLY_SUPPORTED** |
| 87 | `haproxy` | HAProxy | Group E (Reverse Proxy/Load Balancer) | High Performance TCP/HTTP Load Balancer | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 88 | `envoy_proxy` | Envoy Proxy | Group E (Service Mesh Proxy) | Cloud-Native High-Performance Edge Proxy | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 89 | `haproxy_proxy_proto` | HAProxy PROXY Protocol | Group E (Proxy Transport) | Client IP Preservation Header | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 90 | `frp_reverse` | FRP Reverse Tunnel | Group E (Reverse Proxy) | Fast Reverse Proxy for Intranet Traversal | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 91 | `frp` | FRP (Fast Reverse Proxy) | Group E (Reverse Proxy) | Multiplexed Intranet Reverse Proxy | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 92 | `chisel` | Chisel | Group E (Tunnel Tool) | Fast TCP/UDP Tunnel over HTTP via SSH | PLANNED |
| 93 | `sshuttle` | SSHuttle | Group E (Proxy/VPN) | Transparent Proxy VPN over SSH | PLANNED |
| 94 | `ssh_tunnel` | SSH Tunnel | Group E (Tunnel Tool) | Standard SSH Remote/Local Forwarding | **PARTIALLY_SUPPORTED** |
| 95 | `socat_relay` | socat Relay | Group E (Relay Tool) | Multipurpose Bidirectional Relay | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 96 | `haproxy_tcp_relay` | HAProxy TCP Relay | Group E (Relay Tool) | Layer 4 TCP Stream Forwarder | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 97 | `nginx_stream_relay` | NGINX Stream Relay | Group E (Relay Tool) | Layer 4 TCP/UDP Stream Multiplexer | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 98 | `dante_socks` | Dante SOCKS Server | Group E (Proxy Daemon) | Circuit-Level SOCKS v4/v5 Proxy Server | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 99 | `threeproxy` | 3proxy | Group E (Proxy Daemon) | Lightweight Tiny Proxy Server | AVAILABLE_AS_SERVER_INFRASTRUCTURE |
| 100 | `gost_reverse` | gost Reverse Proxy/Tunnel | Group E (Tunnel Tool) | Reverse Multi-Hop Tunneling Engine | PLANNED |
