package ru.valentin.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.valentin.dto.response.task.TaskWithTagsDTO
import ru.valentin.dto.Converter
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.NewTagDto
import ru.valentin.dto.request.UpdateTaskDto
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
    fun createTask(request: CreateTaskDto): TaskWithTagsDTO {
        // Валидация типа задачи
        val taskType = taskTypeRepository.findById(request.typeId)
            .orElseThrow { EntityNotFoundException("Тип задачи с ID: ${request.typeId}") }

        // Обработка тегов
        val tags = processTagsToAdd(request.existingTagIds, request.newTags)

        // Создание задачи
        val task = Task(
            title = request.title,
            type = taskType,
            description = request.description,
            dueDate = request.dueDate,
            tags = tags
        ).let(taskRepository::save)

        return task.toDto()
    }

//    2 Изменение задачи
    @Transactional
    fun updateTask(taskId: Long, request: UpdateTaskDto): TaskWithTagsDTO {
        val existingTask = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Задача с ID $taskId не найдена") }

        var taskType: TaskType? = null
        request.typeId?.let {
            taskType = taskTypeRepository.findById(request.typeId)
                .orElseThrow { EntityNotFoundException("Тип задачи с ID: ${request.typeId}") }
        }

        // Обрабатываем теги для добавления
        val tagsToAdd = processTagsToAdd(request.tagsToAddIds, request.newTagsToAdd)

        // Удаляем теги из коллекции в задаче
        if (request.tagsToRemoveIds != null
            && request.tagsToRemoveIds.isNotEmpty()) {
            existingTask.tags.removeAll(
                tagRepository.findAllById(request.tagsToRemoveIds))
        }

        existingTask.apply {
            request.title?.let { title = it }
            taskType?.let { type = it }
            request.description?.let { description = it }
            request.dueDate?.let { dueDate = it}
            // Добавляем новые теги
            tagsToAdd.takeIf { it.isNotEmpty() }
                ?.also { tags.addAll(it) }
        }

        return taskRepository.save(existingTask).toDto()
    }

//    3. Удаление задачи
    @Transactional
    fun deleteTask(taskId: Long) {
        val task = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Задача с ID $taskId не найдена") }

        // Разрываем связи с тегами перед удалением
        taskRepository.deleteTagsFromTask(taskId)

        // Удаляем саму задачу
        taskRepository.delete(task)
    }

    //10. Получение списка задач за заданную дату с сортировкой по уровню приоритета
    @Transactional
    fun getTasksByDateWithPrioritySort(
        date: LocalDate,
        page: Int,
        size: Int,
        sortDirection : String
    ): Page<TaskWithTagsDTO> {
        val pageable: Pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.fromString(sortDirection),
                "type.priority")
        )

//        val taskViews = taskRepository
//            .findAllByDateOrderByTypePriority(date, pageable)
//        return taskViews.map { Converter.toTaskWithTagsDTO(
//            it, tagRepository.findTagsByTask(it.getId())
//        ) }
        val tasks = taskRepository
            .findAllByDueDateWithTasks(date,pageable)
        return tasks.map { Converter.toTaskWithTagsDTO(it) }
    }

    private fun processTagsToAdd(
        existingTagIds: Set<Long>?,
        newTags: Set<NewTagDto>?
    ): MutableSet<Tag> {

        val tags = mutableSetOf<Tag>()

        //оба пустые или нул?
        if ( !(existingTagIds != null && existingTagIds.isNotEmpty())
            && !(newTags != null && newTags.isNotEmpty())) {
             return tags
        }
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
                    tags.add(it) //если уже существуют добавляем
                } ?: run {
                    tags.add(tagRepository.save(Tag(title = newTag.title))) //создаем новый и добавляем
                }
            }
        }
        return tags
    }
}