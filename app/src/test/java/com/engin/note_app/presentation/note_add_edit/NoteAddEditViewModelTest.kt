package com.engin.note_app.presentation.note_add_edit

import androidx.lifecycle.SavedStateHandle
import com.engin.note_app.domain.model.Note
import com.engin.note_app.domain.usecases.CreateNoteUseCase
import com.engin.note_app.domain.usecases.DeleteNoteUseCase
import com.engin.note_app.domain.usecases.GetNoteById
import com.engin.note_app.domain.usecases.NoteAddEditUseCases
import com.engin.note_app.domain.usecases.SaveNoteUseCase
import com.engin.note_app.domain.usecases.ValidateNote
import com.engin.note_app.util.MainDispatcherRule
import com.engin.note_app.utils.Resource
import com.engin.note_app.utils.SnackType
import com.engin.note_app.utils.UiEvent
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class NoteAddEditViewModelTest {


    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var suv: NoteAddEditViewModel
    private lateinit var editUseCases : NoteAddEditUseCases
    private val deleteNoteUseCase = mockk<DeleteNoteUseCase>()
    private val saveNoteUseCase = mockk<SaveNoteUseCase>()
    private val createNoteUseCase = mockk<CreateNoteUseCase>()
    private val getNoteById = mockk<GetNoteById>(relaxed = true)
    private val validateNote = spyk<ValidateNote>()


    @Before
    fun setUp() {
        val savedStateHandle = SavedStateHandle(mapOf("note_id" to 1L))
        editUseCases =   NoteAddEditUseCases(
            deleteNoteUseCase,
            saveNoteUseCase,
            createNoteUseCase,
            getNoteById, validateNote
        )
        suv = NoteAddEditViewModel(
            savedStateHandle,
            editUseCases
        )
    }

    @Test
    fun `load  note successfully with given note id`() = runTest {
        // Arrange
        val noteId = 1L
        val result = mutableListOf<NoteAddEditState>()
        val note = Note(
            noteId,
            title = "Test Note title",
            "Test Note description",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = "https://google.com.tr"
        )
        coEvery { getNoteById.invoke(noteId) } returns flowOf(Resource.Success(note))
        //  Act
        val job = launch {
            suv.state.toList(result)
        }
        suv.getNoteById(noteId)
        advanceUntilIdle()

        // Assert
        Truth.assertThat(result.last().note).isEqualTo(note)
        job.cancel()
    }

    @Test
    fun `load failed  with given note id`() = runTest {
        // Arrange
        val noteId = -1L
        val result = mutableListOf<NoteAddEditState>()
        coEvery { getNoteById.invoke(noteId) } returns flowOf(
            Resource.Loading(),
            Resource.Error(message = ""))
        //Act
        val job = launch {
            suv.state.toList(result)
        }

        suv.getNoteById(noteId)

        advanceUntilIdle()

        job.cancel()

        // Assert
        Truth.assertThat(result.last().note).isNull()
    }


    @Test
    fun `load  for creating`() = runTest {
        // Arrange
        val noteId = 0L
        val result = mutableListOf<NoteAddEditState>()
        coEvery { getNoteById.invoke(noteId) } returns flowOf(
            Resource.Loading(),
            Resource.Error(message = ""))
        //Act
        val job = launch {
            suv.state.toList(result)
        }

        suv.getNoteById(noteId)

        advanceUntilIdle()

        job.cancel()

        // Assert
        Truth.assertThat(result.last().note).isNull()
    }


    @Test
    fun `creating note when title empty`() {
        // Arrange
        val note = Note(
            id = 1L,
            title = "",
            "selam",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        // Act
        val validateResult = validateNote(note.title, note.description, note.imageUrl)
        // Assert
        Truth.assertThat(validateResult)
            .isInstanceOf(ValidateNote.ValidateNoteResult.Error::class.java)

        Truth.assertThat((validateResult as ValidateNote.ValidateNoteResult.Error).message)
            .isEqualTo("Title does not empty")
    }

    @Test
    fun `validate note successfully`() {
        // Arrange
        val note = Note(
            id = 1L,
            title = "Selam",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        // Act
        val validateResult = validateNote(note.title, note.description, note.imageUrl)
        // Assert
        Truth.assertThat(validateResult)
            .isInstanceOf(ValidateNote.ValidateNoteResult.Success::class.java)
    }

    @Test
    fun `creating note successfully`() = runTest {
        // Arrange
        val note = Note(
            id = null,
            title = "Selam",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.createNoteUseCase(note) } returns flowOf<Resource<Boolean>>(
            Resource.Loading(),
            Resource.Success(true)
        )

        suv.createNote(note.title,note.description,note.imageUrl)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.Navigate::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.Navigate<*>).route)
            .isEqualTo("/notes")
        eventJob.cancel()
    }

    @Test
    fun `creating note with invalid values`() = runTest {
        // Arrange
        val note = Note(
            id = 1L,
            title = "",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.createNoteUseCase(note) } returns flowOf<Resource<Boolean>>(
            Resource.Loading(),
            Resource.Error("Note does not saved")
        )

        suv.createNote(note.title,note.description,note.imageUrl)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.ShowSnackBar::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.ShowSnackBar).snackType)
            .isInstanceOf(SnackType.ERROR::class.java)
        eventJob.cancel()
    }



    @Test
    fun `note does not created `() = runTest {
        // Arrange
        val note = Note(
            id = null,
            title = "Selam",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = "asdas"
        )
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.createNoteUseCase(note) } returns flowOf(
            Resource.Loading(),
            Resource.Error("Note does not created")
        )

        suv.createNote(note.title,note.description,note.imageUrl)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.ShowSnackBar::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.ShowSnackBar).message)
            .isEqualTo("Note does not created")
        eventJob.cancel()
    }

    @Test
    fun `editing note with valid values and saved successfully`() = runTest {
        // Arrange
        val note = Note(
            id = 1L,
            title = "Selam",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.saveNoteUseCase(note) } returns flowOf<Resource<Int>>(
            Resource.Loading(),
            Resource.Success(1)
        )

        suv.editNote(note.title,note.description,note.imageUrl,note.createdDate)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.Navigate::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.Navigate<*>).route)
            .isEqualTo("/notes")
        eventJob.cancel()
    }

    @Test
    fun `editing note with valid values and not saved successfully`() = runTest {
        // Arrange
        val note = Note(
            id = 1L,
            title = "Selam",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.saveNoteUseCase(note) } returns flowOf<Resource<Int>>(
            Resource.Loading(),
            Resource.Error("Note does not saved")
        )

        suv.editNote(note.title,note.description,note.imageUrl,note.createdDate)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.ShowSnackBar::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.ShowSnackBar).message)
            .isEqualTo("Note does not saved")
        eventJob.cancel()
    }

    @Test
    fun `editing note with invalid values and not saved successfully`() = runTest {
        // Arrange
        val note = Note(
            id = 1L,
            title = "",
            "Açıklama :)",
            createdDate = Date(),
            updatedDate = null,
            imageUrl = ""
        )
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.saveNoteUseCase(note) } returns flowOf<Resource<Int>>(
            Resource.Loading(),
            Resource.Error("Note does not saved")
        )

        suv.editNote(note.title,note.description,note.imageUrl,note.createdDate)

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.ShowSnackBar::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.ShowSnackBar).snackType)
            .isInstanceOf(SnackType.ERROR::class.java)
        eventJob.cancel()
    }


    @Test
    fun `delete note successfully`() = runTest {
        // Arrange
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.deleteNoteUseCase(1L) } returns flowOf<Resource<Boolean>>(
            Resource.Loading(),
            Resource.Success(true)
        )

        suv.deleteNote()

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.Navigate::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.Navigate<*>).route)
            .isEqualTo("/notes")
        eventJob.cancel()
    }

    @Test
    fun `note does not delete`() = runTest {
        // Arrange
        val resultEvent = mutableListOf<UiEvent>()
        val eventJob = launch{
            suv.uiEvent.toList(resultEvent)
        }
        // Act
        coEvery { editUseCases.deleteNoteUseCase(1L) } returns flowOf<Resource<Boolean>>(
            Resource.Loading(),
            Resource.Error("Note does not deleted")
        )

        suv.deleteNote()

        advanceUntilIdle()

        // Assert
        Truth.assertThat(resultEvent.last())
            .isInstanceOf(UiEvent.ShowSnackBar::class.java)
        Truth.assertThat(( resultEvent.last() as  UiEvent.ShowSnackBar).message)
            .isEqualTo("Note does not deleted")
        eventJob.cancel()
    }
}