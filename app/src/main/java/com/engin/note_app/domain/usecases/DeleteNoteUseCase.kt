package com.engin.note_app.domain.usecases

import com.engin.note_app.domain.repository.NoteRepository
import com.engin.note_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(id:Long): Flow<Resource<Boolean>> {
        return repository.deleteNote(noteId = id)
    }
}