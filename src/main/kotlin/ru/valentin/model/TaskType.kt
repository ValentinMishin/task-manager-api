package ru.valentin.model

import ru.valentin.dto.TaskTypeNoTasksDto
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
    //UTILS dto
    fun toShortDto(): TaskTypeNoTasksDto {
        return TaskTypeNoTasksDto(
            id = id,
            code = code,
            priority = priority,
            description = description
        )
    }

    companion object {
        fun createDefaultTypes(): MutableSet<TaskType> {
            return mutableSetOf(
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