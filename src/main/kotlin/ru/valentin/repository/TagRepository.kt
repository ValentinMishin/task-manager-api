package ru.valentin.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import ru.valentin.model.Tag
import java.util.Optional

interface TagRepository : JpaRepository<Tag, Long> {
}