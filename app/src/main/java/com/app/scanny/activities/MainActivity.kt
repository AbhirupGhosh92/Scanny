package com.app.scanny.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.app.scanny.BuildConfig
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.databinding.ActivityMainBinding
import com.app.scanny.service.ScreenCaptureService
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    private var isBound: Boolean = false
    private lateinit var dataBinding : ActivityMainBinding
    private var screenHeight: Int = 0
    private var screenWidth: Int = 0
    private var screenDensity: Int = 0
    private  var mMediaProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection : MediaProjection? = null
    private  val CAPTURE = 1277
    private  val BIND = 127
    private  val STORAGE = 1276
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


        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
           requestSharePermission()
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,android.Manifest.permission.READ_EXTERNAL_STORAGE),STORAGE)
            }
        }


    }

    private fun requestSharePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val REQUEST_CODE = 101
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.data = Uri.parse("package:$packageName")
            startActivityForResult(myIntent, REQUEST_CODE)
        }
        else
        {
           startBind()
        }
    }

    private fun bindMyService(data : Intent,resultCode: Int)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var intent = Intent( this,ScreenCaptureService::class.java)
            captureIntent = data
            captureResult = resultCode
            intent.action = Constants.START_SERVICE
            bindService(intent,boundServiceConnection,BIND_AUTO_CREATE)
        }
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


        dataBinding.btnLocation.setOnClickListener {


            var dir = File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Pictures/Scanny")
            if(!dir.exists())
            {
                dir.mkdir()
            }
            val share = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(dir.toUri(), "/")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                share.putExtra(
                    Intent.EXTRA_STREAM,
                    dir.toUri())
            } else {
                share.putExtra(
                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        applicationContext, BuildConfig.APPLICATION_ID + ".provider", dir
                    )
                )
            }
            startActivity(share)
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

    private fun startBind()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            val intent = mMediaProjectionManager?.createScreenCaptureIntent()
            startActivityForResult(intent, BIND)

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

            when(requestCode)
            {
                CAPTURE -> {
                    if(resultCode == -1)
                    {

                        startMyService(data!!,resultCode)
                    }
                    else
                    {
                        AlertDialog.Builder(this)
                            .setMessage("You must provide screen share permissions to capture screenshot")
                            .setNegativeButton("Ok"
                            ) { p0, p1 ->
                                p0.dismiss()
                            }
                            .show()
                    }
                }

                BIND -> {
                    if(resultCode == -1)
                    {

                        bindMyService(data!!,resultCode)
                    }
                }
            }


        }
        else
            super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode)
        {
            STORAGE  -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    requestSharePermission()
                }
                else
                {
                    AlertDialog.Builder(this)
                        .setMessage("You must provide screen storage permissions to save screenshot")
                        .setNegativeButton("Ok"
                        ) { p0, p1 ->
                            p0.dismiss()
                        }
                        .show()
                }
            }
        }
    }
}