package com.redeyesncode.redrecorder

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.redeyesncode.gozulix.service.DeviceAdminDemo
import com.redeyesncode.redbet.base.BaseActivity
import com.redeyesncode.redrecorder.databinding.ActivityMainBinding
import com.redeyesncode.redrecorder.receiver.CallLoggerReceiver
import com.redeyesncode.redrecorder.recorder.CallRecordingService
import com.redeyesncode.redrecorder.recorder.MyRecorderService
import com.redeyesncode.redrecorder.utils.PermissionManager

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding
    val arrayList = arrayListOf<String>()
    private lateinit var mDPM: DevicePolicyManager
    private lateinit var mAdminName: ComponentName
    private val REQUEST_CODE = 0
    private val callLoggerReceiver = CallLoggerReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initClicks()
        setupAdminDevice()

        setupTelephonyLogger()
        PermissionManager.requestForegroundService(this)

        setContentView(binding.root)
    }

    private fun setupTelephonyLogger() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        registerReceiver(callLoggerReceiver, intentFilter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callLoggerReceiver)

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
        }
    }
    @SuppressLint("NewApi")
    private fun initClicks() {
        binding.btnStartCalllRecorder.setOnClickListener {
            if(Environment.isExternalStorageManager()){
                arrayList.clear()
                arrayList.add("STACK-OVER-FLOW-RECORDER")
                arrayList.add("CHAT-GPT-RECORDER")
                showOptionsDialog(this,arrayList.toTypedArray())
            }else{
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)

            }



        }
        binding.btnStop.setOnClickListener {
            val stopRecordingIntent = Intent(this, MyRecorderService::class.java)
            stopService(stopRecordingIntent)
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
        }
    }


}