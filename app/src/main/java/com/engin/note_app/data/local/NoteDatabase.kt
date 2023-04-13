package com.engin.note_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.engin.note_app.data.local.entity.NoteEntity


@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val dao: NoteDao
}