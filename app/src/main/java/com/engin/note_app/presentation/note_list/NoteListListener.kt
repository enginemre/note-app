package com.engin.note_app.presentation.note_list

interface NoteListListener {
    fun onClickNote(id : Long)

    fun onAddNote()
}