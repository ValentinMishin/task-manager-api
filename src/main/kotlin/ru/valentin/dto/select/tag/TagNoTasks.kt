package ru.valentin.dto.select.tag

interface TagNoTasksView {
    fun getId(): Long
    fun getTitle(): String
}

data class TagNoTasksDTO(
    val id: Long,
    val title: String
)