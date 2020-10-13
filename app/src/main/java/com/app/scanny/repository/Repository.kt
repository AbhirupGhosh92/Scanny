package com.app.scanny.repository

import android.util.Log
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.models.UserModel
import com.app.scanny.bindasbol.serializers.Serializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import io.reactivex.rxjava3.core.Observable

object Repository {


    val settings =  FirebaseFirestoreSettings.Builder()
        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        .build()
    val db = FirebaseFirestore.getInstance().apply {
        firestoreSettings = settings
    }

    val mAuth = FirebaseAuth.getInstance()
    val objectMapper = ObjectMapper()

    fun checkAcces() : Observable<UserModel> {
        return  Observable.create {result  ->
                    db.collection("user_data")
                    .whereEqualTo("uid",mAuth.currentUser?.uid.toString())
                        .get().addOnCompleteListener {task  ->

                            if (task.isSuccessful)
                            {
                                task.result?.forEach {
                                    result.onNext(
                                        objectMapper.convertValue(it.data,UserModel::class.java)
                                    )
                                }
                            }
                            else
                            {
                                Log.e("Error",task.exception.toString())
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

        fun addBol(bol : String) : Observable<String>
        {
            return Observable.create {result ->
                db.collection("bols_data")
                    .add(BolModel(
                        bol, mAuth.currentUser?.uid.toString(), Timestamp.now()
                    ))
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
        }

    fun getMyBols() : Observable<BolModel>
    {
        return Observable.create {result ->
            db.collection("bols_data")
                .whereEqualTo("uid", mAuth.currentUser?.uid)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener {task ->
                    if(task.isSuccessful)
                    {
                       task.result?.forEach {
                           result.onNext(Serializer.bolMapToModel(it.data as HashMap<String, Any?>))
                       }
                    }
                    else
                    {
                        task.exception?.printStackTrace()
                    }
                }
        }
    }

    }
