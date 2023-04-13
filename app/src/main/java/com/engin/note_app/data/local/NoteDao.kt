package com.engin.note_app.data.local

import androidx.room.*
import com.engin.note_app.data.local.entity.NoteEntity


/**
 *
 * DB CRUD Operation data access object interface
 *
 *
 */
@Dao
interface NoteDao {

    @Query("DELETE FROM NoteEntity WHERE id = :noteId")
    suspend fun deleteNote(noteId : Long)


    @Query("SELECT * FROM NoteEntity")
    suspend fun getAllNotes() : List<NoteEntity>

    @Query("SELECT * FROM NoteEntity WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long) : NoteEntity

    @Update
    suspend fun saveNote(note: NoteEntity) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

}