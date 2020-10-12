package com.app.scanny.bindasbol.viewmodels

import android.view.View
import com.app.scanny.enums.NavEnums

class ChatHomeViewModel : BaseViewModel() {

    var chatsAvailable: Boolean = false
    var snippet : ((nav : NavEnums) -> Unit)? = null

    fun addBols(view : View)
    {
        snippet?.invoke(NavEnums.NAV_ADD_BOLS)
    }
}