package ru.valentin.dto.error

data class ValidationError(
    val field: String,
    val message: String,
    val rejectedValue: String? = null
)