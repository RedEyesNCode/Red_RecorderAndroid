package com.redeyesncode.gozulix.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class DeviceAdminDemo : DeviceAdminReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        // Implementation for onEnabled goes here
    }

    override fun onDisabled(context: Context, intent: Intent) {
        // Implementation for onDisabled goes here
    }
}
