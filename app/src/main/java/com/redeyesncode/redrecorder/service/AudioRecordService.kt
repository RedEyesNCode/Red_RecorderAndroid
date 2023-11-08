package com.redeyesncode.redrecorder.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import android.os.Handler
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class AudioRecordService : Service() {
    private var mediaRecorder: MediaRecorder? = null
    private val handler = Handler()
    private val recordingDuration = 6000 // 15 seconds in milliseconds
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filePath = downloadsDir.absolutePath + "/recording${getCurrentTime()}red.3gp"
    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        Log.i("RED_RECORDER","service-record-oncreate")
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.setOutputFile(filePath)
        }catch (e:Exception){
            Log.i("RED_RECORDER","service-record-exception-oncreate ${e.message.toString()}")

        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "START_RECORDING") {
            // Start recording
            try {
                mediaRecorder?.prepare()
                mediaRecorder?.start()
                handler.postDelayed({
                    // Stop recording after 15 seconds
                    stopRecording()
                }, recordingDuration.toLong())
                Log.i("RED_RECORDER","service-record-prepare()")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("RED_RECORDER","service-record-exception-recorder ${e.message.toString()}")

            }
        } else if (intent?.action == "STOP_RECORDING") {
            // Stop recording if it's in progress
            stopRecording()
            Log.i("RED_RECORDER","service-record-stop()")

        }

        return START_NOT_STICKY
    }

    private fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder?.release()
        mediaRecorder = null
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("mm_s") // Format as "HH:mm:ss" for 24-hour time

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
