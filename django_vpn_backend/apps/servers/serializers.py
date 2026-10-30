from rest_framework import serializers
from .models import VpnServer

class VpnServerSerializer(serializers.ModelSerializer):
    id = serializers.CharField(source='server_id')
    publicKey = serializers.CharField(source='public_key')
    pingMs = serializers.IntegerField(source='ping_ms')
    loadPercentage = serializers.IntegerField(source='load_percentage')
    isOnline = serializers.BooleanField(source='is_online')
    flagEmoji = serializers.CharField(source='flag_emoji')
    serviceCategory = serializers.CharField(source='service_category')
    serviceLabel = serializers.CharField(source='service_label')
    isPremiumOnly = serializers.SerializerMethodField()
    wireguardPort = serializers.IntegerField(source='wireguard_port')
    openvpnUdpPort = serializers.IntegerField(source='openvpn_udp_port')
    openvpnTcpPort = serializers.IntegerField(source='openvpn_tcp_port')
    supportedProtocols = serializers.ListField(source='supported_protocols')
    openvpnCaCert = serializers.CharField(source='openvpn_ca_cert')

    class Meta:
        model = VpnServer
        fields = (
            'id', 'name', 'country', 'countryCode', 'city', 'host', 'port',
            'publicKey', 'pingMs', 'loadPercentage', 'isOnline', 'flagEmoji',
            'serviceCategory', 'serviceLabel', 'isPremiumOnly', 'wireguardPort',
            'openvpnUdpPort', 'openvpnTcpPort', 'supportedProtocols', 'openvpnCaCert', 'tier'
        )

    countryCode = serializers.CharField(source='country_code')
    port = serializers.IntegerField(source='wireguard_port')

    def get_isPremiumOnly(self, obj):
        return obj.tier.lower() == 'pro'
