package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.valentin.model.Task

interface TaskRepository : JpaRepository<Task, Long> {

}