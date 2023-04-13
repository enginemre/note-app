package com.engin.note_app.data.repository

import com.engin.note_app.data.local.NoteDao
import com.engin.note_app.data.mapper.toNote
import com.engin.note_app.data.mapper.toNoteEntity
import com.engin.note_app.domain.model.Note
import com.engin.note_app.domain.repository.NoteRepository
import com.engin.note_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {


    override fun getAllNotes(): Flow<Resource<List<Note>>> {
        return flow {
            try {
                emit(Resource.Loading())
                val result = noteDao.getAllNotes()
                emit(Resource.Success(result.map { it.toNote() }))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    Resource.Error(
                        message = e.localizedMessage ?: "Beklenmedik bir hata oluştu"
                    )
                )
            }
        }
    }

    override fun getNoteById(id: Long): Flow<Resource<Note>> {
        return flow {
            try {
                emit(Resource.Loading())
                val result = noteDao.getNoteById(id)
                emit(Resource.Success(result.toNote()))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    Resource.Error(
                        message = e.localizedMessage ?: "Beklenmedik bir hata oluştu"
                    )
                )
            }
        }
    }

    override fun deleteNote(noteId:Long): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                noteDao.deleteNote(noteId = noteId)
                emit(Resource.Success(data = true))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    Resource.Error(
                        message = e.localizedMessage ?: "Beklenmedik bir hata oluştu"
                    )
                )
            }
        }
    }

    override fun saveNote(note: Note): Flow<Resource<Int>> {
        return flow {
            try {
                emit(Resource.Loading())
                val result = noteDao.saveNote(note.toNoteEntity())
                emit(Resource.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    Resource.Error(
                        message = e.localizedMessage ?: "Beklenmedik bir hata oluştu"
                    )
                )
            }
        }
    }

    override fun createNote(note: Note): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading())
                noteDao.insertNote(note.toNoteEntity())
                emit(Resource.Success(data = true))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    Resource.Error(
                        message = e.localizedMessage ?: "Beklenmedik bir hata oluştu"
                    )
                )
            }
        }
    }

}