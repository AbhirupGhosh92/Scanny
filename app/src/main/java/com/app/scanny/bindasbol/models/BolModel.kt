package com.app.scanny.bindasbol.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class BolModel(
    var bol : String = "",
    var uid : String = "",
    var dateCreated : Timestamp? = null,
    var dateModified : Timestamp? = null,
    var likes : Long = 0,
    var likeList : List<String>?  =  ArrayList(),
    var comments : Long = 0,
    var commentList : List<String> = ArrayList()
)