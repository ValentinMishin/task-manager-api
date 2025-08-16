package ru.valentin.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.valentin.dto.response.tag.TagNoTasksView
import ru.valentin.model.Tag
import java.util.Optional

interface TagRepository : JpaRepository<Tag, Long> {
    // Получаем ID задач по ID тега
    @Query(
        value = "SELECT task_id FROM task_tag WHERE tag_id = :tagId",
        nativeQuery = true
    )
    fun findTaskIdsById(@Param("tagId") tagId: Long): List<Long>

    @EntityGraph(attributePaths = ["tasks.type"])
    fun findWithTasksAndTaskTypeById(@Param("tagId") tagId: Long): Optional<Tag>

    // Удаление связей
    @Modifying
    @Query(
        value = "DELETE FROM task_tag WHERE tag_id = :tagId",
        nativeQuery = true
    )
    fun removeLinksToTasksById(@Param("tagId") tagId: Long)

    fun findByTitle(title: String): Tag?

    @Query(
        value = """
            select distinct tg.id as id , tg.title as title from tag tg
            join task_tag tt on tt.tag_id = tg.id
            order by tg.id asc
        """,
        nativeQuery = true
    )
    fun findAllWithTasks(): Set<TagNoTasksView>

    @Query(
        value = """
            SELECT EXISTS(SELECT 1 FROM task_tag WHERE tag_id = :tagId)
        """,
        nativeQuery = true
    )
    fun hasTask(@Param("tagId") tagId: Long) : Boolean
}