#!/usr/bin/env bash
# Production Multi-Protocol VPN Gateway Provisioning Script (WireGuard + OpenVPN UDP/TCP)
# Tested on Ubuntu 22.04 / 24.04 LTS and Debian 12
set -euo pipefail

echo "=========================================================="
echo " [1/6] Installing Kernel WireGuard, OpenVPN, and Tools"
echo "=========================================================="
apt-get update
apt-get install -y wireguard wireguard-tools openvpn easy-rsa iptables curl jq python3-pip

echo "=========================================================="
echo " [2/6] Enabling Kernel IPv4 & IPv6 Forwarding"
echo "=========================================================="
sysctl -w net.ipv4.ip_forward=1
sysctl -w net.ipv6.conf.all.forwarding=1
sed -i '/net.ipv4.ip_forward/d' /etc/sysctl.conf
sed -i '/net.ipv6.conf.all.forwarding/d' /etc/sysctl.conf
echo "net.ipv4.ip_forward=1" >> /etc/sysctl.conf
echo "net.ipv6.conf.all.forwarding=1" >> /etc/sysctl.conf
sysctl -p

echo "=========================================================="
echo " [3/6] Setting Up WireGuard Interface wg0 (UDP 51820)"
echo "=========================================================="
mkdir -p /etc/wireguard
chmod 700 /etc/wireguard

if [ ! -f /etc/wireguard/server_private.key ]; then
    wg genkey | tee /etc/wireguard/server_private.key | wg pubkey > /etc/wireguard/server_public.key
fi

SERVER_WG_PRIV=$(cat /etc/wireguard/server_private.key)
SERVER_WG_PUB=$(cat /etc/wireguard/server_public.key)

cat <<EOF > /etc/wireguard/wg0.conf
[Interface]
Address = 10.8.0.1/24, fd00:8::1/64
ListenPort = 51820
PrivateKey = $SERVER_WG_PRIV
PostUp = iptables -A FORWARD -i wg0 -j ACCEPT; iptables -t nat -A POSTROUTING -s 10.8.0.0/24 -o eth0 -j MASQUERADE; ip6tables -A FORWARD -i wg0 -j ACCEPT; ip6tables -t nat -A POSTROUTING -s fd00:8::/64 -o eth0 -j MASQUERADE
PostDown = iptables -D FORWARD -i wg0 -j ACCEPT; iptables -t nat -D POSTROUTING -s 10.8.0.0/24 -o eth0 -j MASQUERADE; ip6tables -D FORWARD -i wg0 -j ACCEPT; ip6tables -t nat -D POSTROUTING -s fd00:8::/64 -o eth0 -j MASQUERADE
EOF

systemctl enable wg-quick@wg0
systemctl restart wg-quick@wg0

echo "WireGuard active on UDP 51820. Server Public Key: $SERVER_WG_PUB"

echo "=========================================================="
echo " [4/6] Setting Up OpenVPN Dual Transports (UDP 1194 & TCP 443)"
echo "=========================================================="
make-cadir /etc/openvpn/easy-rsa || true
cd /etc/openvpn/easy-rsa
if [ ! -f pki/ca.crt ]; then
    ./easyrsa --batch init-pki
    ./easyrsa --batch build-ca nopass
    ./easyrsa --batch gen-dh
    ./easyrsa --batch build-server-full server nopass
    openvpn --genkey secret /etc/openvpn/tc.key
    cp pki/ca.crt pki/dh.pem pki/issued/server.crt pki/private/server.key /etc/openvpn/
fi

# UDP 1194 Config
cat <<EOF > /etc/openvpn/server_udp.conf
port 1194
proto udp
dev tun0
ca /etc/openvpn/ca.crt
cert /etc/openvpn/server.crt
key /etc/openvpn/server.key
dh /etc/openvpn/dh.pem
tls-auth /etc/openvpn/tc.key 0
server 10.9.0.0 255.255.255.0
server-ipv6 fd00:9::/64
push "redirect-gateway def1 bypass-dhcp"
push "route-ipv6 ::/0"
push "dhcp-option DNS 1.1.1.1"
push "dhcp-option DNS 1.0.0.1"
keepalive 10 120
cipher AES-256-GCM
auth SHA256
user nobody
group nogroup
persist-key
persist-tun
status /var/log/openvpn-udp-status.log
verb 3
EOF

# TCP 443 Config (for DPI bypass)
cat <<EOF > /etc/openvpn/server_tcp.conf
port 443
proto tcp-server
dev tun1
ca /etc/openvpn/ca.crt
cert /etc/openvpn/server.crt
key /etc/openvpn/server.key
dh /etc/openvpn/dh.pem
tls-auth /etc/openvpn/tc.key 0
server 10.9.1.0 255.255.255.0
server-ipv6 fd00:9:1::/64
push "redirect-gateway def1 bypass-dhcp"
push "route-ipv6 ::/0"
push "dhcp-option DNS 1.1.1.1"
push "dhcp-option DNS 1.0.0.1"
keepalive 10 120
cipher AES-256-GCM
auth SHA256
user nobody
group nogroup
persist-key
persist-tun
status /var/log/openvpn-tcp-status.log
verb 3
EOF

systemctl enable openvpn@server_udp
systemctl enable openvpn@server_tcp
systemctl restart openvpn@server_udp
systemctl restart openvpn@server_tcp

echo "=========================================================="
echo " [5/6] Configuring IP Tables NAT Masquerade"
echo "=========================================================="
iptables -t nat -A POSTROUTING -s 10.9.0.0/24 -o eth0 -j MASQUERADE
iptables -t nat -A POSTROUTING -s 10.9.1.0/24 -o eth0 -j MASQUERADE

echo "=========================================================="
echo " [6/6] Multi-Protocol Node Setup Complete!"
echo " WireGuard: UDP 51820"
echo " OpenVPN:   UDP 1194 & TCP 443"
echo "=========================================================="
