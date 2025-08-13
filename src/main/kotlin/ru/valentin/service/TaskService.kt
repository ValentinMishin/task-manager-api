package ru.valentin.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.valentin.dto.TaskNoTagsDTO
import ru.valentin.dto.TaskWithTagsDTO
import ru.valentin.dto.request.CreateTaskRequest
import ru.valentin.dto.request.NewTagDto
import ru.valentin.dto.response.DeleteTaskResponse
import ru.valentin.dto.request.UpdateTaskRequest
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository,
    private val taskTypeRepository: TaskTypeRepository
) {
    // 1. Создание новой задачи
    // в рамках одной транзакции
    @Transactional
    fun createTask(request: CreateTaskRequest): TaskWithTagsDTO {
        // Валидация типа задачи
        val taskType = taskTypeRepository.findById(request.typeId)
            .orElseThrow { EntityNotFoundException("TaskType not found with id: ${request.typeId}") }

        // Обработка тегов
        var tags = processTags(request.existingTagIds, request.newTags)
        if (tags == null) tags = emptySet()

        // Создание задачи
        val task = Task(
            title = request.title,
            type = taskType,
            description = request.description,
            dueDate = request.dueDate,
            tags = tags.toMutableSet()
        ).let(taskRepository::save)

        return task.toDto()
    }

    @Transactional
    fun updateTask(taskId: Long, request: UpdateTaskRequest): TaskWithTagsDTO {
        val existingTask = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Task not found with id: $taskId") }

        var taskType: TaskType? = null
        request.typeId?.let {
            taskType = taskTypeRepository.findById(request.typeId)
                .orElseThrow { EntityNotFoundException("TaskType not found with id: ${request.typeId}") }
        }

        // Обрабатываем теги для добавления
        val tagsToAdd = processTags(request.tagsToAddIds, request.newTagsToAdd)

        // Удаляем указанные теги
        var tagsToRemove: List<Tag>? = null
        if (request.tagsToRemoveIds != null) {
            tagsToRemove = tagRepository.findAllById(request.tagsToRemoveIds)
            // удаляем связи, через JPA так как сущность все равно сохранятьа
            tagsToRemove.forEach { it.tasks.remove(existingTask) }
        }

        existingTask.apply {
            request.title?.let { title = it }
            taskType?.let { type = it }
            request.description?.let { description = it }
            request.dueDate?.let { dueDate = it}
            // Удаляем указанные теги
            tagsToRemove?.let { tags.removeAll(it) }
            // Добавляем новые теги
            tagsToAdd?.let { tags.addAll(it) }
        }

        return taskRepository.save(existingTask).toDto()
    }

    fun deleteTask(taskId: Long): DeleteTaskResponse {
        val task = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Task not found with id: $taskId") }

        // Разрываем связи с тегами перед удалением
        taskRepository.deleteTagsFromTask(taskId)

        // Удаляем саму задачу
        taskRepository.delete(task)

        return DeleteTaskResponse(taskId)
    }

    fun getTasksByDateWithPrioritySort(
        date: LocalDate,
        page: Int,
        size: Int
    ): Page<TaskWithTagsDTO> {
        val pageable = PageRequest.of(page, size)

        return taskRepository.findAllByDateOrderByTypePriority(date, pageable)
            .map { it.toDto() }
    }

    private fun processTags(
        existingTagIds: Set<Long>?,
        newTags: Set<NewTagDto>?
    ): Set<Tag>? {
        if (existingTagIds == null && newTags == null)
            return null

        val tags = mutableSetOf<Tag>()

        // Добавляем существующие теги
        existingTagIds?.let {
            if (existingTagIds.isNotEmpty()) {
                tags.addAll(tagRepository.findAllById(existingTagIds))
            }
        }

        // Создаем новые теги
        newTags?.let {
            newTags.forEach { newTag ->
                tagRepository.findByTitle(newTag.title)?.let {
                    tags.add(it)
                } ?: run {
                    tags.add(tagRepository.save(Tag(title = newTag.title)))
                }
            }
        }
        return tags
    }
}