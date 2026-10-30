from django.db import models
from django.contrib.auth.models import User

class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, related_name='vpn_profile')
    tier = models.CharField(max_length=20, default='Pro')  # free, pro, enterprise
    max_devices = models.PositiveIntegerField(default=5)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.user.username} ({self.tier})"

class UserDevice(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='devices')
    device_id = models.CharField(max_length=128)
    device_name = models.CharField(max_length=100)
    device_model = models.CharField(max_length=100)
    registered_at = models.DateTimeField(auto_now_add=True)
    last_seen_at = models.DateTimeField(auto_now=True)
    is_active = models.BooleanField(default=True)

    class Meta:
        unique_together = ('user', 'device_id')

    def __str__(self):
        return f"{self.device_name} ({self.user.username})"
