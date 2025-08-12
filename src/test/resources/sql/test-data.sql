INSERT INTO task_type (id, code, priority, description) VALUES
(1, 'BUG', 1, 'Исправление ошибок'),
(2, 'FEATURE', 2, 'Новая функциональность'),
(3, 'IMPROVEMENT', 3, 'Улучшение существующей функциональности'),
(4, 'DOCUMENTATION', 4, 'Работа с документацией'),
(5, 'URGENT', 0, 'Срочные задачи');

INSERT INTO tag (id, title, created_at, updated_at) VALUES
(1, 'backend', CURRENT_TIMESTAMP, NULL),
(2, 'frontend', CURRENT_TIMESTAMP, NULL),
(3, 'database', CURRENT_TIMESTAMP, NULL),
(4, 'security', CURRENT_TIMESTAMP, NULL),
(5, 'ui/ux', CURRENT_TIMESTAMP, NULL),
(6, 'critical', CURRENT_TIMESTAMP, NULL),
(7, 'refactoring', CURRENT_TIMESTAMP, NULL),
(8, 'testing', CURRENT_TIMESTAMP, NULL);

INSERT INTO task (id, title, type_id, description, due_date, created_at, updated_at) VALUES
(1, 'Исправить ошибку авторизации', 1, 'Пользователи не могут войти после обновления', CURRENT_DATE + INTERVAL '5 days', CURRENT_TIMESTAMP, NULL),
(2, 'Добавить пагинацию', 2, 'Реализовать пагинацию на странице списка', CURRENT_DATE + INTERVAL '10 days', CURRENT_TIMESTAMP, NULL),
(3, 'Оптимизировать запросы', 3, 'Оптимизировать медленные SQL-запросы', CURRENT_DATE + INTERVAL '7 days', CURRENT_TIMESTAMP, NULL),
(4, 'Обновить документацию API', 4, 'Добавить новые endpoint в Swagger', CURRENT_DATE + INTERVAL '3 days', CURRENT_TIMESTAMP, NULL),
(5, 'Починить критический баг', 5, 'Система падает при обработке больших файлов', CURRENT_DATE + INTERVAL '1 days', CURRENT_TIMESTAMP, NULL),
(6, 'Рефакторинг сервиса нотификаций', 3, NULL, CURRENT_DATE + INTERVAL '14 days', CURRENT_TIMESTAMP, NULL);

INSERT INTO task_tag (task_id, tag_id) VALUES
(1, 1), (1, 4),  -- backend, security
(2, 2), (2, 5),  -- frontend, ui/ux
(3, 1), (3, 3),  -- backend, database
(4, 1), (4, 7),  -- backend, refactoring
(5, 1), (5, 4), (5, 6),  -- backend, security, critical
(6, 1), (6, 8);  -- backend, testing