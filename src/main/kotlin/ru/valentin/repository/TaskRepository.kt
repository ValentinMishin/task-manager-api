package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.valentin.model.Task
import java.time.LocalDate

interface TaskRepository : JpaRepository<Task, Long> {
//    fun findAllByDueDateOrderByTypePriorityDescDueDateAsc(dueDate: LocalDate): List<Task>
}