package com.engin.note_app.presentation.note_list

import com.engin.note_app.domain.model.Note

data class NoteListState(
    var isLoading: Boolean  =false,
    var noteList  : List<Note>? = null,
)
