package com.app.scanny.bindasbol.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import androidx.navigation.fragment.findNavController
import com.app.scanny.R
import com.app.scanny.bindasbol.models.UserModel
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

     fun checkAccess() : LiveData<UserModel>
     {
         return LiveDataReactiveStreams.fromPublisher(
              Repository.checkAcces().toFlowable(BackpressureStrategy.BUFFER)
         )
     }
}