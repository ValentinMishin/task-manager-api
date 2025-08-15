package ru.valentin.dto.response.task

import ru.valentin.dto.response.tag.TagNoTasksDTO
import ru.valentin.dto.response.taskType.TaskTypeNoTasksDto
import java.time.LocalDate

data class TaskWithTagsDTO(
    val id: Long,
    val title: String,
    val type: TaskTypeNoTasksDto,
    val description: String,
    val dueDate: LocalDate,
    val tags: Set<TagNoTasksDTO>
)