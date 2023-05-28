package com.engin.note_app.data.repository

import com.engin.note_app.domain.model.Note
import com.engin.note_app.domain.repository.NoteRepository
import com.engin.note_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class FakeRepository  : NoteRepository{

    companion object{
        val notes = arrayListOf<Note>(
            Note(
                1L,
                "Deneme Notu 1",
                description = "Bu bir deneme notu",
                createdDate = Date(),
                updatedDate = null,
                imageUrl = ""
            ),
            Note(
                2L,
                "Deneme Notu 2'",
                description = "Bu bir deneme notu",
                createdDate = Date(),
                updatedDate = null,
                imageUrl = ""
            ),
            Note(
                3L,
                "Deneme Notu 3'",
                description = "Bu bir deneme notu",
                createdDate = Date(),
                updatedDate = null,
                imageUrl = ""
            )
        )
    }

    override fun getAllNotes(): Flow<Resource<List<Note>>> {
        return flow{
            emit(Resource.Success(notes))
        }
    }

    override fun getNoteById(id: Long): Flow<Resource<Note>> {
        return flow {
            val note = notes.find { it.id == id }
            note?.let {
                emit(Resource.Success(note))
            } ?: run {
                emit(Resource.Error("Data not found"))
            }
        }
    }

    override fun deleteNote(noteId: Long): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Success(true))
        }
    }

    override fun saveNote(note: Note): Flow<Resource<Int>> {
        return flow {
            val position = notes.indexOf(note)
            notes[position] = note
            note.id?.let {
                emit(Resource.Success(it.toInt()))
            }

        }
    }

    override fun createNote(note: Note): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Success(true))
        }
    }
}