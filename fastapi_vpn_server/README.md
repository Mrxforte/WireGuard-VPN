# FastAPI WireGuard Controller & Server Setup

This directory contains the production-ready FastAPI backend that provides:
1. Dynamic WireGuard client peer provisioning with Curve25519 public key registration.
2. Dual-stack IP address allocation (`10.8.0.0/24` IPv4 and `fd00:8::/64` IPv6).
3. OAuth2 Bearer token authentication.
4. Automatic lease expiration & renewal (`/vpn/peer/{peer_id}/renew`).
5. Instant peer revocation upon client disconnect (`DELETE /vpn/peer/{peer_id}`).

---

## 1. Quick Start with Docker

```bash
cd fastapi_vpn_server
docker-compose up --build -d
```

The FastAPI Swagger documentation will be available at:
`http://localhost:8000/docs`

---

## 2. Linux Gateway Setup (Native)

Run the automated provisioning script with root privileges on your Linux VPS:

```bash
chmod +x setup_wireguard.sh
sudo ./setup_wireguard.sh
```

---

## 3. Connecting from the Android App

1. In the Android App, tap the **Cloud** icon in the top app bar.
2. If running inside the Android Emulator: set the URL to `http://10.0.2.2:8000/` (alias for host machine `localhost`).
3. If connecting to a remote VPS: enter `http://<your-vps-ip>:8000/`.
4. Tap **Save Endpoint**.
5. Tap the central **CONNECT** dial. The app will generate Curve25519 keys, register with FastAPI, configure the official WireGuard GoBackend, and establish the tunnel.
