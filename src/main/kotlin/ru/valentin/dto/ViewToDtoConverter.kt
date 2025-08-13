package ru.valentin.dto

import ru.valentin.dto.select.tag.TagNoTasksDTO
import ru.valentin.dto.select.tag.TagNoTasksView
import ru.valentin.dto.select.taskType.TaskTypeNoTasksDto
import ru.valentin.dto.select.task.TaskWithTagsDTO
import ru.valentin.dto.select.task.TaskNoTagsDTO
import ru.valentin.dto.select.task.TaskNoTagsView

object ViewToDtoConverter {

    fun toTagNoTasksDTO(view: TagNoTasksView): TagNoTasksDTO {
        return TagNoTasksDTO(
            id = view.getId(),
            title = view.getTitle()
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
            tags = setTags.map { toTagNoTasksDTO(it) }.toSet(),
            description = view.getDescription()
        )
    }
}