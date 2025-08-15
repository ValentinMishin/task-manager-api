package ru.valentin.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.valentin.dto.response.tag.TagNoTasksDTO
import ru.valentin.dto.response.tag.TagWithTasksDTO
import ru.valentin.dto.ViewToDtoConverter
import ru.valentin.dto.request.CreateTagDto
import ru.valentin.dto.request.NewTaskDto
import ru.valentin.dto.request.UpdateTagDto
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import javax.persistence.EntityNotFoundException

@Service
open class TagService (
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository,
    private val taskTypeRepository: TaskTypeRepository) {

//    6. Создание нового тега
    @Transactional
    fun createTag(request: CreateTagDto): TagNoTasksDTO {
        // Создаем тег
        val tag = Tag(title = request.title)
            .let(tagRepository::save)

        // Обрабатываем задачи
        processTasks(tag, request.existingTaskIds, request.newTasks)

        return tag.toShortDto()
    }

//    7. Изменение существующего тега
    @Transactional
    fun updateTag(tagId: Long, request: UpdateTagDto): TagNoTasksDTO {
        val updatingTag = tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException("Tag not found with id: $tagId") }

        // Обновляем основную информацию
        request.title?.let { updatingTag.title = it }

        // Обрабатываем задачи для удаления
        request.tasksToRemoveIds?.let {
            val tasksToRemove = taskRepository.findAllById(request.tasksToRemoveIds)
            tasksToRemove.forEach { task ->
                task.tags.remove(updatingTag)
                updatingTag.tasks.remove(task)
            }
            taskRepository.saveAll(tasksToRemove)
        }

        // Обрабатываем новые задачи
        request.newTasksToAdd?.let {
            val newTasks = request.newTasksToAdd.map { taskDto ->
                val taskType = taskTypeRepository.findById(taskDto.typeId)
                    .orElseThrow { EntityNotFoundException("TaskType not found with id: ${taskDto.typeId}") }

                Task(
                    title = taskDto.title,
                    type = taskType,
                    description = taskDto.description,
                    dueDate = taskDto.dueDate
                ).apply {
                    tags.add(updatingTag)
                }
            }.let(taskRepository::saveAll)
        }

        // Обрабатываем существующие задачи для добавления
        request.tasksToAddIds?.let {
            val tasksToAdd = taskRepository.findAllById(request.tasksToAddIds)
            tasksToAdd.forEach { task ->
                if (!updatingTag.tasks.contains(task)) {
                    task.tags.add(updatingTag)
                    updatingTag.tasks.add(task)
                }
            }
            taskRepository.saveAll(tasksToAdd)
        }

        return tagRepository.save(updatingTag).toShortDto()
    }

//    8. Удаление тега по айди вместе с задачами
    @Transactional
    fun deleteTagWithTasks(tagId: Long) {
        val deletingTag = tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException("Tag not found with id: $tagId") }

        //связанные задачи
        val tasksIds = tagRepository.findTaskIdsByTagId(tagId)
//        удаление связей
        tagRepository.deleteTagRelationships(tagId)
//         удаление задач
        taskRepository.deleteAllById(tasksIds)
//         удаление тега
        tagRepository.deleteById(tagId)
    }

    // 4. Получение тега по идентификатору с задачами, сортированными по приоритету
    @Transactional(readOnly = true)
    fun findTagWithTasksSortedByPriority(tagId: Long): TagWithTasksDTO {
        val tagWithTasks = tagRepository.findById(tagId)
            .orElseThrow{ EntityNotFoundException("Тег с ID $tagId не найден в базе данных") }

        if (!tagRepository.hasTask(tagWithTasks.id)) {
            throw EntityNotFoundException("У тега c ID $tagId нет связанных задач")
        }

        val tasks = taskRepository.findAllByTagIdSortedByPriority(tagId)
        return TagWithTasksDTO(
            id = tagWithTasks.id,
            title = tagWithTasks.title,
            tasks = tasks.map {
                ViewToDtoConverter.toTaskNoTagsDTO(it)
            }
        )
    }

    //5. Получение всех тегов, у которых есть задачи
    fun findTagsHavingTasks(): Set<TagNoTasksDTO> {
        val tags = tagRepository.findTagsWithTasks()
        if (tags.isEmpty())
            throw Exception("Tags with tasks not found")
        else {
            return tags.map { ViewToDtoConverter.toTagNoTasksDTO(it) }.toSet()
        }
    }

    private fun processTasks(
        tag: Tag,
        existingTaskIds: Set<Long>?,
        newTasks: Set<NewTaskDto>?
    ) {
        // Обрабатываем существующие задачи
        //присоединение связей с тегом
        existingTaskIds?.let {
            if (existingTaskIds.isNotEmpty()) {
                val tasks = taskRepository.findAllById(existingTaskIds)
                tasks.forEach { task ->
                    task.tags.add(tag)
                    tag.tasks.add(task)
                }
                taskRepository.saveAll(tasks)
            }
        }

        // Создаем новые задачи
        newTasks?.let {
            newTasks.forEach { newTask ->
                val taskType = taskTypeRepository.findById(newTask.typeId)
                    .orElseThrow { EntityNotFoundException("TaskType not found with id: ${newTask.typeId}") }

                val task = Task(
                    title = newTask.title,
                    type = taskType,
                    description = newTask.description,
                    dueDate = newTask.dueDate
                ).apply {
                    tags.add(tag)
                }

                taskRepository.save(task)
                tag.tasks.add(task)
            }
        }
    }
}