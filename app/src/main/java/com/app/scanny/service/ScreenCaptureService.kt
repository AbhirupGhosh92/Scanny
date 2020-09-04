package com.app.scanny.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.activities.MainActivity
import com.app.scanny.activities.TransparentActivity
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


class ScreenCaptureService : Service() {

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenDensity: Int = 0
    private val channelId = "trace"
    private val OPEN_ACTIVITY = 123
    private lateinit var view: View
    private lateinit var  windowManager : WindowManager
    private  var mMediaProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection : MediaProjection? = null
    private  val CAPTURE = 1277
    private  var mMediaRecorder: MediaRecorder? = null
    private var data : Bundle? = null
    val myService = this
    var captureIntent : Intent? =null
    var result : Int = 0
    var isRunninng = false
    var metrics : DisplayMetrics? = null

   inner class MyBinder : Binder(){

        fun getService() : ScreenCaptureService
        {
            return myService
        }
    }

    private val localBinder: IBinder = MyBinder()

    private val shareCallback :  () -> Unit = {
        var intent = Intent(this,TransparentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }



    override fun onBind(p0: Intent?): IBinder? {
        return localBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        isRunninng = true

        when(intent?.action) {

            Constants.START_SERVICE -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                    createNotificationChannel()

                    val pendingIntent =
                        PendingIntent.getActivity(this, OPEN_ACTIVITY, Intent(
                            this,
                            MainActivity::class.java
                        )
                            .apply {
                                action = Intent.ACTION_MAIN
                                addCategory(Intent.CATEGORY_DEFAULT)
                            }
                            , 0
                        )

                    val notification = NotificationCompat.Builder(this, channelId)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Capture screen")
                        .build()

                    startForeground(1, notification)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        renderView()
                    }
                }
            }

            Constants.STOP_SERVICE -> {
                stopForeground(true);
                stopSelfResult(startId);
                if(::view.isInitialized)
                    view.visibility = View.GONE
            }

            Constants.SHOW_BUTTON -> {
                if(::view.isInitialized)
                    view.visibility = View.VISIBLE
            }

        }


        return START_STICKY
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderView()
    {
        var inflater =  getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view =  inflater.inflate(R.layout.customm_view,null)

        view.findViewById<Button>(R.id.btn_capture).setOnClickListener {
           view.visibility = View.GONE
            CoroutineScope(Dispatchers.Main).launch {
                startRecord()
            }
        }


        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        var LAYOUT_FLAG = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        var layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
           LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)

        layoutParams.gravity = Gravity.BOTTOM

        windowManager.addView(view, layoutParams)
    }

    private fun createNotificationChannel()
    {
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId,"captureService",NotificationManager.IMPORTANCE_DEFAULT)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val manager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           getSystemService(NotificationManager ::class.java)
        } else {
            TODO("VERSION.SDK_INT < M")
        }

        manager?.createNotificationChannel(channel)
    }

    override fun onDestroy() {

        isRunninng = false

        if(::view.isInitialized)
            view.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
     suspend  fun startRecord() {
        mMediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

         mMediaProjection = mMediaProjectionManager?.getMediaProjection(result, captureIntent!!)

        getSize()

        var mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)
        mMediaProjection?.createVirtualDisplay(
            "Recording capture",
            screenWidth,
            screenHeight,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader.surface,
            null,
            null
        )

        delay(1000)

        var image = mImageReader.acquireLatestImage()


        if(image !=null) {
            withContext(Dispatchers.Default){
            val planes: Array<Image.Plane> = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * screenWidth
            val bmp = Bitmap.createBitmap(
                metrics?.widthPixels!! + (rowPadding.toFloat() / pixelStride.toFloat()).toInt(),
                metrics?.heightPixels!!,
                Bitmap.Config.ARGB_8888
            )
            bmp.copyPixelsFromBuffer(buffer)
            stopRecord()

            // var bitmaps = withContext(Dispatchers.Default){processBitmap(bmp)}

             withContext(Dispatchers.IO)
                {
                    writeBitmaps(bitmap = bmp)
                }

                withContext(Dispatchers.Main)
                {
                    //shareBitmap()
                    shareCallback.invoke()
                    //view.visibility = View.VISIBLE
                }
        }
        }
        else
        {
            view.visibility = View.VISIBLE
            Toast.makeText(this,"No Image Captured",Toast.LENGTH_SHORT).show()
        }
    }


    private fun writeBitmaps(bitmap: Bitmap? = null ,bitMapList : List<Bitmap>? = null)
    {
        try {

            when{
                bitmap != null && bitMapList.isNullOrEmpty() ->
                {
                    var byteStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
                    var file = File(Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "temporary_file.jpg")
                    file.createNewFile()
                    var fos = FileOutputStream(file)
                    fos.write(byteStream.toByteArray())
                    fos.close()

                }

                bitmap == null && bitMapList.isNullOrEmpty().not() -> {
                    for (item in bitMapList?.indices!!)
                    {
                        var byteStream = ByteArrayOutputStream()
                        bitMapList[item].compress(Bitmap.CompressFormat.JPEG, 100, byteStream)
                        var dir = getDir(Environment.DIRECTORY_PICTURES,Context.MODE_PRIVATE)
                        var file = File(dir,"${item}_temp.jpeg")
                        file.createNewFile()
                        var fos = FileOutputStream(file)
                        fos.write(byteStream.toByteArray())
                        fos.close()
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }



    private fun processBitmap(bitmap: Bitmap,horizontalSlices : Int = 13) : ArrayList<Bitmap>
    {
        var height  = bitmap.height
        var prevHeight = 0
        var bitmaps = arrayListOf<Bitmap>()

        var heightSlice = height / horizontalSlices

        Log.e("Dims","Height:${height}")

        try {
            for (i in 0 until horizontalSlices) {
                if (i == horizontalSlices - 1) {

                    Log.e("Dims","$prevHeight,${height}")

                            bitmaps.add(
                                Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    prevHeight,
                                    bitmap.width,
                                    height - prevHeight
                                )
                            )
                } else {
                    bitmaps.add(
                        Bitmap.createBitmap(
                            bitmap,
                            0,
                            prevHeight,
                            bitmap.width,
                            heightSlice
                        )
                    )
                    Log.e("Dims","$prevHeight,${heightSlice + prevHeight}")
                    prevHeight += heightSlice + 1
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return bitmaps
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopRecord() {
        if (mMediaRecorder != null) {
            try {
                SystemClock.sleep(500)
                mMediaRecorder?.stop()
                mMediaRecorder?.reset()
                mMediaRecorder?.release()
                mMediaRecorder = null
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
        if (mMediaProjectionManager != null) {
            mMediaProjectionManager = null
        }
        if (mMediaProjection != null) {
            mMediaProjection?.stop()
            mMediaProjection = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initRecorder() {
        try {
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mMediaRecorder?.setOutputFile( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + "/myRecorded.mp4")
            mMediaRecorder?.setVideoSize(screenWidth, screenHeight)
            mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder?.setVideoEncodingBitRate(512 * 1000)
            mMediaRecorder?.setVideoFrameRate(30)
            mMediaRecorder?.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getPath(format: String): String? {
        val path: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString()
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        var file = path +"/temp"+ format

        return file
    }

    private fun getSize() {
        metrics = resources.displayMetrics
        screenWidth = metrics?.widthPixels!!
        screenHeight = metrics?.heightPixels!!
        screenDensity = metrics?.densityDpi!!
    }
}
