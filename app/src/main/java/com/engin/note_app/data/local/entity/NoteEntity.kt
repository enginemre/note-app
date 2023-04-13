package com.engin.note_app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 *
 *
 *  Room [Entity] class table that include note
 *
 */
@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    var title : String,
    var description : String,
    var imageUrl :String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdTime: String,
    val lastModifiedTime: String? = null

)
