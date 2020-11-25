package com.app.scanny.careercoop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CcUserModel(
                        var uid :  String?=null
                       ,var recruiter : Boolean?=null
                       ,var detailsModel: CcUserDetailsModel? = null) : Parcelable