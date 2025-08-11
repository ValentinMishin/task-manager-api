//package ru.valentin.service
//
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import ru.valentin.model.Tag
//import ru.valentin.model.Task
//import ru.valentin.model.TaskType
//import ru.valentin.repository.TagRepository
//import ru.valentin.repository.TaskRepository
//import ru.valentin.repository.TaskTypeRepository
//import java.time.LocalDate
//
//@Service
//@Transactional
//class TaskService(
//    private val taskRepository: TaskRepository,
//    private val tagRepository: TagRepository,
//    private val taskTypeRepository: TaskTypeRepository
//) {
//    // 1. Создание новой задачи
//    fun createTask(
//        title: String,
//        priority: String,
//        description: String?,
//        dueDate: LocalDate,
//        tagIds: Set<Long> = emptySet()
//    ): Task {
//        val taskType = taskTypeRepository.findByCode(typeCode)
//            ?: throw IllegalArgumentException("Task type with code $typeCode not found")
//
//        val task = Task(
//            title = title,
//            type = taskType,
//            description = description,
//            dueDate = dueDate
//        )
//
//        tagIds.forEach { tagId ->
//            val tag = tagRepository.findById(tagId)
//                .orElseThrow { IllegalArgumentException("Tag with id $tagId not found") }
//            task.addTag(tag)
//        }
//
//        return taskRepository.save(task)
//    }
//}