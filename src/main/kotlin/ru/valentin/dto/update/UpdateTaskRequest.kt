package ru.valentin.dto.update

import ru.valentin.dto.create.NewTagDto
import java.time.LocalDate
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateTaskRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotNull(message = "Task type ID is required")
    val typeId: Long,

    val description: String? = null,

    @field:NotNull
    @field:FutureOrPresent(message = "Due date must be present or future")
    val dueDate: LocalDate,

    // Теги для добавления (по ID)
    val tagsToAddIds: Set<Long> = emptySet(),

    // Новые теги для создания и добавления
    val newTagsToAdd: Set<@NotBlank NewTagDto> = emptySet(),

    // Теги для удаления (по ID)
    val tagsToRemoveIds: Set<Long> = emptySet()
)