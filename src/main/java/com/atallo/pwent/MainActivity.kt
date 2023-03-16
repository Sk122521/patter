package com.atallo.pwent


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection

import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.atallo.pwent.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var ischecked = false

    private var mService: ServerService? = null
    private var mBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as ServerService.MyBinder
            mService = binder.getService()
            mBound = true

            val data = mService?.getData()
            // Pass the data to another method for further processing...
            processData(data)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private fun processData(data: String?) {
        Toast.makeText(applicationContext,data,Toast.LENGTH_SHORT).show()
        if(data == "notnull"){
         ischecked = true
          binding.toggleButton.isChecked = true
      }else{
          ischecked = false
          binding.toggleButton.isChecked = false
      }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupIPAddressText()

        val intent = Intent(this, ServerService::class.java)
        intent.putExtra("data", "notstart")
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                start()
            } else {
                stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

      //  stop()
    }

    private fun setupIPAddressText() {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        linkProperties?.let {
            val ipAddress = it.linkAddresses.firstOrNull { it.toString().contains("192.") }
            ipAddress?.let { binding.ipAddressTextView.text = getString(R.string.ip_address_text, it) }
        }
    }

    private fun start() {
//        CoroutineScope(Dispatchers.IO).launch {
//            BridgeApplication.serverManager.start(serverCallBack)
//        }

        val intent = Intent(this, ServerService::class.java)
        intent.putExtra("data", "start")
       // ContextCompat.startForegroundService(this, intent)
       startService(intent)
    }

    private fun stop () {
        BridgeApplication.serverManager.stop()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}