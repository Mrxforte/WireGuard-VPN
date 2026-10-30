# Implementation and Migration Plan

## 1. Migration Strategy Overview
This migration transitions the mobile VPN solution from a single-endpoint prototype to a commercial-grade, multi-protocol platform backed by a self-hosted Django REST Framework control plane and independent Linux VPS gateways.

---

## 2. Phase Breakdown

```
Phase 1: Architecture & Model Decoupling [COMPLETED]
├── Create unified protocol abstraction (`ProtocolAdapter`)
├── Establish VpnSessionState machine (11 distinct lifecycle states)
├── Centralize M3 design tokens (Dark & Light themes)
└── Normalize 100-technology networking catalog

Phase 2: Protocol Engine & Failover Orchestrator [COMPLETED]
├── Implement WireGuardProtocolAdapter using GoBackend
├── Implement OpenVpnProtocolAdapter (UDP 1194 & TCP 443)
├── Build ServerSelector (Latency * 0.6 + Load * 0.4 ranking)
├── Build FailoverPolicy (Exponential backoff, jitter, protocol fallback)
└── Build ConnectionOrchestrator state machine & health observer

Phase 3: Self-Hosted Control Plane & VPS Gateway [COMPLETED]
├── Django 5 + DRF backend with JWT authentication
├── Dynamic session provisioner with dual-stack IP allocation
├── VPS Node Agent with HMAC-SHA256 authenticated peer sync
└── Automated multi-protocol node setup script (`setup_vpn_node.sh`)

Phase 4: Full 100-Technology Catalog & Android UI [CURRENT]
├── In-memory and API-synced TechnologyCatalog registry
├── Modern Jetpack Compose UI (Dashboard, Server Picker, Catalog, Diagnostics, Settings, Account)
├── Real-time telemetry, handshake detection, and audit logging
└── Zero-mock connection verification

Phase 5: Verification & Production Readiness
├── Automated unit tests (Ranking, Failover, Config Generation)
├── Full compilation verification
└── Production deployment handbook and runbook
```

---

## 3. Safe Rollout & Zero-Downtime Migration Steps

1. **Database Migrations**:
   - Run `python manage.py migrate` in Django backend.
   - Initial migrations create `UserProfile`, `UserDevice`, `VpnServer`, and `VpnSession` tables.
2. **Server Seed Script**:
   - Run `python manage.py seed_servers` to populate high-performance nodes across Americas, Europe, and Asia-Pacific with WireGuard and OpenVPN configurations.
3. **Node Provisioning**:
   - On VPS gateways, execute `bash setup_vpn_node.sh` to install WireGuard tools, Easy-RSA, OpenVPN, and configure firewall NAT masquerading.
   - Start node agent: `python3 agent/node_agent.py --port 8989 --secret <NODE_SECRET>`.
4. **Android Client Release**:
   - Deploy Android APK with updated `applicationId`, `VpnModels`, and `ConnectionOrchestrator`.
   - Client fetches live server list via `/api/v1/servers/`. If offline or unauthenticated, client falls back to bundled reliable gateway seeds.
5. **Rollback Plan**:
   - If an endpoint or protocol experiences upstream provider throttling (e.g., UDP blocked), client automatically cascades: `WireGuard (UDP 51820)` -> `OpenVPN UDP (1194)` -> `OpenVPN TCP (443)`.
