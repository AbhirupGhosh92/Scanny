package com.app.scanny.repository

import android.util.Log
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.models.UserModel
import com.app.scanny.bindasbol.serializers.Serializer
import com.app.scanny.utils.ApplicationUtils
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

        fun addBol(bol : String,nickName: String) : Observable<String>
        {
            return Observable.create {result ->
                db.collection("bols_data")
                    .add(BolModel(
                        bol, mAuth.currentUser?.uid.toString(), nickName,Timestamp.now()
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

    fun getMyBols() : Observable<ArrayList<Pair<String,BolModel>>>
    {
        return Observable.create {result ->
            db.collection("bols_data")
                .whereEqualTo("uid", mAuth.currentUser?.uid)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { value, error ->
                    if(error == null)
                    {
                        var temp = ArrayList<Pair<String,BolModel>>()

                        for(item in value?.documents!!)
                        {
                            temp.add(Pair(item.id,Serializer.bolMapToModel(item.data as HashMap<String, Any?>)))
                        }

                        result.onNext(temp)
                    }

                }

        }
    }

    fun getAllBols(limit : Long = 50) : Observable<Pair<String,BolModel>>
    {
        return Observable.create {result ->
            db.collection("bols_data")
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnCompleteListener {task ->
                    if(task.isSuccessful)
                    {
                        for(item in task.result)
                        {
                            result.onNext(Pair(item.id,Serializer.bolMapToModel(item.data as HashMap<String, Any?>)))
                        }
                    }
                    else
                    {
                        task.exception?.printStackTrace()
                    }
                }
        }
    }

    fun likeDislike(bolId : String, likeState : Boolean, bolModel : BolModel) : Observable<String>
    {

        return Observable.create {result ->
            db.collection("bols_data")
                .document(bolId)
                .get()
                .addOnCompleteListener {task ->
                    if(task.isSuccessful)
                    {
                        var bolMap = Serializer.bolMapToModel(task.result.data as HashMap<String, Any?>)

                        if(likeState)
                        {
                            bolMap.likes = bolMap.likes?.plus(1)
                            bolMap.likeList?.add(mAuth.currentUser?.uid.toString())
                        }
                        else if(likeState.not() && bolMap.likes!! > 0)
                        {
                            bolMap.likes = bolMap.likes?.minus(1)
                            bolMap.likeList?.remove(mAuth.currentUser?.uid.toString())
                        }

                        db.collection("bols_data")
                            .document(bolId)
                            .update(Serializer.bolModelToMap(bolMap))
                            .addOnSuccessListener {
                                result.onNext("OK")
                            }
                            .addOnFailureListener {
                                result.onNext(it.message)
                                it.printStackTrace()
                            }
                    }
                    else
                    {
                        task.exception?.printStackTrace()
                    }
                }
        }
    }

    fun addComment(bol: String,nickName: String,bolId: String) : Observable<String>
    {
        return Observable.create {result ->
            db.collection("bols_data")
                .add(BolModel(
                    bol, mAuth.currentUser?.uid.toString(), nickName,Timestamp.now()
                ))
                .addOnCompleteListener {newBol ->
                    if(newBol.isSuccessful)
                    {
                        db.collection("bols_data")
                            .document(bolId)
                            .get()
                            .addOnCompleteListener {task ->
                                if(task.isSuccessful)
                                {
                                    var bolMap = Serializer.bolMapToModel(task.result.data as HashMap<String, Any?>)

                                    bolMap.comments = bolMap.comments?.plus(1)
                                    bolMap.commentList?.add(newBol.result.id)


                                    db.collection("bols_data")
                                        .document(bolId)
                                        .update(Serializer.bolModelToMap(bolMap))
                                        .addOnSuccessListener {
                                            result.onNext("OK")
                                        }
                                        .addOnFailureListener {
                                            result.onNext(it.message)
                                            it.printStackTrace()
                                        }
                                }
                                else
                                {
                                    task.exception?.printStackTrace()
                                }
                            }
                    }
                    else
                    {
                        result.onNext(newBol.exception?.message)
                    }
                }
        }
    }

    }
