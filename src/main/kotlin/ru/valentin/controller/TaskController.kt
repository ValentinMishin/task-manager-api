package ru.valentin.controller

import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.UpdateTaskDto
import ru.valentin.dto.response.task.TaskWithTagsDTO
import ru.valentin.service.TaskService
import java.time.LocalDate
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Pattern

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @PostMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTask(@RequestBody createTaskDto: CreateTaskDto):
            ResponseEntity<TaskWithTagsDTO> {
        val createdTask = taskService.createTask(createTaskDto)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdTask.id)
            .toUri()

        return ResponseEntity.created(location).body(createdTask)
    }

    @PutMapping(
        "/{taskId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateTask(taskId: Long,
                   @RequestBody updateTaskDto: UpdateTaskDto):
            ResponseEntity<TaskWithTagsDTO> {
        val updatedTask = taskService.updateTask(taskId, updateTaskDto)

        return ResponseEntity.ok().body(updatedTask)
    }

    @DeleteMapping(
        "/{taskId}"
    )
    fun deleteTask(taskId: Long): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/by-date",
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTasksByDateWithPrioritySort(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)date: LocalDate,
        page: Int,
        size: Int,
        @Pattern(regexp = "asc|desc",
            message = "asc или desc") sort: String
    ): ResponseEntity<Page<TaskWithTagsDTO>> {

        val result = taskService
            .getTasksByDateWithPrioritySort(date, page, size, sort)

        return ResponseEntity.ok()
            .header("X-Total-Elements", result.totalElements.toString())
            .header("X-Total-Pages", result.totalPages.toString())
            .body(result)
    }
}