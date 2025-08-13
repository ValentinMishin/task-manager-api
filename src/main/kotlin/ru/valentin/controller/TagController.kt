package ru.valentin.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.valentin.dto.select.tag.TagWithTasksDTO
import ru.valentin.service.TagService

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {
    //4
    @GetMapping("/{id}/tasks")
    fun getTagWithSortedTasks(
        @PathVariable id: Long
    ): ResponseEntity<TagWithTasksDTO> {
            val result = tagService.findTagWithTasksSortedByPriority(id)
            return ResponseEntity.ok(result)
    }
}