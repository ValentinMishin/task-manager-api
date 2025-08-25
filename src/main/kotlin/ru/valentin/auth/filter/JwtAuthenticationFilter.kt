package ru.valentin.auth.filter

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.valentin.auth.jwt.JwtUtil
import ru.valentin.service.UserDetailsServiceImpl
import javax.security.auth.message.AuthException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val jwt = authHeader.substring(7)

            try {
                val username = jwtUtil.extractUsername(jwt)

                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        val authToken = UsernamePasswordAuthenticationToken( // токен проверен, можно использовать конструктор
                            userDetails,
                            null,
                            userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            } catch (ex: Exception) {
                log.info("Failed attempt to access resource. ${ex.message}")
            }
        }
        filterChain.doFilter(request, response)
    }
}