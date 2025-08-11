package ru.valentin

import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener
import ru.valentin.model.Tag
import ru.valentin.model.Task
import ru.valentin.model.TaskType
import ru.valentin.repository.TagRepository
import ru.valentin.repository.TaskRepository
import ru.valentin.repository.TaskTypeRepository
import java.time.LocalDate
import kotlin.jvm.java

class DataInitializationTestExecutionListener()
    : AbstractTestExecutionListener() {

    override fun beforeTestClass(testContext: TestContext) {
        val appContext = testContext.applicationContext

        val taskTypeRepository = appContext.getBean(TaskTypeRepository::class.java)
        val tagRepository = appContext.getBean(TagRepository::class.java)
        val taskRepository = appContext.getBean(TaskRepository::class.java)

        taskRepository.deleteAll()
        tagRepository.deleteAll()
        taskTypeRepository.deleteAll()

        val regular = TaskType(code = "regular", priority = 1, description = "Обычная задача")
            .also { taskTypeRepository.save(it) }
        val important = TaskType(code = "important", priority = 2, description = "Важная задача")
            .also { taskTypeRepository.save(it) }
        val urgent = TaskType(code = "urgent", priority = 3, description = "Срочная задача")
            .also { taskTypeRepository.save(it) }

        val tag0 = Tag(title = "Разработка") //0
        val tag1 = Tag(title = "Тестирование") //1
        val tag2 = Tag(title = "Документация") //2
        val tag3 = Tag(title = "Багфикс")//3
        val tag4 = Tag(title = "Инфраструктура")//4
        val tag5 = Tag(title = "Безопасность")//5
        val tag6 = Tag(title = "Аналитика")//6
        val tag7 = Tag(title = "Дизайн") //7
        val tag8 = Tag(title = "Оптимизация")//8
//        val tag9 = Tag(title = "Поддержка")//9

        val task0 = Task(
            title = "Обновить документацию API",
            type = regular,
            description = "Актуализировать Swagger-документацию",
            dueDate = LocalDate.now().plusDays(14) // Разработка, Документация
        ).apply { addAllTags(listOf(tag0,tag2)) }

//        val tasks = mutableListOf(
//                Task(
//                    title = "Обновить документацию API",
//                    type = taskTypeRepository.findById(1).orElseThrow(),
//                    description = "Актуализировать Swagger-документацию",
//                    dueDate = LocalDate.now().plusDays(14) // Разработка, Документация
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[2])) },
//                Task(
//                    title = "Провести code review",
//                    type = taskTypeRepository.findById(1).orElseThrow(),
//                    dueDate = LocalDate.now().plusDays(3),
//                ).apply { addTag(EntityCreation.tags[0]) },
//                Task(
//                    title = "Обновить зависимости",
//                    type = taskTypeRepository.findById(1).orElseThrow(),
//                    description = "Проверить актуальность gradle-зависимостей",
//                    dueDate = LocalDate.now().plusDays(7),
//                ).apply { addTag(EntityCreation.tags[4]) },
//                Task(
//                    title = "Исправить критический баг в авторизации",
//                    type = taskTypeRepository.findById(2).orElseThrow(),
//                    description = "Пользователи не могут войти после последнего обновления",
//                    dueDate = LocalDate.now().plusDays(2),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[3], EntityCreation.tags[5])) },
//                Task(
//                    title = "Подготовить отчет по аналитике",
//                    type = taskTypeRepository.findById(2).orElseThrow(),
//                    dueDate = LocalDate.now().plusDays(5),
//                ).apply { addTag(EntityCreation.tags[6]) },
//                Task(
//                    title = "Рефакторинг модуля платежей",
//                    type = taskTypeRepository.findById(2).orElseThrow(),
//                    description = "Улучшить архитектуру для поддержки новых провайдеров",
//                    dueDate = LocalDate.now().plusDays(10),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[8])) },
//                Task(
//                    title = "Исправить уязвимость XSS",
//                    type = taskTypeRepository.findById(3).orElseThrow(),
//                    description = "Обнаружена критическая уязвимость на странице профиля",
//                    dueDate = LocalDate.now(),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[5])) },
//                Task(
//                    title = "Восстановить работоспособность БД",
//                    type = taskTypeRepository.findById(3).orElseThrow(),
//                    description = "Сервер БД упал в production-среде",
//                    dueDate = LocalDate.now(),
//                ).apply { addTag(EntityCreation.tags[4]) },
//                Task(
//                    title = "Срочный релиз фикса",
//                    type = taskTypeRepository.findById(3).orElseThrow(),
//                    dueDate = LocalDate.now().plusDays(1),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[3])) },
//                Task(
//                    title = "Разработать новый API-эндпоинт",
//                    type = taskTypeRepository.findById(1).orElseThrow(),
//                    description = "Для интеграции с CRM-системой",
//                    dueDate = LocalDate.now().plusDays(21),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[6])) },
//                Task(
//                    title = "Оптимизировать SQL-запросы",
//                    type = taskTypeRepository.findById(2).orElseThrow(),
//                    dueDate = LocalDate.now().plusDays(7),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[8])) },
//                Task(
//                    title = "Обновить дизайн личного кабинета",
//                    type = taskTypeRepository.findById(1).orElseThrow(),
//                    dueDate = LocalDate.now().plusDays(30),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[7])) },
//                Task(
//                    title = "Настроить мониторинг",
//                    type = taskTypeRepository.findById(2).orElseThrow(),
//                    description = "Внедрить Prometheus + Grafana",
//                    dueDate = LocalDate.now().plusDays(14),
//                ).apply { addTag(EntityCreation.tags[4]) },
//                Task(
//                    title = "Написать unit-тесты",
//                    type = taskTypeRepository.findById(1).orElseThrow(),
//                    dueDate = LocalDate.now().plusDays(5),
//                ).apply { addTag(EntityCreation.tags[1]) }
//        )
//        taskRepository.saveAll(tasks)
    }
}