package com.app.scanny.careercoop.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CcUserModel(
                        var date : Timestamp? = null,
                        var uid :  String?=null,
                        var recruiter : Boolean?=null,
                        var skills : Map<String,Boolean>? = null,
                        var location : ArrayList<String>? = null,
                        var name : String? = null,
                        var phone : String? = null,
                        var email : String? = null,
                        var projects : ArrayList<String>? = null,
                        var testionials : ArrayList<String>? = null,
                        var working : Boolean? = false) : Parcelable