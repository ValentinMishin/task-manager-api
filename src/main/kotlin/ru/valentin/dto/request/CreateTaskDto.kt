package ru.valentin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.FutureOrPresent
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Schema(description = "DTO для создания задачи. " +
        "Опционально возможно прикрепить: " +
        "существующие теги через набор айди, " +
        "новые теги, передав набор DTO создания тегов без связей")
data class CreateTaskDto(
    @Schema(description = "Обязательное не пустое название")
    @field:NotBlank(message = "Название задачи не может быть нул или пустым")
    val title: String,

    @Schema(description = "Обязательный тип задачи")
    @field:NotNull(message = "Тип задачи не может быть нул")
    val typeId: Long,

    @Schema(description = "Обязательное не пустое описание")
    val description: String,

    @Schema(description = "Обязательная дата, не может быть в прошлом")
    @field:NotNull
    @field:FutureOrPresent(message = "Запланированная дата не может быть нул или в прошлом")
    val dueDate: LocalDate,

    @Schema(description = "Опциональный набор айди тегов")
    // Прикрепление тегов по ID
    val existingTagIds: Set<@NotNull(message = "ID тегов не могут быть нул") Long>?,

    @Schema(description = "Опциональный набор dto тегов без связей")
    // Создание новых тегов и их прикрепление
    @field:Valid
    val newTags: Set<CreateShortTagDto>?
)
@Schema(description = "DTO для создания тегов без связей, опционально используется в создании задачи")
data class CreateShortTagDto(
    @Schema(description = "Обязательное не пустое название")
    @field:NotBlank(message = "Название тега не может быть нул или пустым")
    val title: String
)