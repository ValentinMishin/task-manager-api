package ru.valentin.controller

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import ru.valentin.dto.request.CreateTagDto
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.UpdateTagDto
import ru.valentin.dto.request.UpdateTaskDto
import ru.valentin.dto.response.tag.TagNoTasksDTO
import ru.valentin.dto.response.tag.TagWithTasksDTO
import ru.valentin.dto.response.task.TaskWithTagsDTO
import ru.valentin.service.TagService
import javax.validation.Valid

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {
    @PostMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createTag(@Valid @RequestBody createTagDto: CreateTagDto):
            ResponseEntity<TagWithTasksDTO> {
        val createdTag = tagService.createTag(createTagDto)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdTag.id)
            .toUri()

        return ResponseEntity.created(location).body(createdTag)
    }

    @PutMapping(
        "/{tagId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateTag(tagId: Long,
                   @Valid @RequestBody updateTagDto: UpdateTagDto):
            ResponseEntity<TagWithTasksDTO> {
        val updatedTag = tagService.updateTag(tagId, updateTagDto)

        return ResponseEntity.ok().body(updatedTag)
    }

    @DeleteMapping(
        "/{tagId}"
    )
    fun deleteTag(tagId: Long): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }

    //4 /api/tags/{id}/tasks
    @GetMapping("/{id}/tasks",
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTagWithSortedTasks(
        @PathVariable id: Long
    ): ResponseEntity<TagWithTasksDTO> {
            val result = tagService.findTagWithTasksSortedByPriority(id)
            return ResponseEntity.ok(result)
    }

    @GetMapping("/with-tasks",
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTagsWithTasks(
    ): ResponseEntity<Set<TagNoTasksDTO>> {

        val result = tagService.findTagsHavingTasks()
        return ResponseEntity.ok(result)
    }
}