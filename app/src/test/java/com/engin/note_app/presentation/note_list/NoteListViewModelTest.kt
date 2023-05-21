@file:OptIn(ExperimentalCoroutinesApi::class)

package com.engin.note_app.presentation.note_list

import com.engin.note_app.domain.model.Note
import com.engin.note_app.domain.usecases.GetAllNotesUseCase
import com.engin.note_app.util.MainDispatcherRule
import com.engin.note_app.utils.Resource
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date


class NoteListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var suv : NoteListViewModel

    private val getAllNotesUseCase = mockk<GetAllNotesUseCase>()

    @Before
    fun setUp() {

    }
    @Test
    fun `get all notes successfully`() = runTest {
        // Arrange
        val fakeList = listOf<Note>(
            Note(1L,"deneme","aciklama", Date(),null,""),
            Note(2L,"dene2me","aciklam2a", Date(),null,"")
        )
        val resultState = mutableListOf<NoteListState>()
        // Act
        val noteJob = launch {
            suv.state.toList(resultState)
        }

        coEvery { getAllNotesUseCase.invoke() } returns flowOf<Resource<List<Note>>>(
            Resource.Loading(),
            Resource.Success(fakeList)
        )

        suv = NoteListViewModel(getAllNotesUseCase)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultState.last().noteList).isEqualTo(fakeList)
        noteJob.cancel()
    }

    @Test
    fun `get all notes with error`() = runTest {
        // Arrange
        val resultState = mutableListOf<NoteListState>()
        // Act
        val noteJob = launch {
            suv.state.toList(resultState)
        }

        coEvery { getAllNotesUseCase.invoke() } returns flowOf<Resource<List<Note>>>(
            Resource.Loading(),
            Resource.Error("")
        )

        suv = NoteListViewModel(getAllNotesUseCase)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultState.last().noteList).isNull()
        noteJob.cancel()
    }
}