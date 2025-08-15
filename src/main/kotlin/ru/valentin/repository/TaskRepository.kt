package ru.valentin.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.valentin.dto.response.task.TaskNoTagsView
import ru.valentin.model.Task
import java.time.LocalDate
import javax.validation.constraints.Pattern

interface TaskRepository : JpaRepository<Task, Long> {

    @Query(
        value = """
            SELECT 
		            tsk.id as id,
                    tsk.title as title,
                    tsk.due_date as dueDate,
                    tsk.description as description,
                    tsk.type_id as typeId,
                    tp.code as typeCode,
                    tp.priority as typePriority,
                    tp.description as typeDescription
            FROM tag tg
            JOIN task_tag tt ON tg.id = tt.tag_id
            JOIN task tsk ON tsk.id = tt.task_id
            join task_type tp on tp.id = tsk.type_id  
            WHERE tt.tag_id = :tagId
            order by tp.priority asc, tsk.due_date asc
        """,
        nativeQuery = true
    )
    fun findAllByTagIdSortedByPriority(@Param("tagId") tagId: Long): List<TaskNoTagsView>

    @Query(
        value = """
            select 
					tsk.id as id,
                    tsk.title as title,
                    tsk.due_date as dueDate,
                    tsk.description as description,
                    tsk.type_id as typeId,
                    tp.code as typeCode,
                    tp.priority as typePriority,
                    tp.description as typeDescription
            from task tsk
            join task_type tp on tp.id  = tsk.type_id 
            where tsk.due_date = :dueDate
            order by tp.priority asc
        """,
        nativeQuery = true
    )
    fun findAllByDateOrderByTypePriority
                (@Param("dueDate") dueDate: LocalDate,
                 pageable: Pageable
    ): Page<TaskNoTagsView>

    @EntityGraph(attributePaths = ["type", "tags"])
    @Query("SELECT t FROM Task t WHERE t.dueDate = :dueDate")
    fun findAllByDueDateWithTasks(
        @Param("dueDate") dueDate: LocalDate,
        pageable: Pageable
    ): Page<Task>

    @Modifying
    @Query(
        value = "DELETE FROM task_tag WHERE task_id = :taskId",
        nativeQuery = true
    )
    fun deleteTagsFromTask(@Param("taskId") taskId: Long)
}