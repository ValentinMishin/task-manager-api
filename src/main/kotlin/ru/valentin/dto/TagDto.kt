package ru.valentin.dto

import java.time.LocalDateTime

data class TagDto(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)