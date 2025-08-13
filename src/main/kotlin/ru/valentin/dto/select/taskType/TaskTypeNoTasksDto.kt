package ru.valentin.dto.select.taskType

data class TaskTypeNoTasksDto(
    val id: Long,
    val code: String,
    val priority: Int,
    val description: String?
)