package com.redeyesncode.redrecorder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.redeyesncode.redbet.base.BaseActivity
import com.redeyesncode.redrecorder.databinding.ActivityMainBinding
import com.redeyesncode.redrecorder.recorder.CallRecordingService

class MainActivity : BaseActivity() {

    lateinit var binding:ActivityMainBinding
    val arrayList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initClicks()

        setContentView(binding.root)
    }

    private fun initClicks() {
        binding.btnStartCalllRecorder.setOnClickListener {
            arrayList.add("STACK-OVER-FLOW-RECORDER")
            arrayList.add("ACCESSIBILITY-RECORDER")
            showOptionsDialog(this,arrayList.toTypedArray())

        }
        binding.btnStop.setOnClickListener {
            val stopRecordingIntent = Intent(this, CallRecordingService::class.java)
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
            val serviceIntent = Intent(this, CallRecordingService::class.java)
            startService(serviceIntent)
        }else if(s.equals("ACCESSIBILITY-RECORDER")){
            showToast("Comming Soon")
        }
    }


}