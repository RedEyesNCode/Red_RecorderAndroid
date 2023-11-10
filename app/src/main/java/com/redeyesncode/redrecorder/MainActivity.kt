package com.redeyesncode.redrecorder

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.redeyesncode.gozulix.service.DeviceAdminDemo
import com.redeyesncode.redbet.base.BaseActivity
import com.redeyesncode.redrecorder.databinding.ActivityMainBinding
import com.redeyesncode.redrecorder.databinding.FloatingWindowRecorderBinding
import com.redeyesncode.redrecorder.receiver.CallLoggerReceiver
import com.redeyesncode.redrecorder.recorder.CallRecordingService
import com.redeyesncode.redrecorder.recorder.MyRecorderService
import com.redeyesncode.redrecorder.service.AudioRecordForegroundService
import com.redeyesncode.redrecorder.service.MyAccessibilityService
import com.redeyesncode.redrecorder.utils.PermissionManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding
    val arrayList = arrayListOf<String>()
    private lateinit var mDPM: DevicePolicyManager
    private lateinit var mAdminName: ComponentName
    private val REQUEST_CODE = 0
    private val callLoggerReceiver = CallLoggerReceiver()
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    val filePath = downloadsDir.absolutePath + "/recording${getCurrentTime()}red.amr"

    val REQUEST_CODE_DRAW_OVERLAY = 123

    private var mediaRecorder: MediaRecorder? = null
        get() {
            if (field == null) {
                field = createMediaRecorder()
            }
            return field
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initClicks()
        PermissionManager.requestForegroundService(this)
        mediaRecorder = createMediaRecorder()

        setContentView(binding.root)
    }

    private fun setupTelephonyLogger() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        registerReceiver(callLoggerReceiver, intentFilter)

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun setupAdminDevice() {
        try {
            // Initiate DevicePolicyManager.
            mDPM = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            mAdminName = ComponentName(this, DeviceAdminDemo::class.java)

            if (!mDPM.isAdminActive(mAdminName)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.")
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                // mDPM.lockNow()
                // val intent = Intent(this, TrackDeviceService::class.java)
                // startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (REQUEST_CODE == requestCode) {
            val intent = Intent(this, MyRecorderService::class.java)
            startService(intent)
        }else if(REQUEST_CODE_DRAW_OVERLAY== requestCode){
            createDraggableFloatingDialog()
        }
    }
    @SuppressLint("NewApi")
    private fun initClicks() {
        binding.fabButton.setOnClickListener {
            showDraggableFloatingDialog()
        }
        binding.btnStartCalllRecorder.setOnClickListener {
            if(Environment.isExternalStorageManager()){
                arrayList.clear()
                arrayList.add("STACK-OVER-FLOW-RECORDER")
                arrayList.add("CHAT-GPT-RECORDER")
                arrayList.add("RECEIVER_2SERVICE_SETUP")

                showOptionsDialog(this,arrayList.toTypedArray())
            }else{
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)

            }



        }
        binding.btnStop.setOnClickListener {
            val stopRecordingIntent = Intent(this, MyRecorderService::class.java)
            stopService(stopRecordingIntent)
            unregisterReceiver(callLoggerReceiver)

        }
        binding.btnStartActivityRecorder.setOnClickListener {

            val serviceIntent = Intent(this, AudioRecordForegroundService::class.java)
            startService(serviceIntent)

        }
        binding.btnStopActivityRecorder.setOnClickListener {
            val serviceIntent = Intent(this, AudioRecordForegroundService::class.java)
            stopService(serviceIntent)
        }
        binding.btnStartAccessService.setOnClickListener {
            val serviceIntent = Intent(this, MyAccessibilityService::class.java)
            startService(serviceIntent)
        }
        binding.btnStopActivityRecorder.setOnClickListener {
            val serviceIntent = Intent(this, MyAccessibilityService::class.java)
            stopService(serviceIntent)
        }
    }

    private suspend fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            Log.i("RED_RECORDER", "service-record-prepare()")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("RED_RECORDER", "service-record-exception-recorder ${e.message.toString()}")
        }
    }
    fun showOptionsDialog(context: Context, options: Array<String>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Recorder Code")

        builder.setItems(options) { _, which ->
            startServiceKey(options[which])
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun startServiceKey(s: String) {
        if(s.equals("STACK-OVER-FLOW-RECORDER")){
            val serviceIntent = Intent(this, MyRecorderService::class.java)
            startService(serviceIntent)
        }else if(s.equals("CHAT-GPT-RECORDER")){
            val serviceIntent = Intent(this, CallRecordingService::class.java)
            startService(serviceIntent)
        }else if(s.equals("RECEIVER_2SERVICE_SETUP")){

            setupTelephonyLogger()
        }
    }
    private fun startMyAccessibilityService() {
        val serviceIntent = Intent(this, MyAccessibilityService::class.java)

        // Check if the service is not already running
        if (!isServiceRunning(MyAccessibilityService::class.java)) {
            // Start the service
            startService(serviceIntent)
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Integer.MAX_VALUE)
        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
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
    }
    @SuppressLint("NewApi")
    fun getCurrentTime(): String {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH_mm_s") // Format as "HH:mm:ss" for 24-hour time

        return currentTime.format(formatter)
    }

    fun AppCompatActivity.showDraggableFloatingDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Request the SYSTEM_ALERT_WINDOW permission
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY)
        } else {
            // You have the permission to draw overlays, create the draggable floating dialog
            createDraggableFloatingDialog()
        }
    }

    private fun AppCompatActivity.createDraggableFloatingDialog() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE, // Use TYPE_PHONE for compatibility
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.x = 100
        params.y = 100

        val floatingView = FloatingWindowRecorderBinding.inflate(LayoutInflater.from(this))






        // Add touch listener to make it draggable
        floatingView.root.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0.0f
            private var initialTouchY = 0.0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                    }
                    MotionEvent.ACTION_UP -> {
                        // Handle the click action or other events here
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = (initialX + (event.rawX - initialTouchX)).toInt()
                        params.y = (initialY + (event.rawY - initialTouchY)).toInt()
                        windowManager.updateViewLayout(floatingView.root, params)
                    }
                }
                return false
            }
        })

        windowManager.addView(floatingView.root, params)
    }

}