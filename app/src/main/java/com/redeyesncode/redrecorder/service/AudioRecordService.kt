package com.redeyesncode.redrecorder.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.Handler
import android.os.Process
import android.util.Log
import androidx.core.app.NotificationCompat
import com.redeyesncode.redrecorder.R
import com.redeyesncode.redrecorder.activity.SplashActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class AudioRecordService : Service() {
    private val handler = Handler()
    private val recordingDuration = 6000 // 15 seconds in milliseconds
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filePath = downloadsDir.absolutePath + "/recording${getCurrentTime()}red.amr"
    private val channelId = "RedRecorderNotificationChannel_2"
    private val notificationId = 102 // Unique ID for the notification


    private var mediaRecorder: MediaRecorder? = null
        get() {
            if (field == null) {
                field = createMediaRecorder()
            }
            return field
        }

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        Log.i("RED_RECORDER","service-record-oncreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START_RECORDING") {
            // Start recording
            try {
                startRecording()
                Log.i("RED_RECORDER","service-record-prepare()")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("RED_RECORDER","service-record-exception-recorder ${e.message.toString()}")

            }
        } else if (intent?.action == "STOP_RECORDING") {
            // Stop recording if it's in progress
            stopRecording()
            Log.i("RED_RECORDER","service-record-stop()")
            dismissNotification()
            stopSelf()

        }

        return START_STICKY
    }
    private fun createMediaRecorder(): MediaRecorder {
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(filePath)
        return recorder
    }
    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder?.release()
        mediaRecorder = null
        stopSelf()
    }
    private fun dismissNotification() {
        stopForeground(true)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(notificationId)
    }
    private fun buildForegroundNotification(): Notification {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "RED_RECORDER", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a pending intent for when the notification is clicked
        val notificationIntent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call In Progress")
            .setContentText("You are currently on a call.")
            .setSmallIcon(R.drawable.baseline_android_24) // Replace with your icon
            .setContentIntent(pendingIntent)
            .build()

        return notification


    }
    private  fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            Log.i("RED_RECORDER", "service-record-prepare()")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("RED_RECORDER", "service-record-exception-recorder ${e.message.toString()}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH_mm_s") // Format as "HH:mm:ss" for 24-hour time

        return currentTime.format(formatter)
    }

    fun createBlank3gpFileInDownloads(filename: String): String? {
        try {
            // Determine the Downloads directory
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Create a File object for the specified directory and filename
            val file = File(downloadsDir, filename)

            // Ensure that the parent directory exists; if not, create it
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }

            // Create the blank 3gp file
            if (file.createNewFile()) {
                // Return the path to the newly created file
                return file.absolutePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return null if the file creation fails
        return null
    }
}
