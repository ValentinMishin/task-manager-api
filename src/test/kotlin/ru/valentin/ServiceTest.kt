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
import ru.valentin.dto.request.UpdateTaskDto
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import ru.valentin.service.TagService
import ru.valentin.service.TaskService
import java.time.LocalDate
import javax.transaction.Transactional
import kotlin.test.assertEquals

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class ServiceTest {
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
    fun `created task with 2 existing tags and new tags`() {
        val createRequestTask = CreateTaskDto(
            title = "Подготовить презентацию",
            typeId = 2,
            description = "Слайды для руководства",
            dueDate = LocalDate.now().plusDays(7),
            existingTagIds = setOf(1L,2L, 100L),
            newTags = setOf(
                NewTagDto("важно"),
                NewTagDto("презентация")
            )
        )
        val createResp = taskService.createTask(createRequestTask)
        assertEquals(createResp.tags.size, 2)
    }

    @Test
    fun `updated task with id=1 should remove 1,4 tag and add 2,3 and "analytic"` () {
        val updateTask = UpdateTaskDto(
            title = "Новое название",
            dueDate = null,
            tagsToAddIds = setOf(3L, 2L),
            tagsToRemoveIds = setOf(1L, 4L),
            newTagsToAdd = setOf(
                NewTagDto("analytic")
            ),
            typeId = null,
            description = null
        ).let { taskService.updateTask(1L, it) }
        val idS = updateTask.tags.map { (id, title) -> id }.toSet()
        assertEquals(idS, setOf(3L,10L,2L))
    }

    @Test
    fun `5 days later most priority task has id 1`(){
        val res = taskService.getTasksByDateWithPrioritySort(
            LocalDate.now().plusDays(5),
            0, 5, "asc")
        assertEquals(res.first().id, 1)

    }
    @Test
    fun `tag with id=1 should has 5 tasks` () {
        val res = tagService.findTagWithTasksSortedByPriority(1)
        assertEquals(res.tasks.size, 5)
    }

    @Test
    fun `expected 8 tags having tasks` () {
        val res = tagService.findTagsHavingTasks()
        assertEquals(res.size, 8)
    }

    @Test
    fun `expected 3 types of task` () {
        val res = taskTypeRepository.findAllByOrderByPriorityDesc()
        assertEquals(res.size, 3)
    }
}