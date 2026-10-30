from rest_framework import status, permissions
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth import authenticate
from .models import UserProfile, UserDevice
from .serializers import UserRegistrationSerializer, UserAccountSerializer, UserDeviceSerializer

class RegisterView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        serializer = UserRegistrationSerializer(data=request.data)
        if serializer.is_valid():
            user = serializer.save()
            refresh = RefreshToken.for_user(user)
            return Response({
                'access_token': str(refresh.access_token),
                'refresh_token': str(refresh),
                'token_type': 'bearer',
                'expires_in': 86400,
                'user_id': str(user.id)
            }, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LoginView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        username = request.data.get('username')
        password = request.data.get('password')
        user = authenticate(username=username, password=password)
        if user:
            refresh = RefreshToken.for_user(user)
            return Response({
                'access_token': str(refresh.access_token),
                'refresh_token': str(refresh),
                'token_type': 'bearer',
                'expires_in': 86400,
                'user_id': str(user.id)
            })
        return Response({'detail': 'Invalid credentials'}, status=status.HTTP_401_UNAUTHORIZED)

class ProfileView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        serializer = UserAccountSerializer(request.user)
        return Response(serializer.data)

class DeviceListView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        devices = request.user.devices.filter(is_active=True)
        serializer = UserDeviceSerializer(devices, many=True)
        return Response(serializer.data)

class DeviceRevokeView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def delete(self, request, device_id):
        device = request.user.devices.filter(id=device_id).first()
        if device:
            device.is_active = False
            device.save()
            return Response({'status': 'revoked'}, status=status.HTTP_200_OK)
        return Response({'detail': 'Device not found'}, status=status.HTTP_404_NOT_FOUND)
