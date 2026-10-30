from rest_framework import serializers
from django.contrib.auth.models import User
from .models import UserProfile, UserDevice

class UserRegistrationSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)

    class Meta:
        model = User
        fields = ('id', 'username', 'email', 'password')

    def create(self, validated_data):
        user = User.objects.create_user(
            username=validated_data['username'],
            email=validated_data.get('email', ''),
            password=validated_data['password']
        )
        UserProfile.objects.create(user=user, tier='Pro', max_devices=5)
        return user

class UserDeviceSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserDevice
        fields = ('id', 'device_name', 'device_model', 'registered_at', 'last_seen_at', 'is_active')

class UserAccountSerializer(serializers.ModelSerializer):
    tier = serializers.CharField(source='vpn_profile.tier', read_only=True)
    max_devices = serializers.IntegerField(source='vpn_profile.max_devices', read_only=True)
    active_devices = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ('id', 'username', 'email', 'tier', 'max_devices', 'active_devices')

    def get_active_devices(self, obj):
        devices = obj.devices.filter(is_active=True)
        return UserDeviceSerializer(devices, many=True).data
