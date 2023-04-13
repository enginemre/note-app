package com.engin.note_app.data.mapper

import com.engin.note_app.data.local.entity.NoteEntity
import com.engin.note_app.domain.model.Note
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 *
 * Converting DB Entity to note entity
 *
 * @return Note
 *
 */
fun NoteEntity.toNote() : Note {
    // Parsing date according to device locale
    val formatter = SimpleDateFormat("dd/mm/yyyy", Locale.ROOT)
    val createdDate = formatter.parse(this.createdTime) ?: Date()
    val updatedDate = this.lastModifiedTime?.let {
        formatter.parse(it)
    }
    return Note(
        id = this.id!!,
        title = this.title,
        description = this.description,
        updatedDate = updatedDate ,
        createdDate = createdDate,
        imageUrl = this.imageUrl
    )
}


/**
 *
 *
 * Converting  Note to db entity
 *
 * @return NoteEntity
 *
 */
fun Note.toNoteEntity() : NoteEntity {
    // Parsing date from date to string  according to device locale
    val format = SimpleDateFormat("dd/MM/yyy", Locale.ROOT)
    val createdTime = format.format(this.createdDate)
    val updatedTime =  this.updatedDate?.let {
        format.format(it)
    }

    return  NoteEntity(
        id = this.id ,
        imageUrl = this.imageUrl,
        title = this.title,
        description = this.description,
        createdTime = createdTime,
        lastModifiedTime = updatedTime,
    )
}