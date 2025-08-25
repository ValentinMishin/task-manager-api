package ru.valentin.dto.response

data class AuthResponse(
    val token: String,
    val username: String,
    val roles: Set<String>
)