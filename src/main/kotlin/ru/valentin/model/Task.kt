package ru.valentin.model

import javax.persistence.*
import java.time.LocalDate
import java.sql.Timestamp
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
    var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(nullable = true)
    var updatedAt: Timestamp? = null,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "task_tag",
        joinColumns = [JoinColumn(name = "task_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp(System.currentTimeMillis())
    }

    fun addTag(tag: Tag) {
        tags.add(tag)
        tag.tasks.add(this)
    }

    fun removeTag(tag: Tag) {
        tags.remove(tag)
        tag.tasks.remove(this)
    }
}