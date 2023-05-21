package com.engin.note_app.domain.usecases

import javax.inject.Inject

class ValidateNote @Inject constructor() {

    operator fun invoke(
        title: String,
        description: String,
        imageUrl: String,
    ): ValidateNoteResult {
        if (title.isEmpty()) {
            return ValidateNoteResult.Error(
                message = "Title does not empty"
            )
        }
        if (description.isEmpty()) {
            return ValidateNoteResult.Error(
                message = "Description does not empty"
            )
        }
        return ValidateNoteResult.Success(
            title, description, imageUrl
        )

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