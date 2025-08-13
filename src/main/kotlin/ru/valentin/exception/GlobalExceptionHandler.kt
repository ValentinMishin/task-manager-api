package ru.valentin.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.valentin.dto.error.ErrorResponse
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message ?: "Requested resource not found"
            ))
    }

    @ExceptionHandler(TagHasNoTasksException::class)
    fun handleTagHasNoTasks(ex: TagHasNoTasksException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = ex.message ?: "Requested resource not found"
            ))
    }
}