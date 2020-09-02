package com.app.scanny.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.databinding.ActivityMainBinding
import com.app.scanny.service.ScreenCaptureService


class MainActivity : AppCompatActivity() {

    private var isBound: Boolean = false
    private lateinit var dataBinding : ActivityMainBinding
    private var screenHeight: Int = 0
    private var screenWidth: Int = 0
    private var screenDensity: Int = 0
    private  var mMediaProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection : MediaProjection? = null
    private  val CAPTURE = 1277
    private  var mMediaRecorder: MediaRecorder? = null
    private  var boundService : ScreenCaptureService? = null
    private  var captureIntent : Intent? = null
    private var captureResult : Int = 0

    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binderBridge: ScreenCaptureService.MyBinder = service as ScreenCaptureService.MyBinder
            boundService = binderBridge.getService()

            renderViews(boundService?.isRunninng!!)

            boundService?.captureIntent = captureIntent
            boundService?.result = captureResult

            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            boundService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

        var intent = Intent(this,ScreenCaptureService::class.java)
        //bindService(intent, boundServiceConnection, BIND_AUTO_CREATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val REQUEST_CODE = 101
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.data = Uri.parse("package:$packageName")
            startActivityForResult(myIntent, REQUEST_CODE)
            } else {
                TODO("VERSION.SDK_INT < M")
            }



        renderViews(false)
    }

    private fun renderViews(start : Boolean)
    {
            if(start)
            {
                dataBinding.btnService.text= "Stop Capture"
            }
            else
            {
                dataBinding.btnService.text= "Start Capture"
            }

        dataBinding.btnService.setOnClickListener {
            if(dataBinding.btnService.text == "Start Capture")
            {
                startCapture()
                dataBinding.btnService.text= "Stop Capture"
            }
            else
            {
                stopMyService()
                dataBinding.btnService.text= "Start Capture"
            }
        }
    }


    private fun startCapture()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

                val intent = mMediaProjectionManager?.createScreenCaptureIntent()
                startActivityForResult(intent, CAPTURE)

        }
    }

    private fun startMyService(data : Intent,resultCode: Int)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var intent = Intent( this,ScreenCaptureService::class.java)
            captureIntent = data
            captureResult = resultCode
            intent.action = Constants.START_SERVICE
            startForegroundService(intent)
            bindService(intent,boundServiceConnection,BIND_AUTO_CREATE)
        }
    }

    private fun stopMyService()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var intent = Intent(this, ScreenCaptureService::class.java)
            intent.action = Constants.STOP_SERVICE
            startForegroundService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isBound) {
            unbindService(boundServiceConnection)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(requestCode == CAPTURE)
            {
                startMyService(data!!,resultCode)
            }

        }
        else
            super.onActivityResult(requestCode, resultCode, data)

    }
}