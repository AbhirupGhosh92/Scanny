package com.app.scanny.careercoop.viewodels

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.scanny.R
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
    var citySelecteLiveData = MutableLiveData<String>()
    var skillSelectLiveData = MutableLiveData<String>()

    fun checkCcAccess() : LiveData<CcUserModel>
    {
        return LiveDataReactiveStreams.fromPublisher(Repository.checkAccessCc().toFlowable(BackpressureStrategy.BUFFER))
    }

    fun citiesClick(view  : View)
    {
        AlertDialog.Builder(view.context)
            .setAdapter(ArrayAdapter<String>(view.context, R.layout.spinner_text, Repository.getCityList())
            ) { p0, p1 ->
               citySelecteLiveData.value =  Repository.getCityList()[p1]
            }.create()
            .show()
    }

    fun skillsClick(view : View)
    {
        AlertDialog.Builder(view.context)
            .setAdapter(ArrayAdapter<String>(view.context, R.layout.spinner_text,Repository.getSkillsList())
            ) { p0, p1 ->
                skillSelectLiveData.value =  Repository.getSkillsList()[p1]
            }.create()
            .show()
    }


}