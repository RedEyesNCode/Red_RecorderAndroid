package com.redeyesncode.redrecorder.recorder

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyRecorderService : Service() {
    private lateinit var recorder: MediaRecorder
    private lateinit var audiofile: File
    private lateinit var name: String
    private lateinit var phonenumber: String
    private lateinit var audio_format: String
    private lateinit var Audio_Type: String
    private var audioSource: Int = 0
    private lateinit var context: Context
    private lateinit var handler: Handler
    private lateinit var timer: Timer
    private var offHook = false
    private var ringing = false
    private var toast: Toast? = null
    private var isOffHook = false
    private var recordstarted = false

    private val ACTION_IN = "android.intent.action.PHONE_STATE"
    private val ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL"
    private lateinit var br_call: CallBr

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("service", "destroy")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filter = IntentFilter()
        filter.addAction(ACTION_OUT)
        filter.addAction(ACTION_IN)
        br_call = CallBr()
        registerReceiver(br_call, filter)
        return START_NOT_STICKY
    }



    inner class CallBr : BroadcastReceiver() {
        private var bundle: Bundle? = null
        private var state: String? = null
        private var inCall: String? = null
        private var outCall: String? = null
        private var wasRinging = false

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_IN) {
                bundle = intent.extras
                if (bundle != null) {
                    state = bundle?.getString(TelephonyManager.EXTRA_STATE)
                    if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                        inCall = bundle?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        wasRinging = true
                        Toast.makeText(context, "IN : $inCall", Toast.LENGTH_LONG).show()
                    } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                        if (wasRinging) {
                            Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show()
                            val out = SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(Date())
                            val sampleDir = File(Environment.getExternalStorageDirectory(), "/TestRecordingDasa1")
                            if (!sampleDir.exists()) {
                                sampleDir.mkdirs()
                            }
                            val file_name = "Record"
                            try {
                                audiofile = File.createTempFile(file_name, ".amr", sampleDir)
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Log.i("SERVICE_RECORDING","IOException")
                            }
                            val path = Environment.getExternalStorageDirectory().absolutePath
                            recorder = MediaRecorder()
                            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                            recorder.setOutputFile(audiofile.absolutePath)
                            try {
                                recorder.prepare()
                                Log.i("SERVICE_RECORDING","PREPARE")

                            } catch (e: IllegalStateException) {
                                Log.i("SERVICE_RECORDING","IllegalStateException")

                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Log.i("SERVICE_RECORDING","IOException")

                            }
                            recorder.start()
                            recordstarted = true
                        }
                    } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                        wasRinging = false
                        Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show()
                        if (recordstarted) {
                            recorder.stop()
                            recordstarted = false
                        }
                    }
                }
            } else if (intent.action == ACTION_OUT) {
                outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                Toast.makeText(context, "OUT : $outCall", Toast.LENGTH_LONG).show()




            }
        }
    }
}
