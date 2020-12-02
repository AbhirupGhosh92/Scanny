package com.app.scanny.careercoop.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CcUserModel(
                        var date : Timestamp? = null,
                        var uid :  String?=null
                       ,var recruiter : Boolean?=null
                       ,var detailsModel: CcUserDetailsModel? = null) : Parcelable