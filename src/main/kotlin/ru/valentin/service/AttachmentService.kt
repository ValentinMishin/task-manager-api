package ru.valentin.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.valentin.exception.attachment.AttachmentNeedNameException
import ru.valentin.exception.attachment.AttachmentNotFoundAtServerStorageException
import ru.valentin.exception.attachment.AttachmentNotFoundException
import ru.valentin.exception.attachment.AttachmentIOException
import ru.valentin.exception.attachment.AttachmentAlreadyAppliedToTaskException
import ru.valentin.model.Attachment
import ru.valentin.repository.AttachmentRepository
import ru.valentin.repository.TaskRepository
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.util.UUID
import javax.persistence.EntityNotFoundException

@Service
class AttachmentService(
    private val attachmentRepository: AttachmentRepository,
    private val taskRepository: TaskRepository,
    @Value("\${file.upload-dir}") private val uploadDir: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
//        создать каталог
        File(uploadDir).mkdirs()
    }

    fun uploadFile(taskId: Long, file: MultipartFile) {
        val task = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Задача с ID $taskId не найдена") }

        if (task.attachment != null) {
            throw AttachmentAlreadyAppliedToTaskException("Вложение уже есть, удалите существующее")
        }
        val originalFilename = file.originalFilename ?:
            throw AttachmentNeedNameException("У файла должно быть имя")

        val fileExtension = originalFilename.substringAfterLast('.', "")
        val uniqueFileName = "${UUID.randomUUID()}.$fileExtension"
        val filePath = Paths.get(uploadDir, uniqueFileName).toString()

        // Сохранить
        try {
            file.transferTo(File(filePath))
        } catch (io: IOException) {
            log.error("Saving attachment:\n"+io.stackTraceToString())
            throw AttachmentIOException(io)
        }
        catch (state: IllegalStateException) {
            log.error("Saving attachment:\n"+state.stackTraceToString())
            throw AttachmentIOException(state)
        }

        val attachment = Attachment(
            fileName = originalFilename,
            filePath = filePath,
            mimeType = file.contentType ?: "application/octet-stream",
            fileSize = file.size
        )

        task.attachment = attachment
        taskRepository.save(task)
    }

    fun getAttachmentFile(taskId: Long): Pair<Resource, Attachment> {
        val task = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Задача с ID $taskId не найдена") }

        val attachment = task.attachment
            ?: throw AttachmentNotFoundException("Вложения для задачи с ID $taskId не найдено")

        val file = File(attachment.filePath)
        if (!file.exists()) {
            log.error("${attachment.filePath} not exists")
            throw AttachmentNotFoundAtServerStorageException(
                "Файл по пути ${attachment.filePath} не найден")
        }

        val resource: Resource = FileSystemResource(file)
        return Pair(resource, attachment)
    }

    fun deleteAttachment(taskId: Long) {
        val task = taskRepository.findById(taskId)
            .orElseThrow { EntityNotFoundException("Задача с ID $taskId не найдена") }

        val attachment = task.attachment
            ?: throw EntityNotFoundException("Вложения для задачи с ID $taskId не найдено")

        // Delete
        try {
            File(attachment.filePath).delete()
        } catch (ex : Exception) {
            log.error("Deleting attachment:\n"+ex.stackTraceToString())
            throw AttachmentIOException(ex)
        }

        // Remove
        task.attachment = null
        taskRepository.save(task)
    }
}