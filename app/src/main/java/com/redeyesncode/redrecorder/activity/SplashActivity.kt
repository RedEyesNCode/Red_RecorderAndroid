package com.redeyesncode.redrecorder.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.redeyesncode.redbet.base.BaseActivity
import com.redeyesncode.redrecorder.MainActivity
import com.redeyesncode.redrecorder.R
import com.redeyesncode.redrecorder.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        Handler().postDelayed(Runnable {
            val intent = Intent(this, PermissionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        },3000)

        setContentView(binding.root)
    }
}