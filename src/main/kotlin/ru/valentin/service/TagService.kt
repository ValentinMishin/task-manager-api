package ru.valentin.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.valentin.dto.response.tag.TagNoTasksDTO
import ru.valentin.dto.response.tag.TagWithTasksDTO
import ru.valentin.dto.Converter
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
    fun createTag(request: CreateTagDto): TagWithTasksDTO {
        // Сохраняем тег и получаем его из БД с айди
        val tag = Tag(title = request.title)
            .let(tagRepository::save)

        // Обрабатываем задачи и добавляем к тегу сохраняем в бд, синхронизируем
        // тег из БД с сущностью для дто
        processTasksAndAddToTag(tag,
            request.existingTaskIds,
            request.newTasks)

        return tag.toDto()
    }

//    7. Изменение существующего тега
    @Transactional
    fun updateTag(tagId: Long, request: UpdateTagDto): TagWithTasksDTO {
        val updatingTag = tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException("Тег с ID $tagId не найден") }

        // Обновляем основную информацию
        request.title?.let { updatingTag.title = it }

        // Обрабатываем задачи для удаления
        request.tasksToRemoveIds?.let {
            val tasksToRemove = taskRepository.findAllById(request.tasksToRemoveIds)
            tasksToRemove.forEach { task ->
                task.tags.remove(updatingTag)
                taskRepository.save(task)
                updatingTag.tasks.remove(task)
            }
        }

        processTasksAndAddToTag(updatingTag,
            request.tasksToAddIds, request.newTasksToAdd)

        return updatingTag.toDto()
    }

//    8. Удаление тега по айди вместе с задачами
    @Transactional
    fun deleteTagWithTasks(tagId: Long) {
        val deletingTag = tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException("Тег с ID $tagId не найден") }

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
            .orElseThrow{ EntityNotFoundException("Тег с ID $tagId не найден") }

        if (!tagRepository.hasTask(tagWithTasks.id)) {
            throw EntityNotFoundException("У тега c ID $tagId нет связанных задач")
        }

        val tasks = taskRepository.findAllByTagIdSortedByPriority(tagId)
        return TagWithTasksDTO(
            id = tagWithTasks.id,
            title = tagWithTasks.title,
            tasks = tasks.map {
                Converter.toTaskNoTagsDTO(it)
            }.toMutableSet()
        )
    }

    //5. Получение всех тегов, у которых есть задачи
    fun findTagsHavingTasks(): Set<TagNoTasksDTO> {
        val tags = tagRepository.findTagsWithTasks()
        if (tags.isEmpty())
            throw EntityNotFoundException("Теги с задачами не найдены")
        else {
            return tags.map { Converter.toTagNoTasksDTO(it) }.toSet()
        }
    }

    private fun processTasksAndAddToTag(
        tag: Tag,
        existingTaskIds: Set<Long>?,
        newTasks: Set<NewTaskDto>?
    ) {
        //оба пустые или нул?
        if ( !(existingTaskIds != null && existingTaskIds.isNotEmpty())
            && !(newTasks != null && newTasks.isNotEmpty())) {
            return
        }

        existingTaskIds?.let {
            if (existingTaskIds.isNotEmpty()) {
                val tasks = taskRepository.findAllById(existingTaskIds)
                tasks.forEach { task ->
                    task.tags.add(tag) // добавил тег
                    taskRepository.save(task) // обновил таблицу связку
                    tag.tasks.add(task) //для дто
                }
            }
        }

        // Создаем новые задачи
        newTasks?.let {
            newTasks.forEach { newTask ->
                val taskType = taskTypeRepository.findById(newTask.typeId)
                    .orElseThrow { EntityNotFoundException("Тип задачи с ID: ${newTask.typeId}") }

                val task = Task(
                    title = newTask.title,
                    type = taskType,
                    description = newTask.description,
                    dueDate = newTask.dueDate
                ).apply {
                    tags.add(tag)
                }

                tag.tasks.add(taskRepository.save(task)) //для дто
            }
        }
    }
}