package ru.valentin.dto.response.task

import io.swagger.v3.oas.annotations.media.Schema
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

@Schema(description = "Задача без отображения прикрепленных к ней тегов")
data class TaskNoTagsDTO(
    val id: Long,
    val title: String,
    val type: TaskTypeNoTasksDto,
    val description: String,
    val dueDate: LocalDate,
)