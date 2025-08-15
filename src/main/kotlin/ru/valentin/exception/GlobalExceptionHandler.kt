package ru.valentin.exception

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.valentin.dto.error.ErrorResponse
import ru.valentin.exception.attachment.AttachmentAlreadyAppliedToTaskException
import ru.valentin.exception.attachment.AttachmentIOException
import ru.valentin.exception.attachment.AttachmentIsEmptyException
import ru.valentin.exception.attachment.AttachmentNeedNameException
import ru.valentin.exception.attachment.AttachmentNotFoundAtServerStorageException
import ru.valentin.exception.attachment.AttachmentNotFoundException
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        AttachmentIsEmptyException::class,
        AttachmentAlreadyAppliedToTaskException::class,
        AttachmentNeedNameException::class)
    fun handleAttachmentRequestException(ex: RuntimeException) :
        ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = ex.message ?: ""
            ))
    }

    @ExceptionHandler(AttachmentNotFoundException::class)
    fun handleMediaTypeException(ex: AttachmentNotFoundException) : ResponseEntity<Void> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleMissingElementsException(ex: RuntimeException)
                : ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message ?: ""
            ))
    }

    @ExceptionHandler(
        AttachmentIOException::class,
        AttachmentNotFoundAtServerStorageException::class)
    fun handleAttachmentServerException(ex: RuntimeException) :
            ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = "Внутренняя ошибка на сервере."
            ))
    }
}