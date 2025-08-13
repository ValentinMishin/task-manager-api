package ru.valentin.dto

import ru.valentin.model.TaskType
import java.time.LocalDate

data class TaskNoTagsDTO(
    val id: Long,
    val title: String,
    val type: TaskTypeNoTasksDto,
    val dueDate: LocalDate,
)