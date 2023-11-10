package com.redeyesncode.access_service_recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

class RedRecorderNative(var context: Context) {

    fun requestAccessibilityPermission(activity: AppCompatActivity) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        activity.startActivity(intent)
    }
    fun checkAccessibilityPermission(activity: Activity): Boolean {
        var accessEnabled = 0
        try {
            accessEnabled = Settings.Secure.getInt(
                activity.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return if (accessEnabled == 0) {
            // if not construct intent to request permission
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // request permission via start activity for result
            activity.startActivity(intent)
            false
        } else {
            true
        }
    }
}