package com.engin.note_app.domain.model

import com.engin.note_app.utils.toFormattedDate
import java.util.Date

data class Note(
    var id : Long? = null,
    var title : String,
    var description : String,
    var createdDate : Date,
    var updatedDate : Date?,
    var imageUrl :String
){
    fun createdDateToString() : String{
       return createdDate.toFormattedDate()
    }
    fun updatedDateToString() : String{
      return  updatedDate?.toFormattedDate() ?: ""
    }
}