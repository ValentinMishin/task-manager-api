package ru.valentin.model

import org.hibernate.annotations.DynamicUpdate
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

    @ManyToMany(mappedBy = "tags")
    @OrderBy("type.priority DESC, dueDate Asc")
    var tasks: MutableSet<Task> = mutableSetOf()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp(System.currentTimeMillis())
    }

    fun hasTasks(): Boolean = tasks.isNotEmpty()

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