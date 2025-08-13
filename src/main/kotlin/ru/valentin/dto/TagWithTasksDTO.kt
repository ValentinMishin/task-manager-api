package ru.valentin.dto

data class TagWithTasksDTO (
    val id: Long,
    val title: String,
    val tasks: List<TaskNoTagsDTO>
)