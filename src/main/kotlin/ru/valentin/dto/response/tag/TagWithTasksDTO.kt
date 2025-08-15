package ru.valentin.dto.response.tag

import ru.valentin.dto.response.task.TaskNoTagsDTO

data class TagWithTasksDTO (
    val id: Long,
    val title: String,
    val tasks: List<TaskNoTagsDTO>
)