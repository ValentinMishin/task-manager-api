package ru.valentin.dto.response.taskType

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Подробное описание типа задач")
data class TaskTypeNoTasksDto(
    val id: Long,
    val code: String,
    val priority: Int,
    val description: String?
)