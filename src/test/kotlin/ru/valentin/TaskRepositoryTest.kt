package ru.valentin

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import ru.valentin.model.Task
import ru.valentin.model.TaskType
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import java.time.LocalDate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskTypeRepository: TaskTypeRepository

    val types = TaskType.createDefaultTypes()

    @BeforeEach
    fun setup() {
        taskTypeRepository.deleteAll()
        taskTypeRepository.saveAll(types)
    }

    @Test
    fun testSaveTask() {
        val taskFixBug = Task(
            title = "Fix",
            type = types[2],
            dueDate = LocalDate.now().minusDays(1)
        )
        taskRepository.save(taskFixBug)

        val taskFixBugId = taskFixBug.id
        val taskFixBuf_ = taskRepository.findById(taskFixBugId)
        println("=======TEST-SAVE========: ${taskFixBuf_}")
    }
}