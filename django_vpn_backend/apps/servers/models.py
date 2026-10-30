from django.db import models

class VpnServer(models.Model):
    server_id = models.CharField(max_length=64, unique=True)
    name = models.CharField(max_length=128)
    country = models.CharField(max_length=128)
    country_code = models.CharField(max_length=4)
    city = models.CharField(max_length=128)
    host = models.CharField(max_length=255)
    wireguard_port = models.PositiveIntegerField(default=51820)
    openvpn_udp_port = models.PositiveIntegerField(default=1194)
    openvpn_tcp_port = models.PositiveIntegerField(default=443)
    public_key = models.CharField(max_length=255)  # WireGuard Server Public Key
    openvpn_ca_cert = models.TextField(default="-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----")
    ping_ms = models.PositiveIntegerField(default=30)
    load_percentage = models.PositiveIntegerField(default=35)
    is_online = models.BooleanField(default=True)
    flag_emoji = models.CharField(max_length=16, default="🌐")
    service_category = models.CharField(max_length=64, default="general")
    service_label = models.CharField(max_length=64, default="Ultra Fast")
    tier = models.CharField(max_length=32, default="free")  # free, pro
    supported_protocols = models.JSONField(default=list)

    def __str__(self):
        return f"{self.name} ({self.country_code})"
