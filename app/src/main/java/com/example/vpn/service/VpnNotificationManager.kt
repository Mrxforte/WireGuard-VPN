package com.example.vpn.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.vpn.model.VpnServer
import com.example.vpn.model.VpnTunnelStats
import java.util.Locale

/**
 * Manages foreground VPN notifications displaying real-time connection state,
 * active server, transferred data metrics, and Disconnect action.
 */
class VpnNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "wireguard_vpn_status_channel"
        const val NOTIFICATION_ID = 1001
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "VPN Status & Telemetry",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Displays active WireGuard VPN tunnel status and live stats"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showConnectedNotification(server: VpnServer, stats: VpnTunnelStats) {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(context, VpnActionReceiver::class.java).apply {
            action = VpnActionReceiver.ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val hours = stats.sessionDurationSeconds / 3600
        val minutes = (stats.sessionDurationSeconds % 3600) / 60
        val seconds = stats.sessionDurationSeconds % 60
        val durationFormatted = if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }

        val rxFormatted = formatBytes(stats.rxBytes)
        val txFormatted = formatBytes(stats.txBytes)

        val contentText = "${server.name} • $durationFormatted • ↓ $rxFormatted  ↑ $txFormatted"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("WireGuard VPN • Connected")
            .setContentText(contentText)
            .setSubText("Zero-Leak Tunnel Active")
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Disconnect",
                disconnectPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // In case notification permission was revoked
        }
    }

    fun showConnectingNotification(server: VpnServer) {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("WireGuard VPN • Connecting...")
            .setContentText("Negotiating peer keys with ${server.name}...")
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {}
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_000_000_000 -> String.format(Locale.US, "%.1f GB", bytes / 1_000_000_000.0)
            bytes >= 1_000_000 -> String.format(Locale.US, "%.1f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format(Locale.US, "%.1f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }
}
