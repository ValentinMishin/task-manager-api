package ru.valentin.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.valentin.dto.response.AuthResponse
import ru.valentin.exception.auth.UsernameAlreadyExistsException
import ru.valentin.auth.jwt.JwtUtil
import ru.valentin.model.User
import ru.valentin.model.ClientRole
import ru.valentin.repository.UserRepository

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun authenticate(username: String, password: String): AuthResponse {

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )

        val userDetails = authentication.principal as UserDetails
        val token = jwtUtil.generateToken(userDetails)

        return AuthResponse(
            token = token,
            username = username,
            roles = userDetails.authorities.map { it.authority }.toSet()
        )
    }

    fun createUser(username: String, password: String): User {
        if (userRepository.existsByClientName(username)) {
            throw UsernameAlreadyExistsException("Пользователь с логином $username уже зарегистрирован")
        }

        val user = User(
            clientName = username,
            clientPassword = passwordEncoder.encode(password),
            roles = setOf(ClientRole.ROLE_USER)
        )

        return userRepository.save(user)
    }
}