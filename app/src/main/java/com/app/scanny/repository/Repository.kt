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
        .setPersistenceEnabled(true)
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
            val timestamp = Timestamp.now()
            val hash = ApplicationUtils.md5("${mAuth.currentUser?.uid}${timestamp.toDate().time}")
            val bolModel = BolModel(hash,
                bol, mAuth.currentUser?.uid.toString(), nickName,timestamp
            )

            return Observable.create {result ->
                db.collection("bols_data")
                    .document(hash!!)
                    .set(bolModel)
                    .addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            result.onNext("OK")
                        }
                        else
                        {
                            it.exception?.printStackTrace()
                        }

                    }
            }
        }

    fun getMyBols() : Observable<ArrayList<BolModel>>
    {
        return Observable.create {result ->
            db.collection("bols_data")
                .whereEqualTo("uid", mAuth.currentUser?.uid)
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { value, error ->
                    if(error == null)
                    {
                        var temp = ArrayList<BolModel>()

                        for(item in value?.documents!!)
                        {
                            temp.add(Serializer.bolMapToModel(item.data as HashMap<String, Any?>))
                        }

                        result.onNext(temp)
                    }

                }

        }
    }

//    fun getCommentsList(commentList : List<String>) : Observable<ArrayList<Pair<String,BolModel>>>
//    {
//        var respModel = ArrayList<Pair<String,BolModel>>()
//
//        return Observable.create {result ->
//
//            db.collection("bols_data")
//                .get(So)d
//                .orderBy("dateCreated", Query.Direction.DESCENDING)
//                .limit(limit)
//                .addSnapshotListener {value, error ->
//                    if(error == null)
//                    {
//                        var temp = ArrayList<Pair<String,BolModel>>()
//                        for(item in value?.documents!!)
//                        {
//                            temp.add(Pair(item.id,Serializer.bolMapToModel(item.data as HashMap<String, Any?>)))
//                        }
//
//                        result.onNext(temp)
//                    }
//
//                }
//        }
//    }

    fun getAllBols(limit : Long = 50) : Observable<ArrayList<BolModel>>
    {
        return Observable.create {result ->
            db.collection("bols_data")
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(limit)
                .addSnapshotListener {value, error ->
                    if(error == null)
                    {
                        var temp = ArrayList<BolModel>()
                        for(item in value?.documents!!)
                        {
                            temp.add(Serializer.bolMapToModel(item.data as HashMap<String, Any?>))
                        }

                        result.onNext(temp)
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

                        var data = task.result.data
                        if(data != null) {
                            var bolMap =
                                Serializer.bolMapToModel(data as HashMap<String, Any?>)

                            if (likeState) {
                                bolMap.likes = bolMap.likes?.plus(1)
                                bolMap.likeList?.add(mAuth.currentUser?.uid.toString())
                            } else if (likeState.not() && bolMap.likes!! > 0) {
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
        val timestamp = Timestamp.now()
        val hash = ApplicationUtils.md5("${mAuth.currentUser?.uid}${timestamp.toDate().time}")
        val bolModel = BolModel(hash,
            bol, mAuth.currentUser?.uid.toString(), nickName,timestamp
        )

        return Observable.create {result ->
            db.collection("bols_data")
                .document(hash!!)
                .set(bolModel)
                .addOnCompleteListener {newBol ->
                    if(newBol.isSuccessful)
                    {
                        db.collection("bols_data")
                            .document(bolId)
                            .get()
                            .addOnCompleteListener {task ->
                                if(task.isSuccessful)
                                {
                                    var data = task.result.data
                                    if(data != null) {
                                        var bolMap =
                                            Serializer.bolMapToModel(data as HashMap<String, Any?>)

                                        bolMap.comments = bolMap.comments?.plus(1)
                                        bolMap.commentList?.add(bolModel.bolId!!)


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
