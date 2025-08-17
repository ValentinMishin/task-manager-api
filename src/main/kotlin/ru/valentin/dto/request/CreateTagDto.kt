package ru.valentin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Schema(description = "DTO для создания тега. " +
        "Опционально возможно прикрепить: " +
        "существующие задачи через набор айди, " +
        "новые задачи, передав набор DTO создания задачи без связей")
data class CreateTagDto(
    @Schema(description = "Обязательное не пустое название")
    @field:NotBlank(message = "Название тега не может быть нул или пустым")
    val title: String,

    @Schema(description = "Опциональный набор айди задач")
    // Существующие задачи (по ID)
    val existingTaskIds: Set<@NotNull(message = "ID задач не могут быть нул") Long>?,

    @Schema(description = "Опциональный набор dto задач без связей")
    // Новые задачи
    val newTasks: Set<@Valid CreateShortTaskDto>?
)

@Schema(description = "DTO для создания задачи без связей, опционально используется в создании тега")
data class CreateShortTaskDto(
    @Schema(description = "Обязательное не пустое название")
    @field:NotBlank(message = "Название задачи не может быть нул или пустым")
    val title: String,

    @Schema(description = "Обязательный тип задачи")
    @field:NotNull(message = "Тип задачи не может быть нул")
    val typeId: Long,

    @Schema(description = "Обязательное не пустое описание")
    @field:NotBlank(message = "Описание задачи не может быть нул или пустым")
    val description: String,

    @Schema(description = "Обязательная дата, не может быть в прошлом")
    @field:NotNull
    @field:FutureOrPresent(message = "Запланированная дата не может быть нул или в прошлом")
    val dueDate: LocalDate
)