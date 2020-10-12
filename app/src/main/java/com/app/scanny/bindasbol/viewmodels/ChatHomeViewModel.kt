package com.app.scanny.bindasbol.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.enums.NavEnums
import com.app.scanny.repository.Repository
import io.reactivex.rxjava3.core.BackpressureStrategy

class ChatHomeViewModel : BaseViewModel() {

    var chatsAvailable: Boolean = false
    var snippet : ((nav : NavEnums) -> Unit)? = null

    fun addBols(view : View)
    {
        snippet?.invoke(NavEnums.NAV_ADD_BOLS)
    }

    fun getMyBols() : LiveData<List<BolModel>>
    {
        return LiveDataReactiveStreams.fromPublisher(
            Repository.getMyBols()
                .toFlowable(BackpressureStrategy.BUFFER)
                .buffer(50)
        )
    }
}