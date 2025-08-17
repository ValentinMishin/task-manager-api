package ru.valentin.dto.response.tag

import io.swagger.v3.oas.annotations.media.Schema
import ru.valentin.dto.response.task.TaskNoTagsDTO

@Schema(description = "Тэг с отображением прикрепленных к нему задач")
data class TagWithTasksDTO (
    val id: Long,
    val title: String,
    val tasks: MutableSet<TaskNoTagsDTO>
)