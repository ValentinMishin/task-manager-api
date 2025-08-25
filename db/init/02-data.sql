TRUNCATE TABLE client, client_role, task_type, task_tag, task, tag RESTART IDENTITY;
-- admin : admin
-- client : client
insert into client (username, password) values
('admin', '$2y$12$l9nyPaWm917Nt2zxWMU6J.ThdCnQ/RdbGlYotMeg1zc9MNPDHFfAe'),
('client', '$2y$12$fVL.tbFwWIuwde6SAsjgOuNaxFtvff9RGEoCY4ZhANJ1om0.SIq2O');

insert into client_role(client_id, role) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

INSERT INTO task_type (code, priority, description) VALUES
('regular', 2, 'Обычные задачи (стандартный приоритет)'),
('important', 1, 'Важные задачи (высокий приоритет)'),
('urgent', 0, 'Срочные задачи (критический приоритет)');

INSERT INTO tag (title) VALUES
('backend'),
('frontend'),
('database'),
('security'),
('ui/ux'),
('critical'),
('refactoring'),
('testing'),
('tag-with-no tasks');

INSERT INTO task (title, type_id, description, due_date) VALUES
('Исправить ошибку авторизации', 3, 'Пользователи не могут войти после обновления', CURRENT_DATE + INTERVAL '5 days'),
('Добавить пагинацию', 1, 'Реализация стандартной функциональности', CURRENT_DATE + INTERVAL '10 days'),
('Оптимизировать запросы', 2, 'Критически важная оптимизация для production', CURRENT_DATE + INTERVAL '2 days'),
('Обновить документацию', 1, 'Рутинное обновление документации', CURRENT_DATE + INTERVAL '7 days'),
('Починить критический баг', 3, 'Система падает при обработке данных', CURRENT_DATE + INTERVAL '1 days'),
('Рефакторинг сервиса', 2, 'Улучшение кода важного модуля', CURRENT_DATE + INTERVAL '5 days');

INSERT INTO task_tag (task_id, tag_id) VALUES
(1, 1), (1, 4),  -- backend, security (ошибка авторизации)
(2, 2), (2, 5),  -- frontend, ui/ux (пагинация)
(3, 1), (3, 3),  -- backend, database (оптимизация)
(4, 1), (4, 7),  -- backend, refactoring (документация)
(5, 1), (5, 4), (5, 6),  -- backend, security, critical (критический баг)
(6, 1), (6, 8);  -- backend, testing (рефакторинг)