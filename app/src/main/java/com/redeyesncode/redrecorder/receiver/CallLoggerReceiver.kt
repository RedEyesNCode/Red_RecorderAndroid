package com.redeyesncode.redrecorder.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.redeyesncode.redrecorder.activity.SplashActivity
import com.redeyesncode.redrecorder.service.AudioRecordService
import com.redeyesncode.redrecorder.service.CallNotificationService

class CallLoggerReceiver : BroadcastReceiver() {
    private var serviceStarted = false // Add this variable to track if the service has started

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (callState == TelephonyManager.EXTRA_STATE_OFFHOOK && !serviceStarted) {
                // The call has started, and the service is not running; start the CallNotificationService
                val serviceIntent = Intent(context, CallNotificationService::class.java)
                serviceIntent.putExtra(TelephonyManager.EXTRA_STATE,callState)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    context?.startForegroundService(serviceIntent)
                } else {
                    context?.startService(serviceIntent)
                }


                serviceStarted = true // Set the flag to indicate the service has started

                showNotification(context!!,"RED_RECORDER","Started Recording")

                // Start the AudioRecordService for audio recording
                val audioRecordIntent = Intent(context, AudioRecordService::class.java)
                audioRecordIntent.action = "START_RECORDING"
                context?.startService(audioRecordIntent)
                Log.i("RED_RECORDER","START_RECORDING()")

            } else if (callState == TelephonyManager.EXTRA_STATE_IDLE) {
                // The call has ended; stop the CallNotificationService if it's running
                Log.i("RED_RECORDER","EXTRA_STATE_IDLE()")
                val stopServiceIntent = Intent(context, CallNotificationService::class.java)
                stopServiceIntent.action = "STOP_SERVICE"
                stopServiceIntent.putExtra(TelephonyManager.EXTRA_STATE,callState)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.startForegroundService(stopServiceIntent)
                } else {
                    context?.startService(stopServiceIntent)
                }
                serviceStarted = false // Reset the flag when the service is stopped

                showNotification(context!!,"RED_RECORDER","Stop Recording")

                // Stop the AudioRecordService
                val stopAudioRecordIntent = Intent(context, AudioRecordService::class.java)
                stopAudioRecordIntent.action = "STOP_RECORDING"
                context?.startService(stopAudioRecordIntent)
                Log.i("RED_RECORDER","STOP_SERVICE()")

            }
        }
    }
    @SuppressLint("MissingPermission")
    fun showNotification(context: Context, title: String, message: String) {
        // Notification ID
        val notificationId = 1

        // Notification Channel ID and Name (required for Android O and above)
        val channelId = "RED_RECORDER"
        val channelName = "RED_RECORDER"
        val notificationIntent = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // Create a notification builder
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Set your desired small icon
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Create the NotificationChannel (required for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

}

/*LOG CALL CODE IS PLACED HERE*/


//override fun onReceive(context: Context, intent: Intent) {
//    // Get the call state from the intent
//    val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
//
//    if (state != null) {
//        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
//            // Incoming call
//            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
//            Log.i("CallLogger", "Incoming Call from: $incomingNumber")
//        } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
//            // Outgoing call or an ongoing call
//            Log.i("CallLogger", "Call Started")
//
//
//
//        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
//            // Call ended
//            Log.i("CallLogger", "Call Ended")
//        }
//    }
//}