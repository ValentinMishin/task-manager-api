package ru.valentin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.valentin.auth.JwtAccessDeniedHandler
import ru.valentin.auth.JwtAuthenticationEntryPoint
import ru.valentin.auth.filter.JwtAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                exceptionHandlingConfigurer ->
                exceptionHandlingConfigurer
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .antMatchers("/api/auth/**").permitAll()

                    .antMatchers(HttpMethod.POST, "/api/tasks/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/tasks/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/api/tags/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.PUT, "/api/tags/**").hasRole("ADMIN")
                    .antMatchers(HttpMethod.DELETE, "/api/tags/**").hasRole("ADMIN")

                    .antMatchers("/api/**").authenticated()

                    .anyRequest().permitAll()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}