package ru.valentin.dto.response.taskType

data class TaskTypeNoTasksDto(
    val id: Long,
    val code: String,
    val priority: Int,
    val description: String?
)