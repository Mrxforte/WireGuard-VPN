import time
import random
from datetime import datetime, timezone, timedelta
from rest_framework import views, status, permissions
from rest_framework.response import Response
from apps.servers.models import VpnServer
from .models import VpnSession

class CreateSessionView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        server_id = request.data.get('server_id')
        client_public_key = request.data.get('client_public_key', '')
        protocol = request.data.get('protocol', 'WIREGUARD').upper()

        server = VpnServer.objects.filter(server_id=server_id).first()
        server_host = server.host if server else "us-va.smartvpn.cloud"
        server_pubkey = server.public_key if server else "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wMbCXo="
        
        if protocol == 'OPENVPN_UDP':
            port = server.openvpn_udp_port if server else 1194
        elif protocol == 'OPENVPN_TCP':
            port = server.openvpn_tcp_port if server else 443
        else:
            port = server.wireguard_port if server else 51820

        octet = random.randint(2, 250)
        session_id = f"sess_{int(time.time())}_{octet}"
        assigned_ipv4 = f"10.8.0.{octet}/32"
        assigned_ipv6 = f"fd00:8::{octet}/128"
        expires_at = int(time.time()) + 86400

        response_data = {
            "peer_id": session_id,
            "server_id": server_id or "us-east-va",
            "server_public_key": server_pubkey,
            "endpoint": f"{server_host}:{port}",
            "assigned_ipv4": assigned_ipv4,
            "assigned_ipv6": assigned_ipv6,
            "dns_servers": ["1.1.1.1", "1.0.0.1", "2606:4700:4700::1111"],
            "mtu": 1420,
            "preshared_key": None,
            "expires_at": expires_at,
            "keepalive_seconds": 25,
            "openvpn_config": None
        }

        return Response(response_data, status=status.HTTP_201_CREATED)

class RenewSessionView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request, session_id):
        new_expiry = int(time.time()) + 86400
        return Response({
            "peer_id": session_id,
            "expires_at": new_expiry,
            "status": "active"
        })

class RevokeSessionView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def delete(self, request, session_id):
        return Response({"status": "revoked", "peer_id": session_id})

class SessionStatusView(views.APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request, session_id):
        return Response({
            "peer_id": session_id,
            "status": "active",
            "bytes_received": 1024 * 1024 * 12,
            "bytes_sent": 1024 * 1024 * 4,
            "last_handshake_epoch": int(time.time()),
            "is_revoked": False
        })
