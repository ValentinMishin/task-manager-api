package ru.valentin

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Commit
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.model.TaskType
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

    private lateinit var regularTaskType: TaskType
    private lateinit var importantTaskType: TaskType
    private lateinit var urgentTaskType: TaskType
    @Test
    @Commit
    fun testSaveTaskAndTag() {
        regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()

        val tagBugfix = Tag(title = "Багфикс")
            .let { tagRepository.save(it) }
            .also { assertEquals("Багфикс", it.title)}

        val onlyTask = Task(
            title = "Обработать запрос в поддержку",
            type = regularTaskType,
            description = "Пользователь сообщил о проблеме с пользовательским интерфейсом",
            dueDate = LocalDate.now().plusDays(1)
        ).let { taskRepository.save(it) }
            .also { assertEquals("Обработать запрос в поддержку", it.title) }
        //c Задачами
        val tagSupport = Tag(title = "Поддержка")
        val task1 = Task(
            title = "Обработать запрос в поддержку",
            type = regularTaskType,
            description = "Пользователь сообщил о проблеме с авторизацией",
            dueDate = LocalDate.now().plusDays(1)
        ).apply { addTag(tagSupport) }
        val task2 = Task(
            title = "Обновить FAQ поддержки",
            type = importantTaskType,
            description = "Добавить новые частые вопросы по платежам",
            dueDate = LocalDate.now().plusDays(3)
        ).apply { addTag(tagSupport) }
        tagSupport.let { tagRepository.save(it) }
            .also { assertEquals(2, it.tasks.size) }
    }

    @Commit
    @Test
    fun testUpdate() {
        regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()

        //создать без тегов
        val tagBugfix = Tag(title = "Багфикс")
        var savedBugfix = tagRepository.save(tagBugfix)

        //изменить заголовок, добавить 2 задачи
        savedBugfix.title = "Багфикс(обновленный)"
        val task1 = Task(
            title = "Исправить падение приложения при оплате",
            type = urgentTaskType,
            description = "При нажатии 'Оплатить' в корзине приложение крашится на iOS 15.4",
            dueDate = LocalDate.now().plusDays(1)
        ).apply { addTag(savedBugfix) }
        var task2 = Task(
            title = "Починить перекрытие текста в мобильной версии",
            type = importantTaskType,
            description = "На iPhone SE текст кнопки 'Подробнее' вылезает за границы",
            dueDate = LocalDate.now().plusDays(3),
        ).apply { addTag(savedBugfix) }
        taskRepository.save(task1)
        taskRepository.save(task2)

//        добавить задачу 3
        savedBugfix = tagRepository.findById(savedBugfix.id).orElseThrow()
        val task3 = Task(
            title = "Исправить 500 ошибку в /api/v1/profile",
            type = importantTaskType,
            description = "Сервер возвращает 500 при GET-запросе с пустым токеном",
            dueDate = LocalDate.now().plusDays(2),
        ).apply { addTag(savedBugfix) }

//        удалить у задачи2 тег
        savedBugfix = tagRepository.findById(savedBugfix.id).orElseThrow()
        task2
            .apply { removeTag(savedBugfix) }
            .let { taskRepository.save(it) }

//        изменить задачу2
        task2
            .apply { title = "REPAIR" }
            .let { taskRepository.save(it) }
    }

    @Commit
    @Test
    fun testDelete() {
        regularTaskType = taskTypeRepository.findByCode("regular") ?: throw NoSuchElementException()
        importantTaskType = taskTypeRepository.findByCode("important") ?: throw NoSuchElementException()
        urgentTaskType = taskTypeRepository.findByCode("urgent") ?: throw NoSuchElementException()

        val tagBugfix = Tag(title = "Багфикс")

        val task1 = Task(
            title = "Исправить падение приложения при оплате",
            type = urgentTaskType,
            description = "При нажатии 'Оплатить' в корзине приложение крашится на iOS 15.4",
            dueDate = LocalDate.now().plusDays(1)
        ).apply { addTag(tagBugfix) }
        var task2 = Task(
            title = "Починить перекрытие текста в мобильной версии",
            type = importantTaskType,
            description = "На iPhone SE текст кнопки 'Подробнее' вылезает за границы",
            dueDate = LocalDate.now().plusDays(3),
        ).apply { addTag(tagBugfix) }

        var savedT1 = taskRepository.save(task1)
        var savedT2 = taskRepository.save(task2)

        //связанные задачи
        val tasksIds = tagRepository.findTaskIdsByTagId(tagBugfix.id)
//        удаление связей
        tagRepository.deleteTagRelationships(tagBugfix.id)
//         удаление задач
        taskRepository.deleteAllById(tasksIds)
//         удаление тега
        tagRepository.deleteById(tagBugfix.id)
    }
}