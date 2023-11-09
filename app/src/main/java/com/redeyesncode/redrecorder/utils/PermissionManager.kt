package com.redeyesncode.redrecorder.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {


    const val CALL_PHONE_PERMISSION_REQUEST_CODE = 1
    const val ANSWER_PHONE_CALLS_PERMISSION_REQUEST_CODE = 2
    const val READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 3
    const val READ_CONTACTS_PERMISSION_REQUEST_CODE = 4
    const val WRITE_CONTACTS_PERMISSION_REQUEST_CODE = 5
    const val READ_CALL_LOG_PERMISSION_REQUEST_CODE = 6
    const val WRITE_CALL_LOG_PERMISSION_REQUEST_CODE = 7
    const val SEND_SMS_PERMISSION_REQUEST_CODE = 8
    const val AUDIO_PERMISSION_REQUEST_CODE = 9
    const val STORAGE_PERMISSION_REQUEST_CODE = 10
    const val PROCESS_CALL_REQUEST_CODE = 11
    const val MANAGE_STORAGE_REQUEST_CODE = 12
    const val READ_STORAGE_REQUEST_CODE = 13
    const val CAPTURE_AUDIO_REQUEST_CODE = 14
    const val FOREGROUND_SERVICE_REQUEST_CODE = 15

    fun requestCallPhonePermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_REQUEST_CODE)
        }
    }

    fun requestAnswerPhoneCallsPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ANSWER_PHONE_CALLS), ANSWER_PHONE_CALLS_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestReadPhoneStatePermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestReadContactsPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CONTACTS), READ_CONTACTS_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestWriteContactsPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_CONTACTS), WRITE_CONTACTS_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestReadCallLogPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CALL_LOG), READ_CALL_LOG_PERMISSION_REQUEST_CODE)
        }
    }

    fun requestWriteCallLogPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_CALL_LOG), WRITE_CALL_LOG_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestSendSMSPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestAudioPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), AUDIO_PERMISSION_REQUEST_CODE)
        }
    }

    fun requestStoragePermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
        }
    }
    fun requestProcessCallPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS), PROCESS_CALL_REQUEST_CODE)
        }
    }
    fun requestCaptureAudioOutput(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAPTURE_AUDIO_OUTPUT), CAPTURE_AUDIO_REQUEST_CODE)
        }
    }

    fun requestManageStoragePermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), MANAGE_STORAGE_REQUEST_CODE)
        }
    }
    fun requestReadStorage(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_REQUEST_CODE)
        }
    }

    fun requestForegroundService(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.FOREGROUND_SERVICE), FOREGROUND_SERVICE_REQUEST_CODE)
        }
    }
}