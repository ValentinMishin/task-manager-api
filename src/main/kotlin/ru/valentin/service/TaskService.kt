package ru.valentin.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.valentin.dto.TagDto
import ru.valentin.dto.TaskDto
import ru.valentin.dto.TaskTypeDto
import ru.valentin.dto.create.CreateTaskRequest
import ru.valentin.dto.create.NewTagDto
import ru.valentin.dto.update.UpdateTaskRequest
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
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
    fun createTask(request: CreateTaskRequest): TaskDto {
        // Валидация типа задачи
        val taskType = taskTypeRepository.findById(request.typeId)
            .orElseThrow { EntityNotFoundException("TaskType not found with id: ${request.typeId}") }

        // Обработка тегов
        val tags = processTags(request.existingTagIds, request.newTags)

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
    fun updateTask(taskId: Long, request: UpdateTaskRequest): TaskDto {
        val existingTask = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Task not found with id: $taskId") }

        val taskType = taskTypeRepository.findById(request.typeId)
            .orElseThrow { EntityNotFoundException("TaskType not found with id: ${request.typeId}") }

        // Обрабатываем теги для добавления
        val tagsToAdd = processTags(request.tagsToAddIds, request.newTagsToAdd)

        // Удаляем указанные теги
        val tagsToRemove = tagRepository.findAllById(request.tagsToRemoveIds)
        // удаляем связи, через JPA так как сущность все равно сохранятьа
        tagsToRemove.forEach { it.tasks.remove(existingTask) }

        existingTask.apply {
            title = request.title
            type = taskType
            description = request.description
            dueDate = request.dueDate
            // Удаляем указанные теги
            tags.removeAll(tagsToRemove)
            // Добавляем новые теги
            tags.addAll(tagsToAdd)
        }

        return taskRepository.save(existingTask).toDto()
    }

    fun deleteTask(taskId: Long) {
        val task = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Task not found with id: $taskId") }

        // Разрываем связи с тегами перед удалением
        taskRepository.deleteTagsFromTask(taskId)

        // Удаляем саму задачу
        taskRepository.delete(task)
    }

    private fun processTags(
        existingTagIds: Set<Long>,
        newTags: Set<NewTagDto>
    ): Set<Tag> {
        val tags = mutableSetOf<Tag>()

        // Добавляем существующие теги
        if (existingTagIds.isNotEmpty()) {
            tags.addAll(tagRepository.findAllById(existingTagIds))
        }

        // Создаем новые теги
        newTags.forEach { newTag ->
            tagRepository.findByTitle(newTag.title)?.let {
                tags.add(it)
            } ?: run {
                tags.add(tagRepository.save(Tag(title = newTag.title)))
            }
        }

        return tags
    }

    private fun Task.toDto() = TaskDto(
        id = id,
        title = title,
        type = type.toDto(),
        description = description,
        dueDate = dueDate,
        tags = tags.map { it.toDto() }.toSet(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun TaskType.toDto() = TaskTypeDto(
        id = id,
        code = code,
        priority = priority,
        description = description
    )

    private fun Tag.toDto() = TagDto(
        id = id,
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}