package ru.valentin.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class TaskWithTagsDTO(
    val id: Long,
    val title: String,
    val type: TaskTypeNoTasksDto,
    val description: String?,
    val dueDate: LocalDate,
    val tags: Set<TagNoTasksDTO>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)