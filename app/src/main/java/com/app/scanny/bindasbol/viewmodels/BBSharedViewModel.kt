package com.app.scanny.bindasbol.viewmodels

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.app.scanny.R
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.models.UserModel
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.repository.Repository
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.reactivestreams.Publisher

class BBSharedViewModel : BaseViewModel() {
    // TODO: Implement the ViewModel

     var userModel : UserModel?= null
    var signedIn = false

    var cities = ArrayDeque<String>(3)
    var skills = ArrayDeque<String>(3)
    var name = ""
    var email = ""
    var phone = ""
    var showForm = false
    var isRecruiter = true
    var citySelecteLiveData = MutableLiveData<String>()
    var skillSelectLiveData = MutableLiveData<String>()
    var testimonialLiveData = MutableLiveData<String>()
    var projectLiveData = MutableLiveData<String>()

     fun checkAccess() : LiveData<UserModel?>
     {
         return LiveDataReactiveStreams.fromPublisher(
              Repository.checkAcces().toFlowable(BackpressureStrategy.BUFFER)
         )
     }

    fun checkCcAccess() : LiveData<CcUserModel>
    {
        return LiveDataReactiveStreams.fromPublisher(Repository.checkAccessCc().toFlowable(BackpressureStrategy.BUFFER))
    }

    fun citiesClick(view  : View)
    {
        AlertDialog.Builder(view.context)
            .setAdapter(
                ArrayAdapter<String>(view.context, R.layout.spinner_text, Repository.getCityList())
            ) { p0, p1 ->
                citySelecteLiveData.value =  Repository.getCityList()[p1]
            }.create()
            .show()
    }

    fun skillsClick(view : View)
    {
        AlertDialog.Builder(view.context)
            .setAdapter(
                ArrayAdapter<String>(view.context, R.layout.spinner_text,Repository.getSkillsList())
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
            .setView(LayoutInflater.from(view.context).inflate(R.layout.edt_testimonials,null,false))
            .create()

        dialog.show()

        dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {
            testimonialLiveData.value =  dialog?.findViewById<EditText>(R.id.tv_resp).text.toString()
            dialog?.dismiss()
        }

    }
}