package ru.valentin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Schema(description = "DTO для обновления тега. Необходимо передать хотя бы одно поле для обновления " +
        "Опционально возможно прикрепить: " +
        "существующие задачи через набор айди, " +
        "новые задачи, передав набор DTO создания задачи без связей. " +
        "Открепить задачи, передав набор айди")
//хотя бы одно поле не должно быть нул
data class UpdateTagDto (
    @Schema(description = "Опциональное не пустое название")
    @field:NotBlank(message = "Название тега не должно быть пустым")
    val title: String?,

    @Schema(description = "Опциональный набор айди задач")
    // Существующие задачи для добавления (по ID)
    val tasksToAddIds: Set<@NotNull(message = "ID задач не могут быть нул") Long>?,

    @Schema(description = "Опциональный набор dto задач без связей")
    // Новые задачи для создания и привязки
    val newTasksToAdd: Set<@Valid CreateShortTaskDto>?,

    @Schema(description = "Опциональный набор айди задач")
    // ID задач для отвязки
    val tasksToRemoveIds: Set<@NotNull(message = "ID задач не могут быть нул")Long>?
)