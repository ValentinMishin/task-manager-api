package ru.valentin.dto.request

import java.time.LocalDate
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank

//хотя бы одно поле должно быть не нул
data class UpdateTaskRequest(
    val title: String?,

    val typeId: Long?,

    val description: String?,

    @field:FutureOrPresent(message = "Due date must be present or future")
    val dueDate: LocalDate?,

    // Теги для добавления (по ID)
    val tagsToAddIds: Set<Long>?,

    // Новые теги для создания и добавления
    val newTagsToAdd: Set<@NotBlank NewTagDto>?,

    // Теги для удаления (по ID)
    val tagsToRemoveIds: Set<Long>?
)