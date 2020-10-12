package com.app.scanny.bindasbol.models

import java.util.*
import kotlin.collections.ArrayList

data class BolModel(
    var bol : String,
    var uid : String,
    var dateCreated : Date,
    var dateModified : Date? = null,
    var likes : Int = 0,
    var likeList : List<String>?  =  ArrayList(),
    var comments : Int = 0,
    var commentList : List<String> = ArrayList()
)