package ru.valentin.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import ru.valentin.dto.response.taskType.TaskTypeNoTasksDto
import ru.valentin.repository.TaskTypeRepository

@Service
class TaskTypeService(
    private val taskTypeRepository: TaskTypeRepository
) {
//    9. Получение списка типов задач с уровнем приоритета
    @Cacheable("taskTypes")
    fun findAllTypes(): Set<TaskTypeNoTasksDto> {
        val taskTypes = taskTypeRepository.findAllByOrderByPriorityDesc()
        if (taskTypes.isEmpty())
            throw Exception("No Task Types found")

        val res = mutableSetOf<TaskTypeNoTasksDto>()
        taskTypes.forEach {
            res.add(it.toShortDto())
        }
        return res.toSet()
    }

    @CacheEvict(value = ["taskTypes"], allEntries = true) // Очистка кэша
    fun clearTaskTypesCache() {
        // Понадобится в случае ручного добавления
    }
}