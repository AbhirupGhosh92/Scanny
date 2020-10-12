package com.app.scanny.bindasbol.viewmodels

import android.view.View
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.scanny.repository.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
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
                signUp()
                notifyChange()
            }

        }

        private fun signUp() {
            Repository.createUser(nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    errorText.value = it
                    showLoader = false
                    notifyChange()
                },{
                    it.printStackTrace()
                    showLoader = false
                    notifyChange()
                    errorText.value = it.message
                })

        }

}