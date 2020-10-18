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
    var nickname  = ""
    var bolId : String? = ""

    fun addBol(view : View)
    {
        var temp = ""+bolId
        bolId = ""

        if(temp.isNullOrEmpty()) {
            Repository.addBol(bolString, nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    bolResponse.value = it
                }, {
                    it.printStackTrace()
                })
        }
        else {
            Repository.addComment(bolString, nickname, temp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    bolResponse.value = it
                }, {
                    it.printStackTrace()
                })


        }
    }
}