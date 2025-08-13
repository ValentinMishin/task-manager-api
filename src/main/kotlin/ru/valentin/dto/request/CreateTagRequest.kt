package ru.valentin.dto.request

import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateTagDto(
    @field:NotBlank(message = "Title is required")
    val title: String,

    // Существующие задачи (по ID)
    val existingTaskIds: Set<Long>?,

    // Новые задачи
    val newTasks: Set<@Valid NewTaskDto>?
)

data class NewTaskDto(
    @field:NotBlank(message = "Task title is required")
    val title: String,

    @field:NotNull(message = "Task type ID is required")
    val typeId: Long,

    val description: String? = null,

    @field:NotNull
    @field:FutureOrPresent(message = "Due date must be present or future")
    val dueDate: LocalDate
)