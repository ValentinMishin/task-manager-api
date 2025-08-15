package ru.valentin.dto.request

import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateTagDto(
    @field:NotBlank(message = "Название тега не может быть нул или пустым")
    val title: String,

    // Существующие задачи (по ID)
    val existingTaskIds: Set<@NotNull(message = "ID задач не могут быть нул") Long>?,

    // Новые задачи
    val newTasks: Set<@Valid NewTaskDto>?
)

data class NewTaskDto(
    @field:NotBlank(message = "Название задачи не может быть нул или пустым")
    val title: String,

    @field:NotNull(message = "Тип задачи не может быть нул")
    val typeId: Long,

    @field:NotBlank(message = "Описание задачи не может быть нул или пустым")
    val description: String,

    @field:NotNull
    @field:FutureOrPresent(message = "Запланированная дата не может быть нул или в прошлом")
    val dueDate: LocalDate
)