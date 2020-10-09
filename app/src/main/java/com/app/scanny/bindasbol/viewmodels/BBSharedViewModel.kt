package com.app.scanny.bindasbol.viewmodels

import androidx.lifecycle.ViewModel
import com.app.scanny.repository.Repository
import com.google.firebase.auth.FirebaseUser

class BBSharedViewModel : ViewModel() {
    // TODO: Implement the ViewModel

     var authUser : FirebaseUser? = null

     fun checkAccess() :
     {
          Repository.checkAcces()
     }
}