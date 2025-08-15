package ru.valentin.controller

import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.valentin.exception.attachment.AttachmentIsEmptyException
import ru.valentin.service.AttachmentService

@RestController
@RequestMapping("/api/tasks/{taskId}/attachment")
class AttachmentController(
    private val attachmentService: AttachmentService
) {

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
        fun uploadAttachment(
        @PathVariable taskId: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        if (file.isEmpty) {
            throw AttachmentIsEmptyException("Передан пустой файл")
        }
        attachmentService.uploadFile(taskId, file)
        return ResponseEntity.ok().body("Файл прикреплен к задаче с ID $taskId")
    }

    @GetMapping("/download",
        produces = [MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadAttachment(
        @PathVariable taskId: Long
    ): ResponseEntity<Resource> {
        val (resource, attachment) = attachmentService.getAttachmentFile(taskId)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(attachment.mimeType))
            .header(
                "Content-Disposition",
                "attachment; filename=\"${attachment.fileName}\""
            )
            .body(resource)
    }

    @DeleteMapping
    fun deleteAttachment(
        @PathVariable taskId: Long
    ): ResponseEntity<Void> {
        attachmentService.deleteAttachment(taskId)
        return ResponseEntity.noContent().build()
    }
}