package com.app.scanny.bindasbol.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.app.scanny.repository.Repository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.core.BackpressureStrategy

class BBSharedViewModel : ViewModel() {
    // TODO: Implement the ViewModel

     var authUser : FirebaseUser? = null

     fun checkAccess() : LiveData<Boolean>
     {
         return LiveDataReactiveStreams.fromPublisher(Repository.checkAcces()
              .toFlowable(BackpressureStrategy.LATEST))
     }

     fun createUser()
     {

     }
}