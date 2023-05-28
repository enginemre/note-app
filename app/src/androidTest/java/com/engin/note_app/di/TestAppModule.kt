package com.engin.note_app.di

import com.engin.note_app.data.repository.FakeRepository
import com.engin.note_app.domain.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TestAppModule{


    @Provides
    @Named("fake_repository")
    @Singleton
    fun provideNoteRepository(
    ) : NoteRepository
    {
        return  FakeRepository()
    }

}