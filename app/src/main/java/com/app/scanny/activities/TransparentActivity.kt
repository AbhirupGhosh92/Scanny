package com.app.scanny.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import androidx.core.content.FileProvider
import com.app.scanny.BuildConfig
import com.app.scanny.Constants
import com.app.scanny.R
import com.app.scanny.service.ScreenCaptureService
import java.io.File

class TransparentActivity : AppCompatActivity() {

    private val SHARE_INTENT = 252

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trannsparent)
        shareBitmap()
    }


    private fun shareBitmap()
    {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            share.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse("file:///sdcard/temporary_file.jpg"))
        } else {
            share.putExtra(
                Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                    applicationContext, BuildConfig.APPLICATION_ID + ".provider", File(
                        Environment.getExternalStorageDirectory()
                            .toString() + File.separator + "temporary_file.jpg"
                    )
                )
            )
        }
        var shareInt = Intent.createChooser(share, "Share Image")
        shareInt.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivityForResult(shareInt,SHARE_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode)
        {
            SHARE_INTENT -> {
                finish()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var intent = Intent( this, ScreenCaptureService::class.java)
                    intent.action = Constants.START_SERVICE
                    startForegroundService(intent)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }



    }
}