package ru.valentin.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.valentin.dto.request.AuthRequest
import ru.valentin.dto.response.AuthResponse
import ru.valentin.service.AuthService
import javax.validation.Valid

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @Operation(
        summary = "Аутентификация клиента"
    )
    @PostMapping("/login")
    fun login(@RequestBody @Valid authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.authenticate(authRequest.username, authRequest.password)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Регистрация клиента, только с правами на просмотр"
    )
    @PostMapping("/register")
    fun register(@RequestBody @Valid authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val user = authService.createUser(authRequest.username, authRequest.password)
        val response = authService.authenticate(authRequest.username, authRequest.password)
        return ResponseEntity.ok(response)
    }
}