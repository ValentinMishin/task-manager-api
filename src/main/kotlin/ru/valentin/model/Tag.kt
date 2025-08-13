package ru.valentin.model

import org.hibernate.annotations.CreationTimestamp
import ru.valentin.dto.TagNoTasksDTO
import ru.valentin.dto.TagWithTasksDTO
import javax.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tag")
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 255)
    var title: String,

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: LocalDateTime? = null,

    @Column(nullable = true)
    var updatedAt: LocalDateTime? = null,

    @ManyToMany(mappedBy = "tags")
    @OrderBy("type.priority DESC, dueDate Asc")
    var tasks: MutableSet<Task> = mutableSetOf()
) {
    //utils TO DTO
    fun toShortDto(): TagNoTasksDTO {
        return TagNoTasksDTO(
            id = id,
            title = title
        )
    }

    fun toDto(): TagWithTasksDTO {
        return TagWithTasksDTO(
            id = id,
            title = title,
            tasks = tasks.map { it.toShortDto() }
        )
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun hasTasks(): Boolean = tasks.isNotEmpty()
    fun hasNoTasks(): Boolean = tasks.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (id != other.id) return false
        if (title != other.title) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}