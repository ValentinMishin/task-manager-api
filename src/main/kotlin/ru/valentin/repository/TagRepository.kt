package ru.valentin.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.valentin.dto.select.tag.TagNoTasksDTO
import ru.valentin.dto.select.tag.TagNoTasksView
import ru.valentin.model.Tag
import ru.valentin.model.Task
import java.util.Optional

interface TagRepository : JpaRepository<Tag, Long> {
    // Получаем ID задач по ID тега
    @Query(
        value = "SELECT task_id FROM task_tag WHERE tag_id = :tagId",
        nativeQuery = true
    )
    fun findTaskIdsByTagId(@Param("tagId") tagId: Long): List<Long>

    // Удаление связей
    @Modifying
    @Query(
        value = "DELETE FROM task_tag WHERE tag_id = :tagId",
        nativeQuery = true
    )
    fun deleteTagRelationships(@Param("tagId") tagId: Long)

    fun findByTitle(title: String): Tag?

    @Query(
        value = """
            select distinct tg.id as id , tg.title as title from tag tg
            join task_tag tt on tt.tag_id = tg.id
            order by tg.id asc
        """,
        nativeQuery = true
    )
    fun findTagsWithTasks(): Set<TagNoTasksView>

    @Query(
        value = """
            select  t.id as id,
		            t.title as title 
            from task_tag tt
            join tag t on tt.tag_id = t.id
            where tt.task_id = :taskId
        """,
        nativeQuery = true
    )
    fun findTagsByTask(@Param("taskId") taskId: Long): Set<TagNoTasksView>

    @Query(
        value = """
            SELECT EXISTS(SELECT 1 FROM task_tag WHERE tag_id = :tagId)
        """,
        nativeQuery = true
    )
    fun hasTask(@Param("tagId") tagId: Long) : Boolean
}