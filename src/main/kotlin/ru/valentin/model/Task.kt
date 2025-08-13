package ru.valentin.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import ru.valentin.dto.select.task.TaskNoTagsDTO
import ru.valentin.dto.select.task.TaskWithTagsDTO
import javax.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import javax.validation.constraints.FutureOrPresent

@Entity
@Table(name = "task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 255)
    var title: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    var type: TaskType,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    @field:FutureOrPresent(message = "Task planned date must be present or Future")
    var dueDate: LocalDate,

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    var createdAt: LocalDateTime? = null,

    @Column(nullable = true)
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "task_tag",
        joinColumns = [JoinColumn(name = "task_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
) {
    //DTO utils
    fun toShortDto(): TaskNoTagsDTO {
        return TaskNoTagsDTO(
            id = this.id,
            title = this.title,
            type = this.type.toShortDto(),
            dueDate = this.dueDate,
            description = this.description
        )
    }

    fun toDto(): TaskWithTagsDTO {
        return TaskWithTagsDTO(
            id = id,
            title = title,
            type = type.toShortDto(),
            description = description,
            dueDate = dueDate,
            tags = tags.map { it.toShortDto() }.toSet()
        )
    }

    fun addTagWithUpdateTasks(tag: Tag) {
        tags.add(tag)
        tag.tasks.add(this)
    }

    fun removeTagWithUpdateTasks(tag: Tag) {
        tags.remove(tag)
        tag.tasks.remove(this)
    }

    override fun toString(): String {
        return "Task(id=$id, title='$title', type=${type.id}, description=$description, dueDate=$dueDate, createdAt=$createdAt, updatedAt=$updatedAt, tags=${tags.map { it.id }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false
        if (title != other.title) return false
        if (type != other.type) return false
        if (description != other.description) return false
        if (dueDate != other.dueDate) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + dueDate.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}