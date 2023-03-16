package com.atallo.pwent

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.atallo.pwent.BridgeApplication.Companion.serverManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException

class ServerService : Service() {

   // val mBinder = MyBinder()
    private var mData = "null"


   private val serverCallBack: ServerCallBack = (object: ServerCallBack {
        override fun onReceiveData(data: ByteArray) {
            BridgeApplication.printerManager.sendRawData(data)
        }
    })


   inner class MyBinder : Binder() {
        fun getService(): ServerService {
            return this@ServerService
        }
    }
    override fun onBind(intent: Intent): IBinder {
        //TODO("Return the communication channel to the service.")

        return MyBinder()

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // start the server thread here
        Log.d(ServerManager.TAG, "service came.")
        CoroutineScope(Dispatchers.IO).launch {
            serverManager.start(serverCallBack)
        }
//        val data = intent?.extras?.getString("data")
//        if (data == "notstart"){
//
//        }else{
//            CoroutineScope(Dispatchers.IO).launch {
//                BridgeApplication.serverManager.start(serverCallBack)
//            }
//        }
        return START_REDELIVER_INTENT
    }
    fun getData(): String {
        if (serverManager.server !=  null){
            mData = "notnull" ;
        }else {
            mData = "null" ;
        }
        return mData
    }

    override fun onDestroy() {
        super.onDestroy()
    }

//    override fun onCreate() {
//        serverThread = MyServerThread()
//        serverThread?.start()
//        super.onCreate()
//    }


}