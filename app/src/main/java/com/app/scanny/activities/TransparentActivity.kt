package com.app.scanny.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import com.app.scanny.R

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
        share.type = "image/jpeg"
        share.putExtra(Intent.EXTRA_STREAM, getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE).toURI())
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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