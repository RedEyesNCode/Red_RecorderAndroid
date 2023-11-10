package com.redeyesncode.redrecorder.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.Process
import androidx.core.app.NotificationCompat
import com.redeyesncode.redrecorder.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AudioRecordForegroundService : Service() {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val filePath = downloadsDir.absolutePath + "/recording${getCurrentTime()}red_RAWWWW.pcm"
    val wavfilePath = downloadsDir.absolutePath + "/recording${getCurrentTime()}red_WAV.wav"

    companion object {
        private const val SAMPLE_RATE = 44100

        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "audio_record_channel"
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildForegroundNotification())

        startRecording()

        return START_STICKY
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun startRecording() {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val audioData = ByteArray(bufferSize)
        audioRecord?.startRecording()

        isRecording = true

        Thread {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)

            while (isRecording) {
                val bytesRead = audioRecord?.read(audioData, 0, bufferSize) ?: 0
                // Process the audio data as needed
                val audioData = ByteArray(bufferSize)
                val fileOutputStream = FileOutputStream(filePath)

                audioRecord?.startRecording()

                isRecording = true

                Thread {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)

                    while (isRecording) {
                        val bytesRead = audioRecord?.read(audioData, 0, bufferSize) ?: 0
                        if (bytesRead > 0) {
                            fileOutputStream.write(audioData, 0, bytesRead)
                        }
                    }

                    audioRecord?.stop()
                    audioRecord?.release()
                    fileOutputStream.close()
                    addWavHeader(filePath,wavfilePath)
                }.start()
            }

            audioRecord?.stop()
            audioRecord?.release()
        }.start()
    }

    private fun stopRecording() {
        isRecording = false
    }
    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH_mm_s") // Format as "HH:mm:ss" for 24-hour time

        return currentTime.format(formatter)
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    private fun buildForegroundNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, AudioRecordForegroundService::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording Audio")
            .setContentText("Your app is recording audio in the background.")
            .setSmallIcon(R.drawable.baseline_lock_24)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Record",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    private fun addWavHeader(pcmFilePath: String, wavFilePath: String) {
        try {
            val pcmFile = File(pcmFilePath)
            val wavFile = File(wavFilePath)

            val inputStream = FileInputStream(pcmFile)
            val outputStream = FileOutputStream(wavFile)

            val totalAudioLen = inputStream.channel.size()
            val totalDataLen = totalAudioLen + 36
            val longSampleRate = 44100L
            val channels = 1
            val byteRate = 16 * longSampleRate * channels / 8

            val header = ByteBuffer.allocate(44)

            header.order(ByteOrder.LITTLE_ENDIAN)

            header.put("RIFF".toByteArray())
            header.putInt(totalDataLen.toInt())
            header.put("WAVE".toByteArray())
            header.put("fmt ".toByteArray())
            header.putInt(16)
            header.putShort(1.toShort()) // AudioFormat, PCM = 1
            header.putShort(channels.toShort())
            header.putInt(longSampleRate.toInt())
            header.putInt(byteRate.toInt())
            header.putShort((channels * 16 / 8).toShort())
            header.putShort(16.toShort())
            header.put("data".toByteArray())
            header.putInt(totalAudioLen.toInt())

            outputStream.write(header.array())

            val data = ByteArray(4096)
            var bytesRead = inputStream.read(data)

            while (bytesRead != -1) {
                outputStream.write(data, 0, bytesRead)
                bytesRead = inputStream.read(data)
            }

            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    // Rest of the code (notification creation, etc.) remains the same
}
