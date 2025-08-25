package ru.valentin.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import ru.valentin.config.JwtConfig
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.HashMap
import javax.crypto.SecretKey

@Component
class JwtUtil(
    private val jwtConfig: JwtConfig
) {

    private val secretKey: SecretKey = jwtConfig.secretKey()

    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    fun extractRoles(token: String): List<String> {
        return extractAllClaims(token).get("roles", List::class.java) as List<String>
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, Any> = HashMap()
        val roles = userDetails.authorities.map { it.authority }
        claims["roles"] = roles

        return createToken(claims, userDetails.username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(Instant.now().toEpochMilli()))
            .setExpiration(Date(Instant.now().toEpochMilli() + jwtConfig.getExpiration()))
            .signWith(secretKey)
            .compact()
    }

    fun LocalDateTime.toDate(): Date {
        return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }
}