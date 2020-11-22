package com.app.scanny.careercoop.models

data class CcUserDetailsModel(var skills : ArrayList<String>?,
                              var location : ArrayList<String>?,
                              var name : String? = null,
                              var phone : String? = null,
                              var email : String? = null,
                              var projects : ArrayList<String>? = null,
                              var testionials : ArrayList<String>? = null,
                              var working : Boolean? = false
                              )