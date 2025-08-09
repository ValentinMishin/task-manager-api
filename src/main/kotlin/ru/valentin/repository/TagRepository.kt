package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.valentin.model.Tag

interface TagRepository : JpaRepository<Tag, Long> {

}