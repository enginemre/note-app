package com.engin.note_app.domain.usecases

import com.engin.note_app.domain.model.Note
import com.engin.note_app.domain.repository.NoteRepository
import com.engin.note_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor(
  private val repository: NoteRepository
) {
    operator fun invoke(): Flow<Resource<List<Note>>> {
        return repository.getAllNotes()
    }
}