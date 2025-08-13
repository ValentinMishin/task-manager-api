package ru.valentin.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.valentin.model.Task
import java.time.LocalDate

interface TaskRepository : JpaRepository<Task, Long> {

    @Query(
        value = """
            SELECT tsk.*  FROM tag tg
            JOIN task_tag tt ON tg.id = tt.tag_id
            JOIN task tsk ON tsk.id = tt.task_id
            join task_type tp on tp.id = tsk.type_id  
            WHERE tt.tag_id = :tagId
            order by tp.priority desc, tsk.due_date asc
        """,
        nativeQuery = true
    )
    fun findByIdWithTasksSortedByPriority(@Param("tagId") tagId: Long): List<Task>

    @Query(
        value = """
            select t.id , t.title , t.type_id, t.description , t.due_date , t.created_at , t.updated_at   from task t 
            join task_type tt on t.type_id  = tt.id
            where t.due_date = :dueDate
            order by tt.priority asc
        """,
        nativeQuery = true
    )
    fun findAllByDateOrderByTypePriority(@Param("dueDate") dueDate: LocalDate,
                                         pageable: Pageable
    ): Page<Task>

    @Modifying
    @Query(
        value = "DELETE FROM task_tag WHERE task_id = :taskId",
        nativeQuery = true
    )
    fun deleteTagsFromTask(@Param("taskId") taskId: Long)
}