package com.app.scanny.careercoop.models

data class CcUserDetailsModel(var skills : List<String>?,
                              var location : List<String>?,
                              var phone : String? = null,
                              var email : String? = null,
                              var projects : List<String>,
                              var testionials : List<String>
                              )