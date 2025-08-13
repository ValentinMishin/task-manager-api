package ru.valentin.dto.select.tag

import ru.valentin.dto.select.task.TaskNoTagsDTO

data class TagWithTasksDTO (
    val id: Long,
    val title: String,
    val tasks: List<TaskNoTagsDTO>
)