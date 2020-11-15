package com.app.scanny.careercoop.viewodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.app.scanny.bindasbol.viewmodels.BaseViewModel
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.repository.Repository
import io.reactivex.rxjava3.core.BackpressureStrategy

class CcHomeViewModel : BaseViewModel()
{
    var name = ""
    var email = ""
    var phone = ""
    var showForm = false
    var isRecruiter = true

    fun checkCcAccess() : LiveData<CcUserModel>
    {
        return LiveDataReactiveStreams.fromPublisher(Repository.checkAccessCc().toFlowable(BackpressureStrategy.BUFFER))
    }


}