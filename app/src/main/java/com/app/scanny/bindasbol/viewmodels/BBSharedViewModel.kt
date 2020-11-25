package com.app.scanny.bindasbol.viewmodels

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.app.scanny.R
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.models.UserModel
import com.app.scanny.careercoop.models.CcUserDetailsModel
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
import java.lang.reflect.Array

class BBSharedViewModel : BaseViewModel() {
    // TODO: Implement the ViewModel

    var userModel : UserModel?= null
    var signedIn = false
    var testimmonials : ArrayList<String> = ArrayList()
    var projects : ArrayList<String> = ArrayList()
    var cities = ArrayDeque<String>(3)
    var skills = ArrayDeque<String>(3)
    var name = ""
    var email = ""
    var phone = ""
    var showForm = false
    var loaderVisibility = View.VISIBLE
    var isRecruiter = true
    var citySelecteLiveData = MutableLiveData<String>()
    var skillSelectLiveData = MutableLiveData<String>()
    var testimonialLiveData = MutableLiveData<ArrayList<String>>()
    var projectLiveData = MutableLiveData<ArrayList<String>>()
    var isWorking = false

     fun checkAccess() : LiveData<UserModel?>
     {
         return LiveDataReactiveStreams.fromPublisher(
              Repository.checkAcces().toFlowable(BackpressureStrategy.BUFFER)
         )
     }

    fun checkCcAccess() : LiveData<CcUserModel>
    {
        return LiveDataReactiveStreams.fromPublisher(
            Repository.checkAccessCc().toFlowable(BackpressureStrategy.BUFFER))
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

        if(projectLiveData.value.isNullOrEmpty().not() &&  projectLiveData.value?.size!! < 3) {
            dialog.show()
            dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {

                    projectLiveData.value?.add(dialog?.findViewById<EditText>(R.id.tv_resp).text.toString())
                    projectLiveData.value = projectLiveData.value
                    dialog?.dismiss()
            }
        }
        else if(projectLiveData.value.isNullOrEmpty())
        {
            dialog.show()
            dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {

                    var item= ArrayList<String>()
                    item.add(dialog?.findViewById<EditText>(R.id.tv_resp).text.toString())
                    projectLiveData.value = item
                    dialog?.dismiss()

            }

        }else
        {
            Toast.makeText(view.context,view.context.resources.getString(R.string.cannot_add_pro),Toast.LENGTH_SHORT).show()
        }
    }

    fun addTestionial(view : View)
    {
        var dialog =  AlertDialog.Builder(view.context)
            .setView(LayoutInflater.from(view.context).inflate(R.layout.edt_testimonials,null,false))
            .create()

        if(testimonialLiveData.value.isNullOrEmpty().not() &&  testimonialLiveData.value?.size!! < 3) {
            dialog.show()
            dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {

                testimonialLiveData.value?.add(dialog?.findViewById<EditText>(R.id.tv_resp).text.toString())
                testimonialLiveData.value = testimonialLiveData.value
                dialog?.dismiss()
            }
        }
        else if(testimonialLiveData.value.isNullOrEmpty())
        {
            dialog.show()
            dialog?.findViewById<Button>(R.id.add)?.setOnClickListener {

                var item= ArrayList<String>()
                item.add(dialog?.findViewById<EditText>(R.id.tv_resp).text.toString())
                testimonialLiveData.value = item
                dialog?.dismiss()

            }
        }
        else
        {
            Toast.makeText(view.context,view.context.resources.getString(R.string.cannot_add_test),Toast.LENGTH_SHORT).show()
        }

    }

    fun deleteProject(position : Int)
    {
        projectLiveData.value?.removeAt(position)
        projectLiveData.value = projectLiveData.value
    }


    fun deleteTestimonial(position : Int)
    {
        testimonialLiveData.value?.removeAt(position)
        testimonialLiveData.value = testimonialLiveData.value
    }

    fun submitData(view : View)
    {
        if(name.isNullOrEmpty() || email.isNullOrEmpty() || phone.isNullOrEmpty())
        {
            Toast.makeText(view.context,view.context.resources.getString(R.string.fill_data),Toast.LENGTH_SHORT).show()
        }
        else if(skills.isNullOrEmpty() || cities.isNullOrEmpty())
        {
            Toast.makeText(view.context,view.context.resources.getString(R.string.fill_skills),Toast.LENGTH_SHORT).show()
        }
        else {

            if (isRecruiter) {
                Repository.addUserData(
                    CcUserModel(
                        Repository.mAuth.uid, isRecruiter, CcUserDetailsModel(
                            skills.toArrayList(),
                            cities.toArrayList(),
                            name,
                            phone,
                            email
                        )
                    )
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        showForm = false
                        notifyChange()
                    },{

                    })
            } else {

                Repository.addUserData(
                    CcUserModel(
                        Repository.mAuth.uid, isRecruiter, CcUserDetailsModel(
                            skills.toArrayList(),
                            cities.toArrayList(),
                            name,
                            phone,
                            email,
                            projectLiveData.value!!,
                            testimonialLiveData.value!!,
                            isWorking
                        )
                    )
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        showForm = false
                        notifyChange()
                    },{

                    })
            }
        }
    }

    private fun ArrayDeque<String>.toArrayList() : ArrayList<String>
    {
        var temp = ArrayList<String>()

        for(item in this)
        {
            temp.add(item)
        }

        return temp
    }
}