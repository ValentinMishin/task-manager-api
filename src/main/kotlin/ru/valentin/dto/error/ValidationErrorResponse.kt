package ru.valentin.dto.error

data class ValidationErrorResponse(
    val status: Int,
    val path: String,
    val errors: List<ValidationError>
)