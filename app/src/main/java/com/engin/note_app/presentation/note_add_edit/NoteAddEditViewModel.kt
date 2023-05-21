package com.engin.note_app.presentation.note_add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engin.note_app.domain.model.Note
import com.engin.note_app.domain.usecases.NoteAddEditUseCases
import com.engin.note_app.domain.usecases.ValidateNote
import com.engin.note_app.utils.Resource
import com.engin.note_app.utils.Route
import com.engin.note_app.utils.SnackType
import com.engin.note_app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteAddEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val useCases: NoteAddEditUseCases,
) : ViewModel() {
    private var noteId: Long? = null

    private var _state = MutableStateFlow(NoteAddEditState())
    val state = _state.asStateFlow()

    private var _uiEvent = Channel<UiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    private var getNoteByIdJob: Job? = null
    private var deleteNoteJob: Job? = null
    private var editNoteJob: Job? = null
    private var createNoteJob: Job? = null

    init {
        noteId = savedStateHandle["note_id"]
        noteId?.let {
            // Coming with data part
            if (it != 0L)
                getNoteById(it)
        }
    }


    fun getNoteById(id: Long) {
        getNoteByIdJob?.cancel()
        getNoteByIdJob = useCases.getNoteById(
            noteId = id
        ).onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _uiEvent.send(
                        UiEvent.ShowSnackBar(
                            message = "Note does not fetched",
                            snackType = SnackType.ERROR
                        )
                    )
                }
                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            note = resource.data
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    fun editNote(
        title: String,
        description: String,
        imageUrl: String,
        createdDate: Date
    ) {
        val validateResult = useCases.validateNote(
            title,
            description,
            imageUrl
        )
        when(validateResult){
            is ValidateNote.ValidateNoteResult.Error -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UiEvent.ShowSnackBar(
                            message = validateResult.message,
                            snackType = SnackType.ERROR
                        )
                    )
                }
            }
            is ValidateNote.ValidateNoteResult.Success -> {
                val updatedNote = Note(
                    id = noteId!!,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    createdDate = createdDate,
                    updatedDate = Date()
                )
                editNoteJob?.cancel()
                editNoteJob = useCases.saveNoteUseCase(note = updatedNote).onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _uiEvent.send(
                                UiEvent.ShowSnackBar(
                                    message = "Note does not saved",
                                    snackType = SnackType.ERROR
                                )
                            )
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            _uiEvent.send(
                                UiEvent.ShowSnackBar(
                                    message = "Note Successfully saved",
                                    snackType = SnackType.SUCCESS
                                )
                            )
                            _uiEvent.send(
                                UiEvent.Navigate<Nothing>(
                                    route = "/notes"
                                )
                            )
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }

    }


    fun createNote(
        title: String,
        description: String,
        imageUrl: String,
    ) {
        val validateResult = useCases.validateNote(
            title,
            description,
            imageUrl
        )
        when(validateResult){
            is ValidateNote.ValidateNoteResult.Error -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UiEvent.ShowSnackBar(
                            message = validateResult.message,
                            snackType = SnackType.ERROR
                        )
                    )
                }
            }
            is ValidateNote.ValidateNoteResult.Success ->{
                val newNote = Note(
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    createdDate = Date(),
                    updatedDate = null
                )
                createNoteJob?.cancel()
                createNoteJob = useCases.createNoteUseCase(note = newNote).onEach { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _uiEvent.send(
                                UiEvent.ShowSnackBar(
                                    message = "Note does not created",
                                    snackType = SnackType.ERROR
                                )
                            )
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            _uiEvent.send(
                                UiEvent.ShowSnackBar(
                                    message = "Note successfully created",
                                    snackType = SnackType.SUCCESS
                                )
                            )
                            _uiEvent.send(
                                UiEvent.Navigate<Nothing>(
                                    route = "/notes"
                                )
                            )
                        }
                    }
                }.launchIn(viewModelScope)
            }

        }

    }

    fun deleteNote() {
        noteId?.let { id ->
            deleteNoteJob?.cancel()
            deleteNoteJob = useCases.deleteNoteUseCase(id = id).onEach { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        _uiEvent.send(
                            UiEvent.ShowSnackBar(
                                message = "Note does not deleted",
                                snackType = SnackType.ERROR
                            )
                        )
                    }
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                note = null
                            )
                        }
                        _uiEvent.send(
                            UiEvent.ShowSnackBar(
                                message = "Note Successfully deleted",
                                snackType = SnackType.SUCCESS
                            )
                        )
                        _uiEvent.send(
                            UiEvent.Navigate<Nothing>(
                                route = Route.Note
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }

    }

}

