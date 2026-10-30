# Production Multi-Protocol Django VPN Backend

A commercial-grade, multi-protocol VPN control plane built on **Django REST Framework**, **PostgreSQL**, and **Redis**, managing WireGuard and OpenVPN dual-transport gateways.

---

## Architecture Overview

```
                      ┌──────────────────────────────────────────────┐
                      │          Django REST API Master              │
                      │  - /api/v1/auth/                             │
                      │  - /api/v1/me/ (Profile & Devices)           │
                      │  - /api/v1/servers/                          │
                      │  - /api/v1/vpn/sessions/                     │
                      │  - /api/v1/diagnostics/                      │
                      └──────────────────────┬───────────────────────┘
                                             │ HMAC-Signed Sync
                      ┌──────────────────────┴───────────────────────┐
                      │         VPS Gateway Nodes (Any Linux)        │
                      │  - Node Agent (`agent/node_agent.py`)        │
                      │  - WireGuard Engine (`wg0` UDP 51820)        │
                      │  - OpenVPN UDP Engine (`tun0` UDP 1194)      │
                      │  - OpenVPN TCP Engine (`tun1` TCP 443)       │
                      │  - Dual-Stack NAT Masquerade (IPv4 + IPv6)   │
                      └──────────────────────────────────────────────┘
```

---

## 1. Quick Start: Deploying the Control Plane via Docker

Run the master Django control plane, PostgreSQL database, and Redis broker with one command:

```bash
cd django_vpn_backend
docker compose up -d --build
```

Access the Django API at:
* Swagger / Endpoints: `http://localhost:8000/api/v1/servers/`
* Admin Dashboard: `http://localhost:8000/admin/`

To create an administrator account:
```bash
docker compose exec web python manage.py createsuperuser
```

---

## 2. Deploying a VPS Gateway Node (WireGuard + OpenVPN)

On any fresh Ubuntu (22.04 / 24.04 LTS) or Debian (12) VPS instance:

```bash
chmod +x setup_vpn_node.sh
sudo ./setup_vpn_node.sh
```

This automated script performs:
1. Installs official WireGuard kernel modules, OpenVPN 2.6, Easy-RSA, and iptables.
2. Generates server Curve25519 keypair and creates `wg0` interface listening on **UDP 51820**.
3. Initializes an Easy-RSA PKI and launches **OpenVPN UDP (1194)** and **OpenVPN TCP (443)** services.
4. Enables kernel IP forwarding (`net.ipv4.ip_forward=1` and `net.ipv6.conf.all.forwarding=1`).
5. Configures zero-leak NAT masquerade rules in iptables and ip6tables.

---

## 3. Connecting the Android App

1. Launch the Android VPN client.
2. Open **Settings** (or tap the Menu/Cloud icon).
3. Set the API Endpoint to your Django server URL:
   * **Android Emulator**: `http://10.0.2.2:8000/api/v1/`
   * **Self-Hosted VPS**: `https://vpn-api.yourdomain.com/api/v1/`
4. Tap **Save Endpoint**.
5. The Android client will authenticate, query servers, and intelligently route via WireGuard or OpenVPN!
