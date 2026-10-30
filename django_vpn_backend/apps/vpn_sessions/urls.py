from django.urls import path
from .views import CreateSessionView, RenewSessionView, RevokeSessionView, SessionStatusView

urlpatterns = [
    path('', CreateSessionView.as_view(), name='session-create'),
    path('<str:session_id>/renew/', RenewSessionView.as_view(), name='session-renew'),
    path('<str:session_id>/', RevokeSessionView.as_view(), name='session-revoke'),
    path('<str:session_id>/status/', SessionStatusView.as_view(), name='session-status'),
]
