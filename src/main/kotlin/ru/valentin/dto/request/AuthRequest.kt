package ru.valentin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Schema(description = "DTO для аутентификации или регистрации клиента API.")
data class AuthRequest(
    @Schema(description = "Обязательное не пустое имя пользователя")
    @field:NotBlank
    @Size(max = 50)
    val username: String,

    @Schema(description = "Обязательный не пустой пароль для пользователя")
    @field:NotBlank
    @Size(max = 100)
    val password: String
)