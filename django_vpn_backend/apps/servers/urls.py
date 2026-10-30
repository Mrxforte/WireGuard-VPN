from django.urls import path
from .views import VpnServerListView, CapabilitiesListView, ProtocolsListView

urlpatterns = [
    path('', VpnServerListView.as_view(), name='server-list'),
    path('capabilities/', CapabilitiesListView.as_view(), name='capabilities-list'),
    path('protocols/', ProtocolsListView.as_view(), name='protocols-list'),
]
