package ru.valentin

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers
import ru.valentin.dto.request.*
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import ru.valentin.service.TagService
import ru.valentin.service.TaskService
import java.time.LocalDate
import kotlin.test.assertEquals

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
@SpringBootTest
@Transactional
class ServiceTest {

    private lateinit var taskService: TaskService
    private lateinit var tagService: TagService

    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskTypeRepository: TaskTypeRepository
    @Autowired
    private lateinit var tagRepository: TagRepository

    @BeforeEach
    fun init() {
        taskService = TaskService(taskRepository,tagRepository,taskTypeRepository)
        tagService = TagService(taskRepository,tagRepository,taskTypeRepository)
    }

    @Test
    fun `created task with 2 existing tags and apply new 2 tags`() {
        val createRequestTask = CreateTaskDto(
            title = "Подготовить презентацию",
            typeId = 2,
            description = "Слайды для руководства",
            dueDate = LocalDate.now().plusDays(7),
            existingTagIds = setOf(1L,2L, 100L),
            newTags = setOf(
                CreateShortTagDto("важно"),
                CreateShortTagDto("презентация")
            )
        )
        val createResp = taskService.createTask(createRequestTask)
        assertEquals(4, createResp.tags.size)
    }

    @Test
    @Sql("/02-data.sql") //тест на базовых данных, избежать результатов соседних тестов
    fun `updated task with id=1 should remove 1,4 tag and add 2,3 and analytic` () {
        val updateTask = UpdateTaskDto(
            title = "Новое название",
            dueDate = null,
            tagsToAddIds = setOf(3L, 2L),
            tagsToRemoveIds = setOf(1L, 4L),
            newTagsToAdd = setOf(
                CreateShortTagDto("analytic")
            ),
            typeId = null,
            description = null
        ).let { taskService.updateTask(1L, it) }
        val idS = updateTask.tags.map { (id, _) -> id }.toSet()
        assertEquals(setOf(3L,10L,2L), idS)
    }


    @Test
    @Sql("/02-data.sql")
    fun `updated tag with id=4 should remove task 1,5 add task 2,3 and apply new Task` () {
        val updateTagDto = UpdateTagDto(
            title = "super-security",
            tasksToAddIds = setOf(2L, 3L),
            newTasksToAdd = setOf(CreateShortTaskDto(
                "ad-hoc",
                1L,
                "some description",
                LocalDate.now().plusDays(10))
            ),
            tasksToRemoveIds = setOf(1L,5L)
        ).let { tagService.updateTag(4L, it) }
        val idS = updateTagDto.tasks.map { (id, _) -> id }.toSet()
        assertEquals(setOf(2L,3L,7L), idS)
    }

    @Transactional
    @Test
    fun `should delete tag with id=1 with all its tasks` () {
        tagService.deleteTagWithTasks(1L)
        assertEquals(1,taskRepository.findAll().size)
        assertEquals(true, tagRepository.findById(1).isEmpty)
    }

    @Test
    fun `5 days later most priority task has id 1`(){
        val res = taskService.getTasksByDateWithPrioritySort(
            LocalDate.now().plusDays(5),
            0, 5, "asc")
        assertEquals( 1, res.first().id)

    }
    @Test
    fun `tag with id=1 should has 5 tasks` () {
        val res = tagService.findTagWithTasksSortedByPriority(1)
        assertEquals( 5, res.tasks.size)
    }

    @Test
    fun `expected 8 tags having tasks` () {
        val res = tagService.findTagsHavingTasks()
        assertEquals(8,res.size)
    }

    @Test
    fun `expected 3 types of task` () {
        val res = taskTypeRepository.findAllByOrderByPriorityDesc()
        assertEquals(3, res.size)
    }
}