package ru.valentin.spring.model

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
    @OrderBy("type.priority DESC") // Сортировка по приоритету типа задачи
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
}