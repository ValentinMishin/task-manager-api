package ru.valentin.model

import javax.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "tag")
data class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 255)
    var title: String,

    @Column(nullable = false, updatable = false)
    var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(nullable = true)
    var updatedAt: Timestamp? = null,

    @ManyToMany(mappedBy = "tags", cascade = [CascadeType.ALL])
    @OrderBy("type.priority DESC, dueDate Asc")
    var tasks: MutableSet<Task> = mutableSetOf()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp(System.currentTimeMillis())
    }

    @PreRemove
    fun onRemove() {
        tasks.forEach { it.tags.remove(this) }
    }

    fun hasTasks(): Boolean = tasks.isNotEmpty()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (id != other.id) return false
        if (title != other.title) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }
}