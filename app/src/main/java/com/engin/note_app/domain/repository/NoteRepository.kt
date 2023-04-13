package com.engin.note_app.domain.repository

import com.engin.note_app.domain.model.Note
import com.engin.note_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotes() : Flow<Resource<List<Note>>>

    fun getNoteById(id:Long) : Flow<Resource<Note>>

    fun deleteNote(noteId: Long) : Flow<Resource<Boolean>>

    fun saveNote(note: Note) : Flow<Resource<Int>>

    fun createNote(note: Note) : Flow<Resource<Boolean>>
}