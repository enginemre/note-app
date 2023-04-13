package com.engin.note_app.presentation.note_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engin.note_app.domain.usecases.GetAllNotesUseCase
import com.engin.note_app.utils.Resource
import com.engin.note_app.utils.SnackType
import com.engin.note_app.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase
) : ViewModel() {


    private var _state = MutableStateFlow(NoteListState())
    val state = _state.asStateFlow()

    private var _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var getAllNoteJob : Job? = null


    init {
        getAllNote()
    }


    fun getAllNote() {
        getAllNoteJob?.cancel()
        getAllNoteJob = getAllNotesUseCase().onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _uiEvent.send(
                        UiEvent.ShowSnackBar(
                            message = "Notes does not fetched",
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
                            noteList = resource.data
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}