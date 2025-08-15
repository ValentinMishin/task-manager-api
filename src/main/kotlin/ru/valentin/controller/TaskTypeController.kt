package ru.valentin.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.valentin.dto.response.tag.TagWithTasksDTO
import ru.valentin.dto.response.taskType.TaskTypeNoTasksDto
import ru.valentin.service.TaskTypeService

@RestController
@RequestMapping("/api/task-types")
class TaskTypeController(
    private val taskTypeService: TaskTypeService
) {

    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTaskTypes(
    ): ResponseEntity<Set<TaskTypeNoTasksDto>> {
        val result = taskTypeService.findAllTypes()
        return ResponseEntity.ok(result)
    }
}
