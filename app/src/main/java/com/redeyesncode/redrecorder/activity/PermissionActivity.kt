package com.redeyesncode.redrecorder.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.redeyesncode.redbet.base.BaseActivity
import com.redeyesncode.redrecorder.MainActivity
import com.redeyesncode.redrecorder.R
import com.redeyesncode.redrecorder.adapter.PermissionAdapter
import com.redeyesncode.redrecorder.data.PermissionData
import com.redeyesncode.redrecorder.databinding.ActivityPermissionBinding
import com.redeyesncode.redrecorder.utils.PermissionManager

class PermissionActivity : BaseActivity(),PermissionAdapter.onPermission {

    lateinit var binding:ActivityPermissionBinding

    override fun onPermissionClick(data: PermissionData) {
        if(data.permissionKey.equals("PHONE")){
            showToast("PHONE")

            PermissionManager.requestCallPhonePermission(this)

        }else if(data.permissionKey.equals("CONTACT")){
            showToast("CONTACT")

            PermissionManager.requestReadPhoneStatePermission(this)

        }else{
            //MICROPHONE
            showToast("AUDIO & STORAGE")

            PermissionManager.requestAudioPermission(this)


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionBinding.inflate(layoutInflater)

        initClicks()
        setupPermissionAdapter()



        setContentView(binding.root)
    }


    private fun setupPermissionAdapter() {

        // making the data class.
        val data = PermissionData("PHONE","Phone","This Permission is required to place phone calls",
            ContextCompat.getDrawable(this@PermissionActivity,R.drawable.baseline_phone_24)!!)
        var dataList = ArrayList<PermissionData>()
        dataList.add(data)
        dataList.add(PermissionData("CONTACT","Contact","This Permission is required to read your contacts, when you choose to import them",
            ContextCompat.getDrawable(this@PermissionActivity,R.drawable.baseline_account_box_24)!!))
        dataList.add(PermissionData("MICROPHONE","Microphone","This Permission is required to convert your voice notes to text notes.",
            ContextCompat.getDrawable(this@PermissionActivity,R.drawable.baseline_lock_24)!!))

        binding.recvPermission.adapter = PermissionAdapter(this@PermissionActivity,dataList,this)

        binding.recvPermission.layoutManager = LinearLayoutManager(this@PermissionActivity,
            LinearLayoutManager.VERTICAL,false)

    }

    private fun initClicks() {
        binding.btnFinish.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)


        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionManager.CALL_PHONE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // CALL_PHONE permission granted, you can now make phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestAnswerPhoneCallsPermission(this)

                } else {
                    // CALL_PHONE permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")
                }
            }
            PermissionManager.ANSWER_PHONE_CALLS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestReadCallLogPermission(this)
                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.READ_CALL_LOG_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestWriteCallLogPermission(this)

                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestStoragePermission(this)
                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestProcessCallPermission(this)
                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.PROCESS_CALL_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestManageStoragePermission(this)
                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.MANAGE_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestCaptureAudioOutput(this)

                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED MANAGE STORAGE")
                    PermissionManager.requestReadStorage(this)
                    PermissionManager.requestCaptureAudioOutput(this)

                }
            }
            PermissionManager.READ_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED READ STORAGE")

                }
            }
            PermissionManager.WRITE_CALL_LOG_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestSendSMSPermission(this)

                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.SEND_SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ANSWER_PHONE_CALLS permission granted, you can now answer phone calls
                    showSnackbar("PERMISSION IS GRANTED")

                } else {
                    // ANSWER_PHONE_CALLS permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.READ_PHONE_STATE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // READ_PHONE_STATE permission granted, you can now access phone state information
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestReadContactsPermission(this)
                } else {
                    // READ_PHONE_STATE permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.READ_CONTACTS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // READ_PHONE_STATE permission granted, you can now access phone state information
                    showSnackbar("PERMISSION IS GRANTED")
                    PermissionManager.requestWriteContactsPermission(this)

                } else {
                    // READ_PHONE_STATE permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
            PermissionManager.WRITE_CONTACTS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // READ_PHONE_STATE permission granted, you can now access phone state information
                    showSnackbar("PERMISSION IS GRANTED")

                } else {
                    // READ_PHONE_STATE permission denied, handle accordingly
                    showSnackbar("PERMISSION IS DENIED")

                }
            }
        }
    }


}