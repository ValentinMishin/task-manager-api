package ru.valentin.auth

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import ru.valentin.dto.error.ErrorResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"

        val errorResponse = ErrorResponse(
            status = HttpServletResponse.SC_UNAUTHORIZED,
            message = "Необходима аутентификация. Приложите токен или проверьте его корректность",
            details = "Запрашиваемый ресурс : ${request.requestURI}",
        )

        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
        response.writer.write(mapper.writeValueAsString(errorResponse))
    }
}