package com.redeyesncode.redrecorder.recorder

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.redeyesncode.redrecorder.R
import java.io.File
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CallRecordingService : Service() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRecording) {
            startRecording()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (isRecording) {
            stopRecording()
        }
        super.onDestroy()
    }
    private fun startRecording() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        // Set the output file path (adjust this as per your needs)
        val outputFilePath = getOutputFilePath()

        val outputFile = File(outputFilePath)

        if (!outputFile.exists()) {
            try {
                if (outputFile.createNewFile()) {
                    // File created successfully
                    mediaRecorder?.setOutputFile(outputFilePath)
                    mediaRecorder?.prepare()
                    mediaRecorder?.start()

                    isRecording = true
                    showRecordingNotification()
                    Log.i("SERVICE_RECORDING","FILE_CREATED")


                } else {
                    // Failed to create the file
                    Log.i("SERVICE_RECORDING","FAILED_FILE_CREATION")

                }
            } catch (e: IOException) {
                // Handle file creation error
                e.printStackTrace()
                Log.i("SERVICE_RECORDING","IO-EXCEPTION ${e.message.toString()}")

            }
        }else{
            Log.i("SERVICE_RECORDING","FILE_DOES_NOT_EXISTS")

        }



    }

    private fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null

            isRecording = false
            stopForeground(true)
        } catch (e: Exception) {
            // Handle exceptions
            e.printStackTrace()
        }
    }

    private fun showRecordingNotification() {
        val notification = NotificationCompat.Builder(this, "your_channel_id")
            .setContentTitle("Call Recording Service")
            .setContentText("Recording calls in progress")
            .setSmallIcon(R.drawable.baseline_android_24)
            .build()

        startForeground(1, notification)
    }

    private fun getOutputFilePath(): String {
        // Return the desired file path where you want to save the recorded call
        // You can use external storage or your app's private directory




        return Environment.getExternalStorageDirectory().absolutePath + "/${getCurrentTime()}_recorded_call.3gp"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss") // Format as "HH:mm:ss" for 24-hour time

        return currentTime.format(formatter)
    }

}
