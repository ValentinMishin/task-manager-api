package ru.valentin.dto.request

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

//хотя бы одно поле не должно быть нул
data class UpdateTagDto (
    @field:NotBlank(message = "Название тега не должно быть пустым")
    val title: String?,

    // Существующие задачи для добавления (по ID)
    val tasksToAddIds: Set<@NotNull(message = "ID задач не могут быть нул") Long>?,

    // Новые задачи для создания и привязки
    val newTasksToAdd: Set<@Valid NewTaskDto>?,

    // ID задач для отвязки
    val tasksToRemoveIds: Set<@NotNull(message = "ID задач не могут быть нул")Long>?
)