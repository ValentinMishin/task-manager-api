package ru.valentin.dto.error

data class ValidationErrorResponse(
    val status: Int,
    val errors: List<ValidationError>
)