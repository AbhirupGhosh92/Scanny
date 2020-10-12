package com.app.scanny.utils

import android.R
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


object ApplicationUtils {

     fun setupFullHeight(context: Context , bottomSheetDialog: BottomSheetDialog,rootLayout : FrameLayout) {
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetDialog.findViewById(rootLayout.id)!!)
        val layoutParams = rootLayout.layoutParams
        val windowHeight = getWindowHeight(context)
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        rootLayout.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

     fun getWindowHeight(context : Context): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}