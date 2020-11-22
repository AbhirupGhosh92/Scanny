package com.app.scanny.repository

import android.util.Log
import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.models.UserModel
import com.app.scanny.bindasbol.serializers.Serializer
import com.app.scanny.careercoop.models.CcUserDetailsModel
import com.app.scanny.careercoop.models.CcUserModel
import com.app.scanny.utils.ApplicationUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Observable

object Repository {


    val settings =  FirebaseFirestoreSettings.Builder()
        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        .setPersistenceEnabled(true)
        .build()
    val db = FirebaseFirestore.getInstance().apply {
        firestoreSettings = settings
    }

    var remoteConfig : FirebaseRemoteConfig? = null


    val mAuth = FirebaseAuth.getInstance()
    val objectMapper = ObjectMapper()

     fun  getCityList() : List<String>
    {
       return Gson().fromJson<List<String>>(remoteConfig?.getString("city_list"),object : TypeToken<List<String>>(){}.type)
    }

    fun getSkillsList() : List<String>
    {
        return  Gson().fromJson<List<String>>(remoteConfig?.getString("skills_list"),object : TypeToken<List<String>>(){}.type)
    }

    private fun parseCcUser(map : MutableMap<String,Any>)  : CcUserModel
    {
        var temp = CcUserModel()

        temp.uid = map["uid"] as String?
        temp.recruiter =  map["recruiter"] as Boolean?

        var detail = map["detailsModel"] as MutableMap<*, *>

        temp.detailsModel = CcUserDetailsModel()
        temp.detailsModel?.email = detail["email"] as String?
        temp.detailsModel?.location = detail["location"] as ArrayList<String>?
        temp.detailsModel?.skills = detail["skills"] as ArrayList<String>?
        temp.detailsModel?.name = detail["name"] as String?
        temp.detailsModel?.phone = detail["phone"] as String?
        temp.detailsModel?.working = detail["working"] as Boolean?
        temp.detailsModel?.projects = detail["projects"] as  ArrayList<String>?
        temp.detailsModel?.testionials = detail["testionials"] as  ArrayList<String>?

        return temp
    }

    fun addUserData(data : CcUserModel) : Observable<String>
    {
        return Observable.create { result ->
            db.collection("cc_user_data")
                .document(data.uid.toString())
                .set(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.onNext("OK")
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }

    fun checkAccessCc() : Observable<CcUserModel>
    {
        return Observable.create {result ->
            db.collection("cc_user_data")
                .whereEqualTo("uid",mAuth.currentUser?.uid.toString())
                .get().addOnCompleteListener {task  ->

                    if (task.isSuccessful)
                    {
                        if(task.result != null && task.result.isEmpty.not()) {
                            task.result?.forEach {
                                result.onNext(
                                   parseCcUser(it.data)
                                )
                            }
                        }
                        else
                        {
                            result.onNext(CcUserModel())
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

    fun checkAcces() : Observable<UserModel?> {
        return  Observable.create {result  ->
                    db.collection("user_data")
                    .whereEqualTo("uid",mAuth.currentUser?.uid.toString())
                        .get().addOnCompleteListener {task  ->

                            if (task.isSuccessful)
                            {
                                if(task.result != null && task.result.isEmpty.not()) {
                                    task.result?.forEach {
                                        result.onNext(
                                            objectMapper.convertValue(
                                                it.data,
                                                UserModel::class.java
                                            )
                                        )
                                    }
                                }
                                else
                                {
                                    result.onNext(UserModel())
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

    fun getCommentsList(commentList : List<String>,limit: Long  = 50) : Observable<ArrayList<BolModel>>
    {
        var respModel = ArrayList<BolModel>()

        return Observable.create {result ->

            db.collection("bols_data")
                .whereIn("bolId",commentList)
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
