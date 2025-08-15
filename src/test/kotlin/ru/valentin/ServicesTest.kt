package ru.valentin

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.ActiveProfiles
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.NewTagDto
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import ru.valentin.service.TagService
import ru.valentin.service.TaskService
import java.time.LocalDate
import javax.transaction.Transactional

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class ServicesTest {
    private lateinit var taskService: TaskService
    private lateinit var tagService: TagService

    private lateinit var regularTaskType: TaskType
    private lateinit var importantTaskType: TaskType
    private lateinit var urgentTaskType: TaskType

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskTypeRepository: TaskTypeRepository
    @Autowired
    private lateinit var tagRepository: TagRepository

    @BeforeEach
    fun init() {
        regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()
        taskService = TaskService(taskRepository,tagRepository,taskTypeRepository)
        tagService = TagService(taskRepository,tagRepository,taskTypeRepository)
    }

    @Test
    @Commit
    fun testCreation() {
        val request2 = CreateTaskDto(
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

        val createResp = taskService.createTask(request2)

        val delete = taskService.deleteTask(7)
    }

    @Test
    fun testSelect() {
        val test = taskService.getTasksByDateWithPrioritySort(
            LocalDate.of(2025,8,18),
            0,
            5
        )

        val test1 = tagService.findTagsHavingTasks()

        val test2 = tagService.findTagWithTasksSortedByPriority(1)

        val test4 = taskTypeRepository.findAllByOrderByPriorityDesc()
    }
}