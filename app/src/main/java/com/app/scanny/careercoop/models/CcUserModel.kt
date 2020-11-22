package com.app.scanny.careercoop.models

data class CcUserModel(
                        var uid :  String?=null
                       ,var recruiter : Boolean?=null
                       ,var detailsModel: CcUserDetailsModel? = null)