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
import com.app.scanny.R
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
        var share = Intent(Intent.ACTION_SEND)
        var file = File(getDir(Environment.DIRECTORY_PICTURES,Context.MODE_PRIVATE),"temp.jpeg")
        var uri : Uri
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {

            uri = FileProvider.getUriForFile(this,getString(R.string.file_provider_authority) , file);
            share.setDataAndType(uri, "image/jpg")
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        else
        {
            share.setDataAndType(Uri.fromFile(file), "image/jpg");
        }
        startActivityForResult(Intent.createChooser(share, "Share Image"),SHARE_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode)
        {
            SHARE_INTENT -> {
                finish()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }



    }
}