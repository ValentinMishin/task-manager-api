package ru.valentin

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.transaction.annotation.Isolation
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import java.time.LocalDate
import javax.transaction.Transactional
import kotlin.test.assertEquals

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestExecutionListeners(
    listeners = [DataInitializationTestExecutionListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@Transactional
class RepositoriesTest {
    @Autowired
    private lateinit var taskRepository: TaskRepository
    @Autowired
    private lateinit var taskTypeRepository: TaskTypeRepository
    @Autowired
    private lateinit var tagRepository: TagRepository

    @Test
    @Commit
    fun testSaveTaskAndTag() {
        val regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        val importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        val urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()

        val onlyTag = Tag(title = "Багфикс").let { tagRepository.save(it) }
        onlyTag.also { assertEquals("Багфикс", it.title)}

        val onlyTask = Task(
            title = "Обработать запрос в поддержку",
            type = regularTaskType,
            description = "Пользователь сообщил о проблеме с пользовательским интерфейсом",
            dueDate = LocalDate.now().plusDays(1)
        ).let { taskRepository.save(it) }
        onlyTask.also { assertEquals("Обработать запрос в поддержку", it.title) }

        //c Задачами
        var tagWithTasks : Tag = Tag(title = "Поддержка")
        val task1 = Task(
            title = "Обработать запрос в поддержку",
            type = regularTaskType,
            description = "Пользователь сообщил о проблеме с авторизацией",
            dueDate = LocalDate.now().plusDays(1)
        ).apply { addTag(tagWithTasks) }
        val task2 = Task(
            title = "Обновить FAQ поддержки",
            type = importantTaskType,
            description = "Добавить новые частые вопросы по платежам",
            dueDate = LocalDate.now().plusDays(3)
        ).apply { addTag(tagWithTasks) }
        tagWithTasks = tagWithTasks.let { tagRepository.saveAndFlush(it) }
            .also { assertEquals(2, it.tasks.size) }
    }

    @Test
    fun testUpdate() {
        println("UPDATE TEST")
    }

}