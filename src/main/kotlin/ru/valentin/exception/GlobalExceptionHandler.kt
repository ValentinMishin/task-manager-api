package ru.valentin.exception

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import ru.valentin.dto.error.ErrorResponse
import ru.valentin.dto.error.ValidationError
import ru.valentin.dto.error.ValidationErrorResponse
import ru.valentin.exception.attachment.AttachmentAlreadyAppliedToTaskException
import ru.valentin.exception.attachment.AttachmentIOException
import ru.valentin.exception.attachment.AttachmentIsEmptyException
import ru.valentin.exception.attachment.AttachmentNeedNameException
import ru.valentin.exception.attachment.AttachmentNotFoundAtServerStorageException
import ru.valentin.exception.attachment.AttachmentNotFoundException
import javax.persistence.EntityNotFoundException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class GlobalExceptionHandler(
    @Autowired private val messageSource: MessageSource
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.allErrors.map { error ->
            ValidationError(
                field = (error as? FieldError)?.field ?: error.objectName,
                message = resolveErrorMessage(error) ?: "",
                rejectedValue = (error as? FieldError)?.rejectedValue?.toString()
            )
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                errors = errors
            ))
    }

    private fun resolveErrorMessage(error: ObjectError): String? {
        //Проверяем прямое сообщение из аннотации
        if (!error.defaultMessage.isNullOrEmpty()) {
            return error.defaultMessage!!
        }

        //Пытаемся найти локализованное сообщение
        val codes = error.codes ?: arrayOf("validation.error.default")
        return try {
            messageSource.getMessage(
                codes[0],
                error.arguments,
                codes.joinToString(", "),
                LocaleContextHolder.getLocale()
            )
        } catch (e: NoSuchMessageException) {
            codes.firstOrNull() ?: "validation.error.default"
        }
    }


    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.error("Error processing request ${request.parameterMap}", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = "Внутренняя ошибка на сервере. Логи сохранены"
            ))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationExceptions(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val errors = ex.constraintViolations
            .associate { it.propertyPath.toString() to it.message }

        return ResponseEntity.badRequest()
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = "Валидация не пройдена",
                errors = errors
            ))
    }

    @ExceptionHandler(
        AttachmentIsEmptyException::class,
        AttachmentAlreadyAppliedToTaskException::class,
        AttachmentNeedNameException::class)
    fun handleAttachmentRequestException(ex: RuntimeException) :
        ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                message = ex.message ?: "Некорректный запрос для передачи вложения"
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
                message = ex.message ?: "Некорректные данные для поиска сущностей"
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
                message = "Внутренняя ошибка при работе с вложениями"
            ))
    }
}