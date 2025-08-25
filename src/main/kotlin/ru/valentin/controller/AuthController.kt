package ru.valentin.controller

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

    @PostMapping("/login")
    fun login(@RequestBody @Valid authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val response = authService.authenticate(authRequest.username, authRequest.password)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun register(@RequestBody @Valid authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val user = authService.createUser(authRequest.username, authRequest.password)
        val response = authService.authenticate(authRequest.username, authRequest.password)
        return ResponseEntity.ok(response)
    }
}