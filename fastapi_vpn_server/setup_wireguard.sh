#!/usr/bin/env bash
# WireGuard Gateway Server Setup Script for Ubuntu / Debian / RHEL
set -e

echo "=== [1/4] Installing WireGuard & Tools ==="
apt-get update
apt-get install -y wireguard wireguard-tools iptables

echo "=== [2/4] Generating Server Keypair ==="
umask 077
wg genkey | tee /etc/wireguard/server_private.key | wg pubkey > /etc/wireguard/server_public.key
SERVER_PRIV=$(cat /etc/wireguard/server_private.key)
SERVER_PUB=$(cat /etc/wireguard/server_public.key)

echo "Server Public Key: $SERVER_PUB"

echo "=== [3/4] Creating WireGuard Interface wg0.conf ==="
cat <<EOF > /etc/wireguard/wg0.conf
[Interface]
Address = 10.8.0.1/24, fd00:8::1/64
ListenPort = 51820
PrivateKey = $SERVER_PRIV
PostUp = iptables -A FORWARD -i wg0 -j ACCEPT; iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE; ip6tables -A FORWARD -i wg0 -j ACCEPT; ip6tables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
PostDown = iptables -D FORWARD -i wg0 -j ACCEPT; iptables -t nat -D POSTROUTING -o eth0 -j MASQUERADE; ip6tables -D FORWARD -i wg0 -j ACCEPT; ip6tables -t nat -D POSTROUTING -o eth0 -j MASQUERADE
EOF

echo "=== [4/4] Enabling IP Forwarding & Starting wg-quick ==="
sysctl -w net.ipv4.ip_forward=1
sysctl -w net.ipv6.conf.all.forwarding=1
echo "net.ipv4.ip_forward=1" >> /etc/sysctl.conf
echo "net.ipv6.conf.all.forwarding=1" >> /etc/sysctl.conf

systemctl enable wg-quick@wg0
systemctl restart wg-quick@wg0

echo "WireGuard Server is active and listening on UDP port 51820!"
