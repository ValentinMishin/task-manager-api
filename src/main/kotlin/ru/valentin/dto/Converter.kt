package ru.valentin.dto

import ru.valentin.dto.response.tag.TagNoTasksDTO
import ru.valentin.dto.response.tag.TagNoTasksView
import ru.valentin.dto.response.taskType.TaskTypeNoTasksDto
import ru.valentin.dto.response.task.TaskWithTagsDTO
import ru.valentin.dto.response.task.TaskNoTagsDTO
import ru.valentin.dto.response.task.TaskNoTagsView
import ru.valentin.model.Tag
import ru.valentin.model.Task

object Converter {

    fun toTaskWithTagsDTO(from: Task): TaskWithTagsDTO {
        val res = TaskWithTagsDTO(
            id = from.id,
            title = from.title,
            type = from.type.toShortDto(),
            description = from.description,
            dueDate = from.dueDate,
            tags = from.tags.map { toTagNoTasksDTO(it) }.toMutableSet()
        )
        return res
    }

    fun toTagNoTasksDTO(view: TagNoTasksView): TagNoTasksDTO {
        return TagNoTasksDTO(
            id = view.getId(),
            title = view.getTitle()
        )
    }

    fun toTagNoTasksDTO(from: Tag): TagNoTasksDTO {
        return TagNoTasksDTO(
            id = from.id,
            title = from.title
        )
    }

    fun toTaskNoTagsDTO(view: TaskNoTagsView): TaskNoTagsDTO {
        return TaskNoTagsDTO(
            id = view.getId(),
            title = view.getTitle(),
            type = TaskTypeNoTasksDto(
                id = view.getTypeId(),
                code = view.getTypeCode(),
                priority = view.getTypePriority(),
                description = view.getTypeDescription()
            ),
            dueDate = view.getDueDate(),
            description = view.getDescription()
        )
    }

    fun toTaskWithTagsDTO(view: TaskNoTagsView, setTags: Set<TagNoTasksView>): TaskWithTagsDTO {
        return TaskWithTagsDTO(
            id = view.getId(),
            title = view.getTitle(),
            type = TaskTypeNoTasksDto(
                id = view.getTypeId(),
                code = view.getTypeCode(),
                priority = view.getTypePriority(),
                description = view.getTypeDescription()
            ),
            dueDate = view.getDueDate(),
            tags = setTags.map { toTagNoTasksDTO(it) }.toMutableSet(),
            description = view.getDescription()
        )
    }
}