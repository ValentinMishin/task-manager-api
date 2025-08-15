package ru.valentin.dto.response.task

import ru.valentin.dto.response.taskType.TaskTypeNoTasksDto
import java.time.LocalDate

interface TaskNoTagsView {
    fun getId(): Long
    fun getTitle(): String
    fun getDueDate(): LocalDate
    fun getDescription(): String
    fun getTypeId(): Long
    fun getTypeCode(): String
    fun getTypePriority(): Int
    fun getTypeDescription(): String?
}

data class TaskNoTagsDTO(
    val id: Long,
    val title: String,
    val type: TaskTypeNoTasksDto,
    val description: String,
    val dueDate: LocalDate,
)