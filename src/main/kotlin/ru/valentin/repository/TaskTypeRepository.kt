package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.valentin.model.TaskType

interface TaskTypeRepository : JpaRepository<TaskType, Long> {

}