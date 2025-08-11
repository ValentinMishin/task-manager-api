package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.scheduling.config.Task
import ru.valentin.model.TaskType
import java.util.Optional

interface TaskTypeRepository : JpaRepository<TaskType, Long> {
//    fun findAllByOrderByPriorityAsc(): List<TaskType>
    fun existsByCode(code: String): Boolean

    fun findByCode(code: String): TaskType?

    @Query("SELECT t FROM TaskType t WHERE t.code IN :codes")
    fun findExistingTypes(@Param("codes") codes: Set<String>): Set<TaskType>
}