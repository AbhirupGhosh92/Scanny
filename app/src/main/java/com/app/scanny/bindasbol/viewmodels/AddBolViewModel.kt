package com.app.scanny.bindasbol.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import com.app.scanny.enums.ResponseEnums
import com.app.scanny.repository.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class AddBolViewModel : BaseViewModel() {

    var bolString = ""
    var bolResponse : MutableLiveData<String> =  MutableLiveData()

    fun addBol(view : View)
    {
       Repository.addBol(bolString)
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe({
               bolResponse.value = it
           },{
               it.printStackTrace()
           })
    }
}