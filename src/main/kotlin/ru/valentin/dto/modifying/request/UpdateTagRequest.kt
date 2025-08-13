package ru.valentin.dto.modifying.request

import javax.validation.Valid

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