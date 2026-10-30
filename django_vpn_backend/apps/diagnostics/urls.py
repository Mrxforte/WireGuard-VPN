from django.urls import path
from .views import DiagnosticsIngestView, HealthCheckView, EgressVerificationView

urlpatterns = [
    path('', DiagnosticsIngestView.as_view(), name='diagnostics-ingest'),
    path('health/', HealthCheckView.as_view(), name='diagnostics-health'),
    path('verify-egress/', EgressVerificationView.as_view(), name='diagnostics-verify-egress'),
]
