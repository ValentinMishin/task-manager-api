package ru.valentin.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.valentin.model.Tag
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
}