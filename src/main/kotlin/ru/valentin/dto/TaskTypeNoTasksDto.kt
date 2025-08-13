package ru.valentin.dto

data class TaskTypeNoTasksDto(
    val id: Long,
    val code: String,
    val priority: Int,
    val description: String?
)