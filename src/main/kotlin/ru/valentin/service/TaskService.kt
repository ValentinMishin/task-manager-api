//package ru.valentin.service
//
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import ru.valentin.dto.TagDto
//import ru.valentin.dto.TaskDto
//import ru.valentin.dto.TaskTypeDto
//import ru.valentin.dto.create.CreateTaskRequest
//import ru.valentin.dto.create.NewTagDto
//import ru.valentin.model.Tag
//import ru.valentin.model.Task
//import ru.valentin.model.TaskType
//import ru.valentin.repository.TagRepository
//import ru.valentin.repository.TaskRepository
//import ru.valentin.repository.TaskTypeRepository
//import javax.persistence.EntityNotFoundException
//
//@Service
//@Transactional
//class TaskService(
//    private val taskRepository: TaskRepository,
//    private val tagRepository: TagRepository,
//    private val taskTypeRepository: TaskTypeRepository
//) {
//    // 1. Создание новой задачи
//    // в рамках одной транзакции
//    @Transactional
//    fun createTask(request: CreateTaskRequest): TaskDto {
//        // Валидация типа задачи
//        val taskType = taskTypeRepository.findById(request.typeId)
//            .orElseThrow { EntityNotFoundException("TaskType not found with id: ${request.typeId}") }
//
//        // Обработка тегов
//        val tags = processTags(request.existingTagIds, request.newTags)
//
//        // Создание задачи
//        val task = Task(
//            title = request.title,
//            type = taskType,
//            description = request.description,
//            dueDate = request.dueDate,
//            tags = tags.toMutableSet()
//        ).let(taskRepository::save)
//
//        return task.toDto()
//    }
//
//    private fun processTags(
//        existingTagIds: Set<Long>,
//        newTags: Set<NewTagDto>
//    ): Set<Tag> {
//        val tags = mutableSetOf<Tag>()
//
//        // Добавляем существующие теги
//        if (existingTagIds.isNotEmpty()) {
//            tags.addAll(tagRepository.findAllById(existingTagIds))
//        }
//
//        // Создаем новые теги
//        newTags.forEach { newTag ->
//            tagRepository.findByTitle(newTag.title)?.let {
//                tags.add(it)
//            } ?: run {
//                tags.add(tagRepository.save(Tag(title = newTag.title)))
//            }
//        }
//
//        return tags
//    }
//
//    private fun Task.toDto() = TaskDto(
//        id = id,
//        title = title,
//        type = type.toDto(),
//        description = description,
//        dueDate = dueDate,
//        tags = tags.map { it.toDto() }.toSet(),
//        createdAt = createdAt,
//        updatedAt = updatedAt
//    )
//
//    private fun TaskType.toDto() = TaskTypeDto(
//        id = id,
//        code = code,
//        priority = priority,
//        description = description
//    )
//
//    private fun Tag.toDto() = TagDto(
//        id = id,
//        title = title,
//        createdAt = createdAt,
//        updatedAt = updatedAt
//    )
//
//}