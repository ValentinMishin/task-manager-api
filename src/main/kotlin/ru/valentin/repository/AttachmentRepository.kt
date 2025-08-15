package ru.valentin.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.valentin.model.Attachment

interface AttachmentRepository : JpaRepository<Attachment, Long> {}