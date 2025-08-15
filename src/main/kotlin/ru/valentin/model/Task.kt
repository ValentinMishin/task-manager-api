package ru.valentin.model

import ru.valentin.dto.response.task.TaskNoTagsDTO
import ru.valentin.dto.response.task.TaskWithTagsDTO
import javax.persistence.*
import java.time.LocalDate

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

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false)
    var dueDate: LocalDate,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "task_tag",
        joinColumns = [JoinColumn(name = "task_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "attachment_id")
    var attachment: Attachment? = null

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
            tags = tags.map { it.toShortDto() }.toMutableSet()
        )
    }

    override fun toString(): String {
        return "Task(id=$id, title='$title', type=${type.id}, description=$description, dueDate=$dueDate, tags=${tags.map { it.id }})"
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

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + dueDate.hashCode()
        return result
    }
}