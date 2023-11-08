package com.redeyesncode.redrecorder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.redeyesncode.redrecorder.MainActivity
import com.redeyesncode.redrecorder.R
import com.redeyesncode.redrecorder.activity.SplashActivity

class CallNotificationService : Service() {
    private val channelId = "RedRecorderNotificationChannel"
    private val notificationId = 101 // Unique ID for the notification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action == "STOP_SERVICE") {
                // Stop the service and remove the notification
                stopForeground(true)
                stopSelf()
            }else{
                val callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if (callState == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    // The call has started; show the notification.
                    showNotification()
                }else{
                    Log.i("RED_RECORDER","service-call-state-${callState}")
                }
            }
        }else{
            Log.i("RED_RECORDER","service-intent-null")

        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun showNotification() {

        Log.i("RED_RECORDER","showNotification()")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Call Notification", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a pending intent for when the notification is clicked
        val notificationIntent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call In Progress")
            .setContentText("You are currently on a call.")
            .setSmallIcon(R.drawable.baseline_android_24) // Replace with your icon
            .setContentIntent(pendingIntent)
            .build()

        // Start the service in the foreground, so the notification remains visible
        startForeground(notificationId, notification)
    }
}
