package com.engin.note_app.presentation.note_add_edit

import com.engin.note_app.domain.model.Note

data class NoteAddEditState(
    var isLoading : Boolean = false,
    var note : Note? = null
)
