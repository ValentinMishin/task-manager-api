package ru.valentin

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import ru.valentin.dto.create.CreateTaskRequest
import ru.valentin.dto.create.NewTagDto
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import ru.valentin.service.TaskService
import java.time.LocalDate
import javax.transaction.Transactional

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
//@TestExecutionListeners(
//    listeners = [DataInitializationTestExecutionListener::class],
//    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
//)
@Transactional
class ServicesTest {
    private lateinit var service: TaskService

    private lateinit var regularTaskType: TaskType
    private lateinit var importantTaskType: TaskType
    private lateinit var urgentTaskType: TaskType

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskTypeRepository: TaskTypeRepository
    @Autowired
    private lateinit var tagRepository: TagRepository

    @Test
    @Commit
    fun testCreation() {
        regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()

        service = TaskService(taskRepository,tagRepository,taskTypeRepository)

        val request2 = CreateTaskRequest(
            title = "Подготовить презентацию",
            typeId = 2, // Тип с ID=2 (например, "PRESENTATION")
            description = "Слайды для руководства",
            dueDate = LocalDate.now().plusDays(7),
            existingTagIds = emptySet(),
            newTags = setOf(
                NewTagDto("важно"),
                NewTagDto("презентация")
            )
        )

        service.createTask(request2)
    }
}