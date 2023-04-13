package com.engin.note_app.di

import android.app.Application
import androidx.room.Room
import com.engin.note_app.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotesLocalModule {


    @Singleton
    @Provides
    fun provideNoteDatabase(
        application: Application,
    )  : NoteDatabase {
        return Room.databaseBuilder(
            application,
            NoteDatabase::class.java,
            "notes"
        ).build()
    }
}