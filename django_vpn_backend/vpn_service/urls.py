from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/v1/auth/', include('apps.authentication.urls')),
    path('api/v1/me/', include('apps.authentication.user_urls')),
    path('api/v1/servers/', include('apps.servers.urls')),
    path('api/v1/vpn/sessions/', include('apps.vpn_sessions.urls')),
    path('api/v1/diagnostics/', include('apps.diagnostics.urls')),
]
