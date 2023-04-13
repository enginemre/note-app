package com.engin.note_app.domain.usecases

import javax.inject.Inject

class ValidateNote @Inject constructor() {

    operator fun invoke(
        title: String,
        description: String,
        imageUrl: String,
    ): ValidateNoteResult {
        if (title.isNotEmpty()) {
            if (description.isNotEmpty()) {
                return ValidateNoteResult.Success(
                    title, description, imageUrl
                )
            } else {
                return ValidateNoteResult.Error(
                    message = "Description does not empty"
                )
            }
        } else {
            return ValidateNoteResult.Error(
                message = "Title does not empty"
            )
        }
    }

    sealed class ValidateNoteResult {
        data class Success(
            val title: String,
            val description: String,
            val imageUrl: String,
        ) : ValidateNoteResult()

        data class Error(val message: String) : ValidateNoteResult()
    }
}