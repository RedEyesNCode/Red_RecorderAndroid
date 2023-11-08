package com.redeyesncode.redbet.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.snackbar.Snackbar
import com.redeyesncode.redrecorder.R
import com.redeyesncode.redrecorder.databinding.DialogMessageAlertBinding


open class BaseActivity: AppCompatActivity() {
    private var loadingDialog: AlertDialog? = null
    lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        disableScreenShot()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.layout_loading_dialog)
        loadingDialog = builder.create()
        if (isDeviceRooted()) {
            // Handle rooted device (e.g., show an error message and close the app)
            Toast.makeText(this, "Rooted devices are not supported.", Toast.LENGTH_SHORT).show()
            finish()
        }
        observeConnectivity()

    }
    private fun observeConnectivity() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                // Network is available, you can perform actions here if needed.
            }

            override fun onLost(network: android.net.Network) {
                super.onLost(network)
                // Network is lost, show a toast to inform the user.
                runOnUiThread {
                    showToast("No Internet Connection !!")
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }



    fun disableScreenShot(){
        // Disable screenshot and screen recording
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    private fun isDeviceRooted(): Boolean {
        val buildTags = android.os.Build.TAGS
        return (buildTags != null && buildTags.contains("test-keys")) ||
                isRootAccessible()
    }

    private fun isRootAccessible(): Boolean {
        try {
            val process = Runtime.getRuntime().exec("su")
            val outputStream = process.outputStream
            outputStream.write("exit\n".toByteArray())
            outputStream.flush()
            process.waitFor()
            return process.exitValue() == 0
        } catch (e: Exception) {
            return false
        }
    }


    fun showToast(message: String) {
        // Show a toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(message: String) {
        // Show a Snackbar message
        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

    fun showCustomDialog(title: String, message: String) {
        // Show a custom dialog with an OK button
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
    fun showLoadingDialog() {

        loadingDialog?.show()
    }

    fun hideLoadingDialog() {
        if(loadingDialog!!.isShowing){
            loadingDialog?.dismiss()
            loadingDialog?.dismiss()
            loadingDialog?.dismiss()
            loadingDialog?.dismiss()
            loadingDialog?.dismiss()
        }

    }

    fun showMessageDialog(message:String,title:String){
        val builder = AlertDialog.Builder(this)
        val bindingDialog: DialogMessageAlertBinding = DialogMessageAlertBinding.inflate(
            LayoutInflater.from(this))
        builder.setView(bindingDialog.root)
        val mDialog = builder.create()
        bindingDialog.tvMessage.text = message
        bindingDialog.tvAlert.text = "Alert !"

        bindingDialog.ivClose.setOnClickListener {
            mDialog.dismiss()
        }
        bindingDialog.btnClose.setOnClickListener {
            mDialog.dismiss()
        }
        if(!mDialog.isShowing){
            mDialog.show()

        }

    }

    fun showBackPressMessageDialog(message: String,title: String){

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
            finish()
        }

        val mDialog = builder.create()
        if(!mDialog.isShowing){
            mDialog.show()

        }
    }

    lateinit var mWebView:WebView

}