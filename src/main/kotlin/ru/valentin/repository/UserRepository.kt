package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.valentin.model.User
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByClientName(username: String): Optional<User>
    fun existsByClientName(username: String): Boolean
}