package com.app.scanny.bindasbol.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
data class BolModel(
    var bolId : String? = "",
    var bol : String? = "",
    var uid : String? = "",
    var nickName : String? = "",
    var dateCreated : Timestamp? = null,
    var dateModified : Timestamp? = null,
    var likes : Long? = 0,
    var likeList : ArrayList<String>?  =  ArrayList(),
    var comments : Long? = 0,
    var commentList : ArrayList<String>? = ArrayList()
)