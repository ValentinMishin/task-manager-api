package ru.valentin.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class AuthRequest(
    @NotBlank
    @Size(max = 50)
    val username: String,

    @NotBlank
    @Size(max = 100)
    val password: String
)