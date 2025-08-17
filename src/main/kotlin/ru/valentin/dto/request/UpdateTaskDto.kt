package ru.valentin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

//хотя бы одно поле должно быть не нул
@Schema(description = "DTO для обновления задачи. Необходимо передать хотя бы одно поле для обновления " +
        "Опционально возможно прикрепить: " +
        "существующие теги через набор айди, " +
        "новые теги, передав набор DTO создания тега без связей. " +
        "Открепить теги, передав набор айди")
data class UpdateTaskDto(
    @Schema(description = "Опциональное не пустое название")
    @field:NotBlank(message = "Название задачи не должно быть пустым")
    val title: String?,

    @Schema(description = "Опциональный тип")
    val typeId: Long?,

    @Schema(description = "Опциональное не пустое описание")
    @field:NotBlank(message = "Описание задачи не должно быть пустым")
    val description: String?,

    @Schema(description = "Опциональная дата не в прошлом")
    @field:FutureOrPresent(message = "Запланированная дата не должна быть в прошлом")
    val dueDate: LocalDate?,

    @Schema(description = "Опциональный набор айди тегов")
    // Теги для добавления (по ID)
    val tagsToAddIds: Set<@NotNull(message = "ID тегов не могут быть нул") Long>?,

    @Schema(description = "Опциональный набор dto тегов без связей")
    // Новые теги для создания и добавления
    val newTagsToAdd: Set<@Valid CreateShortTagDto>?,

    @Schema(description = "Опциональный набор айди тегов")
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