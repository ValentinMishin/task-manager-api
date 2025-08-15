package ru.valentin.model

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "attachment")
data class Attachment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var fileName: String,

    @Column(nullable = false)
    var filePath: String,

    @Column(nullable = false)
    var mimeType: String,

    @Column(nullable = false)
    var fileSize: Long,

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: LocalDateTime? = null
)