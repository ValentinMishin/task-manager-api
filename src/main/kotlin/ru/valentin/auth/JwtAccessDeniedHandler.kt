package ru.valentin.auth

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import ru.valentin.dto.error.ErrorResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: org.springframework.security.access.AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val errorResponse = ErrorResponse(
            status = HttpServletResponse.SC_FORBIDDEN,
            message = "Нехватает прав для получения ресурса.",
            details = "Запрашиваемый ресурс : ${request.requestURI}",
        )

        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
        response.writer.write(mapper.writeValueAsString(errorResponse))
    }
}