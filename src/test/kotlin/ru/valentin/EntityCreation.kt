//package ru.valentin
//
//import org.springframework.stereotype.Component
//import ru.valentin.model.Tag
//import ru.valentin.model.Task
//import ru.valentin.model.TaskType
//import java.time.LocalDate
//
//object EntityCreation {
//
//    val taskTypes: MutableList<TaskType>
//        get() = TaskType.createDefaultTypes()
//
//    val tags: MutableList<Tag>
//        get() = mutableListOf(
//            Tag(title = "Разработка"), //0
//            Tag(title = "Тестирование"),//1
//            Tag(title = "Документация"),//2
//            Tag(title = "Багфикс"),//3
//            Tag(title = "Инфраструктура"),//4
//            Tag(title = "Безопасность"),//5
//            Tag(title = "Аналитика"),//6
//            Tag(title = "Дизайн"),//7
//            Tag(title = "Оптимизация")//8
//        )
//
//    val tasks: MutableList<Task>
//        get() {
//            return mutableListOf(
//                Task(
//                    title = "Обновить документацию API",
//                    type = taskTypes[0],
//                    description = "Актуализировать Swagger-документацию",
//                    dueDate = LocalDate.now().plusDays(14) // Разработка, Документация
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[2])) },
//                Task(
//                    title = "Провести code review",
//                    type = taskTypes[0],
//                    dueDate = LocalDate.now().plusDays(3),
//                ).apply { addTag(EntityCreation.tags[0]) },
//                Task(
//                    title = "Обновить зависимости",
//                    type = taskTypes[0],
//                    description = "Проверить актуальность gradle-зависимостей",
//                    dueDate = LocalDate.now().plusDays(7),
//                ).apply { addTag(EntityCreation.tags[4]) },
//                Task(
//                    title = "Исправить критический баг в авторизации",
//                    type = taskTypes[1],
//                    description = "Пользователи не могут войти после последнего обновления",
//                    dueDate = LocalDate.now().plusDays(2),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[3], EntityCreation.tags[5])) },
//                Task(
//                    title = "Подготовить отчет по аналитике",
//                    type = taskTypes[1],
//                    dueDate = LocalDate.now().plusDays(5),
//                ).apply { addTag(EntityCreation.tags[6]) },
//                Task(
//                    title = "Рефакторинг модуля платежей",
//                    type = taskTypes[1],
//                    description = "Улучшить архитектуру для поддержки новых провайдеров",
//                    dueDate = LocalDate.now().plusDays(10),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[8])) },
//                Task(
//                    title = "Исправить уязвимость XSS",
//                    type = taskTypes[2],
//                    description = "Обнаружена критическая уязвимость на странице профиля",
//                    dueDate = LocalDate.now(),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[5])) },
//                Task(
//                    title = "Восстановить работоспособность БД",
//                    type = taskTypes[2],
//                    description = "Сервер БД упал в production-среде",
//                    dueDate = LocalDate.now(),
//                ).apply { addTag(EntityCreation.tags[4]) },
//                Task(
//                    title = "Срочный релиз фикса",
//                    type = taskTypes[2],
//                    dueDate = LocalDate.now().plusDays(1),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[3])) },
//                Task(
//                    title = "Разработать новый API-эндпоинт",
//                    type = taskTypes[0],
//                    description = "Для интеграции с CRM-системой",
//                    dueDate = LocalDate.now().plusDays(21),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[6])) },
//                Task(
//                    title = "Оптимизировать SQL-запросы",
//                    type = taskTypes[1],
//                    dueDate = LocalDate.now().plusDays(7),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[8])) },
//                Task(
//                    title = "Обновить дизайн личного кабинета",
//                    type = taskTypes[0],
//                    dueDate = LocalDate.now().plusDays(30),
//                ).apply { addAllTags(listOf(EntityCreation.tags[0], EntityCreation.tags[7])) },
//                Task(
//                    title = "Настроить мониторинг",
//                    type = taskTypes[1],
//                    description = "Внедрить Prometheus + Grafana",
//                    dueDate = LocalDate.now().plusDays(14),
//                ).apply { addTag(EntityCreation.tags[4]) },
//                Task(
//                    title = "Написать unit-тесты",
//                    type = taskTypes[0],
//                    dueDate = LocalDate.now().plusDays(5),
//                ).apply { addTag(EntityCreation.tags[1]) }
//            )
//        }
//}
//
