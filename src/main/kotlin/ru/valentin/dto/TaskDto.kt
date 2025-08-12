package ru.valentin.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class TaskDto(
    val id: Long,
    val title: String,
    val type: TaskTypeDto,
    val description: String?,
    val dueDate: LocalDate,
    val tags: Set<TagDto>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)