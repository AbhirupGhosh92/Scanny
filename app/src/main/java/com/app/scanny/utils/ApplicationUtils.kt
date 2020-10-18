package com.app.scanny.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object ApplicationUtils {

    val digest: MessageDigest = MessageDigest.getInstance("MD5")

     fun setupFullHeight(
         context: Context,
         bottomSheetDialog: BottomSheetDialog,
         rootLayout: FrameLayout
     ) {
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
            bottomSheetDialog.findViewById(
                rootLayout.id
            )!!
        )
        val layoutParams = rootLayout.layoutParams
        val windowHeight = getWindowHeight(context)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        rootLayout.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

     fun getWindowHeight(context: Context): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun md5(s: String): String? {
        try {
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(
                    0xFF and messageDigest[i]
                        .toInt()
                )
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}