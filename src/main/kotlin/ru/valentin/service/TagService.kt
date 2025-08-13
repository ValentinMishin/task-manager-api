package ru.valentin.service

import org.springframework.transaction.annotation.Transactional
import ru.valentin.dto.TagNoTasksDTO
import ru.valentin.dto.TagWithTasksDTO
import ru.valentin.dto.request.CreateTagDto
import ru.valentin.dto.request.NewTaskDto
import ru.valentin.dto.response.DeleteTagResponse
import ru.valentin.dto.request.UpdateTagRequest
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import javax.persistence.EntityNotFoundException

open class TagService (
    private val taskRepository: TaskRepository,
    private val tagRepository: TagRepository,
    private val taskTypeRepository: TaskTypeRepository) {

    @Transactional
    fun createTag(request: CreateTagDto): TagNoTasksDTO {
        // Создаем тег
        val tag = Tag(title = request.title)
            .let(tagRepository::save)

        // Обрабатываем задачи
        processTasks(tag, request.existingTaskIds, request.newTasks)

        return tag.toShortDto()
    }

    @Transactional
    fun updateTag(tagId: Long, request: UpdateTagRequest): TagNoTasksDTO {
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

    @Transactional
    fun deleteTagWithTasks(tagId: Long): DeleteTagResponse {
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

        return DeleteTagResponse(tagId, tasksIds.toSet())
    }

    fun findTagWithTasksSortedByPriority(tagId: Long): TagWithTasksDTO {
        val tagWithTasks = tagRepository.findById(tagId)
            .orElseThrow{ EntityNotFoundException("Tag not found with id: $tagId") }

        if (tagWithTasks.hasNoTasks()) {
            throw EntityNotFoundException("Tag with $tagId has no tasks")
        }

//        val tasks = tagRepository.findByIdWithTasksSortedByPriority(tagId)
        val tasks = taskRepository.findByIdWithTasksSortedByPriority(tagId)

        return TagWithTasksDTO(
            id = tagWithTasks.id,
            title = tagWithTasks.title,
            tasks.map { it.toShortDto() }
        )
    }

    fun findTagsHavingTasks(): Set<TagNoTasksDTO> {
        val tags = tagRepository.findTagsWithTasks()
        if (tags.isEmpty())
            throw Exception("Tags with tasks not found")
        else {
            val resTags = mutableSetOf<TagNoTasksDTO>()
            tags.forEach {
                resTags.add(it.toShortDto())
            }
            return resTags.toSet()
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