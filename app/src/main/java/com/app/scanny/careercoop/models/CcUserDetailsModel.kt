package com.app.scanny.careercoop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CcUserDetailsModel(var skills : ArrayList<String>? = null,
                              var location : ArrayList<String>? = null,
                              var name : String? = null,
                              var phone : String? = null,
                              var email : String? = null,
                              var projects : ArrayList<String>? = null,
                              var testionials : ArrayList<String>? = null,
                              var working : Boolean? = false
                              ) : Parcelable