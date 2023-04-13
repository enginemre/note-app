package com.engin.note_app.domain.usecases

import javax.inject.Inject

class NoteAddEditUseCases @Inject constructor(
    val deleteNoteUseCase: DeleteNoteUseCase,
    val saveNoteUseCase: SaveNoteUseCase,
    val createNoteUseCase: CreateNoteUseCase,
    val getNoteById: GetNoteById,
    val validateNote: ValidateNote,
) {
}