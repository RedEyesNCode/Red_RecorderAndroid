package com.redeyesncode.redrecorder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
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
                context?.startService(serviceIntent)
                serviceStarted = true // Set the flag to indicate the service has started


                // Start the AudioRecordService for audio recording
                val audioRecordIntent = Intent(context, AudioRecordService::class.java)
                audioRecordIntent.action = "START_RECORDING"
                context?.startService(audioRecordIntent)
                Log.i("RED_RECORDER","START_RECORDING()")

            } else if (callState == TelephonyManager.EXTRA_STATE_IDLE) {
                // The call has ended; stop the CallNotificationService if it's running
                if (serviceStarted) {
                    val stopServiceIntent = Intent(context, CallNotificationService::class.java)
                    stopServiceIntent.action = "STOP_SERVICE"
                    context?.startService(stopServiceIntent)
                    serviceStarted = false // Reset the flag when the service is stopped

                    // Stop the AudioRecordService
                    val stopAudioRecordIntent = Intent(context, AudioRecordService::class.java)
                    stopAudioRecordIntent.action = "STOP_RECORDING"
                    context?.startService(stopAudioRecordIntent)
                    Log.i("RED_RECORDER","STOP_SERVICE()")

                }
            }
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