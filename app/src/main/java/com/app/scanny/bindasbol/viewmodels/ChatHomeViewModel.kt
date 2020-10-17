package com.app.scanny.bindasbol.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.enums.NavEnums
import com.app.scanny.repository.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.schedulers.Schedulers

class ChatHomeViewModel : BaseViewModel() {

    var chatsAvailable: Boolean = false
    var snippet : ((nav : NavEnums) -> Unit)? = null

    fun addBols(view : View)
    {
        snippet?.invoke(NavEnums.NAV_ADD_BOLS)
    }

    fun getBols() :  LiveData<ArrayList<Pair<String,BolModel>>>
    {
        var bolList  = MutableLiveData<ArrayList<Pair<String,BolModel>>>()

        Repository.getMyBols()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                bolList.value = it
            },{
                it.printStackTrace()
            })

        return bolList

    }


    fun addLike(bolId : String,likeState : Boolean,bolModel: BolModel) : LiveData<String>
    {
        return LiveDataReactiveStreams.fromPublisher(Repository.addLike(bolId,likeState,bolModel).toFlowable(BackpressureStrategy.BUFFER))
    }
}