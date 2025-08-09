package ru.valentin.spring.model

import javax.persistence.*

@Entity
@Table(name = "task_type")
data class TaskType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val code: String,

    @Column(nullable = false)
    val priority: Int,

    @Column(nullable = true, columnDefinition = "TEXT")
    val description: String? = null
) {
    companion object {
        fun createDefaultTypes(): List<TaskType> {
            return listOf(
                TaskType(code = "regular", priority = 1,
                    description = "Обычная задача"),
                TaskType(code = "important", priority = 2,
                    description = "Важная задача"),
                TaskType(code = "urgent", priority = 3,
                    description = "Срочная задача")
            )
        }
    }
}