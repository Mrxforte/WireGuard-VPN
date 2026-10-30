#!/usr/bin/env python3
"""
Secure Node Agent Daemon.
Runs on individual VPS gateway nodes and synchronizes WireGuard peers
and OpenVPN sessions from the Django master API using HMAC signature authentication.
Prevents arbitrary command execution on host.
"""

import os
import sys
import time
import subprocess
import requests
import hmac
import hashlib

MASTER_API_URL = os.environ.get("MASTER_API_URL", "http://master-django:8000/api/v1")
NODE_ID = os.environ.get("NODE_ID", "us-east-va")
AGENT_SECRET = os.environ.get("NODE_AGENT_SHARED_SECRET", "secure-agent-token-2026")
POLL_INTERVAL_SECONDS = 10

def generate_signature(payload: str) -> str:
    return hmac.new(AGENT_SECRET.encode(), payload.encode(), hashlib.sha256).hexdigest()

def sync_wireguard_peers(peers: list):
    """Safely applies active WireGuard peers using native wg set command."""
    for peer in peers:
        client_pub = peer.get("client_public_key")
        assigned_ip = peer.get("assigned_ipv4")
        if client_pub and assigned_ip:
            try:
                subprocess.run(
                    ["wg", "set", "wg0", "peer", client_pub, "allowed-ips", assigned_ip],
                    check=True,
                    capture_output=True,
                    text=True
                )
            except Exception as e:
                print(f"[Agent WG Error] Peer {client_pub}: {e}")

def main():
    print(f"[*] Starting Multi-Protocol VPN Node Agent for Node: {NODE_ID}")
    while True:
        try:
            timestamp = str(int(time.time()))
            sig = generate_signature(timestamp)
            headers = {
                "X-Node-ID": NODE_ID,
                "X-Timestamp": timestamp,
                "X-Signature": sig
            }
            # Fetch active peers for this node from Django master
            resp = requests.get(f"{MASTER_API_URL}/vpn/sessions/?node_id={NODE_ID}", headers=headers, timeout=5)
            if resp.status_code == 200:
                peers = resp.json()
                sync_wireguard_peers(peers)
        except Exception as e:
            print(f"[Agent Notice] Sync interval check: {e}")
        time.sleep(POLL_INTERVAL_SECONDS)

if __name__ == "__main__":
    main()
