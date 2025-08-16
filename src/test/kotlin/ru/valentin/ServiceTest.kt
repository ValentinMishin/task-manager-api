package ru.valentin

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Commit
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.NewTagDto
import ru.valentin.dto.request.NewTaskDto
import ru.valentin.dto.request.UpdateTagDto
import ru.valentin.dto.request.UpdateTaskDto
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import ru.valentin.service.TagService
import ru.valentin.service.TaskService
import java.time.LocalDate
import javax.persistence.EntityManager
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

    @Autowired
    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun init() {
        regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()
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
                NewTagDto("важно"),
                NewTagDto("презентация")
            )
        )
        val createResp = taskService.createTask(createRequestTask)
        assertEquals(4, createResp.tags.size)
        val newTag = tagRepository.findAll()
        println(newTag.last())
    }

    @Test
    fun `updated task with id=1 should remove 1,4 tag and add 2,3 and analytic` () {
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
        val idS = updateTask.tags.map { (id, _) -> id }.toSet()
        assertEquals(setOf(3L,10L,2L), idS)
    }

    @Test
    fun `updated tag with id=4 should remove task 1,5 add task 2,3 and apply new Task` () {
        val updateTagDto = UpdateTagDto(
            title = "super-security",
            tasksToAddIds = setOf(2L, 3L),
            newTasksToAdd = setOf(NewTaskDto(
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