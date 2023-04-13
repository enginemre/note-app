package com.engin.note_app.utils

sealed class UiEvent{
    data class ShowSnackBar(val message: String, val snackType: SnackType) : UiEvent()
    data class Navigate<T>(val route: String, val data: T? = null) : UiEvent()
}
