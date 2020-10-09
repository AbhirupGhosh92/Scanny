package com.app.scanny.bindasbol.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {

    var showLoader = false

    fun onClickButton(view: View) {
        showLoader = true
    }


    fun signUp()
    {

    }
}