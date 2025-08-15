package ru.valentin.dto.request

import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

//хотя бы одно поле должно быть не нул
data class UpdateTaskDto(
    @field:NotBlank(message = "Название задачи не должно быть пустым")
    val title: String?,

    val typeId: Long?,

    @field:NotBlank(message = "Описание задачи не должно быть пустым")
    val description: String?,

    @field:FutureOrPresent(message = "Запланированная дата не должна быть в прошлом")
    val dueDate: LocalDate?,

    // Теги для добавления (по ID)
    val tagsToAddIds: Set<@NotNull(message = "ID тегов не могут быть нул") Long>?,

    // Новые теги для создания и добавления
    val newTagsToAdd: Set<@Valid NewTagDto>?,

    // Теги для удаления (по ID)
    val tagsToRemoveIds: Set<@NotNull(message = "ID тегов не могут быть нул") Long>?
) {
    init {
        require(listOf(title, typeId, description, dueDate,
            tagsToAddIds, newTagsToAdd, tagsToRemoveIds).any { it != null }) {
            "Необходимо указать хотя бы одно поле для обновления"
        }
    }
}