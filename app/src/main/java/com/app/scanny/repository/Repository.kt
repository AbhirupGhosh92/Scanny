package com.app.scanny.repository

import android.util.Log
import android.widget.Toast
import com.app.scanny.bindasbol.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

object Repository {

    val db = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    val gson = Gson()

    fun checkAcces() : Observable<UserModel> {
        return  Observable.create {result  ->
                    db.collection("user_data")
                    .whereEqualTo("uid",mAuth.currentUser?.uid.toString())
                        .get().addOnCompleteListener {

                            if (it.isSuccessful)
                            {
                                it.result?.forEach {
                                    result.onNext(
                                      gson.fromJson(gson.toJsonTree(it.data,Map::class.java),UserModel::class.java)
                                    )
                                }
                            }
                            else
                            {
                                Log.e("Error",it.exception.toString())
                                result.onNext(null)
                            }
                        }
                    }
        }


    fun createUser(nickName : String) : Observable<String> {
        return Observable.create { result ->

            db.collection("user_data")
                .whereEqualTo("nickName",nickName)
                .get()
                .addOnCompleteListener {
                    if ( it.isComplete)
                    {
                            db.collection("user_data")
                                .add(UserModel(mAuth.currentUser?.uid.toString(),nickName))
                                .addOnCompleteListener {
                                    if(it.isSuccessful)
                                    {
                                        result.onNext("OK")
                                    }
                                    else
                                    {
                                        result.onNext(it.exception?.message)
                                    }
                                }
                        }
                        else
                        {
                            result.onNext("Nickname already exists")
                        }
                    }


                }


        }
    }
