package com.redeyesncode.redrecorder.service

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.telephony.TelephonyManager
import android.media.MediaRecorder
import android.telephony.PhoneStateListener
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.redeyesncode.redrecorder.R
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MyAccessibilityService : AccessibilityService() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var telephonyManager: TelephonyManager? = null
    private var phoneStateListener: PhoneStateListener? = null
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filePath = downloadsDir.absolutePath + "/recording${getCurrentTime()}red.3gp"
    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH_mm_s") // Format as "HH:mm:ss" for 24-hour time

        return currentTime.format(formatter)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // You can implement logic here to handle Accessibility Events.
        Log.i("RED_RECORDER",event?.eventType.toString())

    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        // Initialize and register PhoneStateListener
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        Log.i("RED_RECORDER","OFF_HOOK_ACCESS_SERVICE")
                        if(!isRecording){
                            Handler().postDelayed(Runnable {
                                startRecording()

                            },2000)
                        }


                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        Log.i("RED_RECORDER","IDLE_STATE_ACCESS_SERVICE")
                        if(isRecording){
                            Handler().postDelayed(Runnable {
                                stopRecording()
                            },5000)
                        }

                    }
                }
            }
        }
        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)


        // Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "AccessibilityServiceChannel"
            val channelName = "Accessibility Service Channel"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        // Create a notification for the foreground service using NotificationCompat.Builder
        val notificationIntent = Intent(this, MyAccessibilityService::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "AccessibilityServiceChannel")
            .setContentTitle("Red Recorder")
            .setContentText("Call Recorder Service is Running")
            .setSmallIcon(R.drawable.ic_app_record)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification to make the service run in the foreground
        startForeground(1, notification)
    }

    private fun startRecording() {
        if (!isRecording) {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//            val outputFile = File(Environment.getExternalStorageDirectory(), "recorded_audio.3gp")
            mediaRecorder?.setOutputFile(filePath)
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
    }

    override fun onInterrupt() {
        // Handle interruptions or cleanup here
        if (isRecording) {
            stopRecording()
        }
    }
}

