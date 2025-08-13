package ru.valentin.dto.request

import java.time.LocalDate
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateTaskRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotNull(message = "Task type ID is required")
    val typeId: Long,

    val description: String? = null,

    @field:NotNull
    @field:FutureOrPresent(message = "Due date must be in present or future")
    val dueDate: LocalDate,

    // Прикрепление тегов по ID (опционально)
    val existingTagIds: Set<Long>?,

    // Создание новых тегов по названиям (опционально)
    val newTags: Set<@NotBlank NewTagDto>?
)

data class NewTagDto(
    @field:NotBlank(message = "Tag title cannot be blank")
    val title: String
)