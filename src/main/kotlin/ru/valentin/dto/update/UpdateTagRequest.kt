package ru.valentin.dto.update

import ru.valentin.dto.create.NewTaskDto
import javax.validation.Valid
import javax.validation.constraints.NotBlank

//хотя бы одно поле не должно быть нул
data class UpdateTagRequest (
    val title: String?,

    // Существующие задачи для добавления (по ID)
    val tasksToAddIds: Set<Long>?,

    // Новые задачи для создания и привязки
    val newTasksToAdd: Set<@Valid NewTaskDto>?,

    // ID задач для отвязки
    val tasksToRemoveIds: Set<Long>?
)

