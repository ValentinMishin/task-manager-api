package ru.valentin.dto

data class TaskTypeDto(
    val id: Long,
    val code: String,
    val priority: Int,
    val description: String?
)