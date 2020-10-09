package com.app.scanny.repository

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

object Repository {

    val db = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()

    fun checkAcces() : Observable<Boolean> {
        return  Observable.create {
                db.collection("user_data")
                .document(mAuth.currentUser?.uid.toString())
                    .addSnapshotListener { value, error ->

                        if (error == null)
                        {
                            if(value != null && value.exists())
                            {
                                it.onNext(true)
                            }
                            else
                            {
                                it.onNext(false)
                            }
                        }
                        else
                        {
                            Log.e("Error",error.toString())
                            it.onNext(false)
                        }
                    }
        }
    }

//    fun createUser() : Observable<String>
//    {
//
//    }
}