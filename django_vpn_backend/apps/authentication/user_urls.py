from django.urls import path
from .views import ProfileView, DeviceListView, DeviceRevokeView

urlpatterns = [
    path('', ProfileView.as_view(), name='profile'),
    path('devices/', DeviceListView.as_view(), name='device-list'),
    path('devices/<int:device_id>/', DeviceRevokeView.as_view(), name='device-revoke'),
]
