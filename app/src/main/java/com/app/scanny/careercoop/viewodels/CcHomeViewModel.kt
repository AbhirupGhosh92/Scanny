package com.app.scanny.careercoop.viewodels

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.scanny.R
import com.app.scanny.bindasbol.viewmodels.BaseViewModel
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.repository.Repository
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    var testimonialLiveData = MutableLiveData<String>()
    var projectLiveData = MutableLiveData<String>()

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

    fun addProject(view : View)
    {
       var dialog =  AlertDialog.Builder(view.context)
            .setView(LayoutInflater.from(view.context).inflate(R.layout.edt_testimonials,null,false))
            .create()

        dialog.show()

        dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {
            projectLiveData.value =  dialog?.findViewById<EditText>(R.id.tv_resp).text.toString()
            dialog?.dismiss()
        }
    }

    fun addTestionial(view : View)
    {
       var dialog =  AlertDialog.Builder(view.context)
            .create()

        dialog.show()

        dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {
            testimonialLiveData.value =  dialog?.findViewById<EditText>(R.id.tv_resp).text.toString()
            dialog?.dismiss()
        }

    }


}