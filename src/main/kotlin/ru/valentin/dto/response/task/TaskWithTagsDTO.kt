package ru.valentin.dto.response.task

import io.swagger.v3.oas.annotations.media.Schema
import ru.valentin.dto.response.tag.TagNoTasksDTO
import ru.valentin.dto.response.taskType.TaskTypeNoTasksDto
import java.time.LocalDate

@Schema(description = "Задача с отображением прикрепленных к ней тегов, и типом")
data class TaskWithTagsDTO(
    val id: Long,
    val title: String,
    val type: TaskTypeNoTasksDto,
    val description: String,
    val dueDate: LocalDate,
    val tags: MutableSet<TagNoTasksDTO>
)