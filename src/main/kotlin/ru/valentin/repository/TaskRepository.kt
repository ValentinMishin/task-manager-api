package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.valentin.model.Task

interface TaskRepository : JpaRepository<Task, Long> {
//    fun findAllByDueDateOrderByTypePriorityDescDueDateAsc(dueDate: LocalDate): List<Task>

    @Modifying
    @Query(
        value = "DELETE FROM task_tag WHERE task_id = :taskId",
        nativeQuery = true
    )
    fun deleteTagsFromTask(@Param("taskId") taskId: Long)
}