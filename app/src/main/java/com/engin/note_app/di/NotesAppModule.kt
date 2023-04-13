package com.engin.note_app.di

import com.engin.note_app.data.local.NoteDatabase
import com.engin.note_app.data.repository.NoteRepositoryImpl
import com.engin.note_app.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotesAppModule {


    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDb: NoteDatabase
    ) : NoteRepository
    {
      return  NoteRepositoryImpl(noteDb.dao)
    }


}