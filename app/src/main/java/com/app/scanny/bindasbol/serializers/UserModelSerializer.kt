package com.app.scanny.bindasbol.serializers

import com.app.scanny.bindasbol.models.BolModel
import com.app.scanny.bindasbol.models.UserModel
import com.fasterxml.jackson.databind.ser.std.MapSerializer
import com.google.firebase.Timestamp

object Serializer{

    fun bolMapToModel(map : HashMap<String,Any?>)  : BolModel
    {
        return BolModel(
            bol = map["bol"] as String,
            uid =  map["uid"] as String,
            dateCreated = map["dateCreated"] as Timestamp?,
            dateModified = map["dateModified"] as  Timestamp?,
            likes = map["likes"] as Long,
            likeList = map["likeList"] as  List<String>,
            comments = map["comments"] as Long,
            commentList =  map["commentList"] as  List<String>
        )
    }

}