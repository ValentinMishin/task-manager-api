package ru.valentin.dto.response.tag

import io.swagger.v3.oas.annotations.media.Schema

interface TagNoTasksView {
    fun getId(): Long
    fun getTitle(): String
}

@Schema(description = "Тег без отображения прикрепленных к нему задач")
data class TagNoTasksDTO(
    val id: Long,
    val title: String
)