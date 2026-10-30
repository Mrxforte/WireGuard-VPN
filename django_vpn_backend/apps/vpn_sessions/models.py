from django.db import models
from django.contrib.auth.models import User
from apps.servers.models import VpnServer

class VpnSession(models.Model):
    session_id = models.CharField(max_length=64, unique=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=True, blank=True, related_name='vpn_sessions')
    server = models.ForeignKey(VpnServer, on_delete=models.CASCADE, related_name='active_sessions')
    protocol = models.CharField(max_length=32, default='WIREGUARD')
    client_public_key = models.CharField(max_length=255)
    assigned_ipv4 = models.CharField(max_length=32)
    assigned_ipv6 = models.CharField(max_length=64, null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    expires_at = models.DateTimeField()
    is_active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.session_id} on {self.server.name} ({self.protocol})"
