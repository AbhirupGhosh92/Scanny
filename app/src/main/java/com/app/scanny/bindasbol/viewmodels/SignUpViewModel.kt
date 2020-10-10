package com.app.scanny.bindasbol.viewmodels

import android.view.View
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class SignUpViewModel : BaseViewModel() {

        var showLoader = false
        var nickname : String = ""
        var errorText : MutableLiveData<String> = MutableLiveData()

        fun onClickButton(view: View) {
            if(nickname.isNullOrEmpty())
            {
                errorText.value = "Please enter a nickname"
            }
            else
            {
                errorText.value = ""
                showLoader = true
                notifyChange()
            }

        }

        fun signUp() {

        }

}