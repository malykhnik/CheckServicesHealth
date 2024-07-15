-- ЭТОТ СКРИПТ НУЖЕН ДЛЯ ЗАПОЛНЕНИЯ БД ПОСЛЕ ЗАПУСКА docker-compose,
-- ТАК КАК АВТОМАТИЧЕСКОЙ ИНИЦИАЛИЗАЦИИ БД НЕТ

INSERT INTO roles (id, name) VALUES (1, 'user');
INSERT INTO roles (id, name) VALUES (2, 'admin');

INSERT INTO users (id, username, password, role_id) VALUES (1, 'user1', '$2a$12$UfSyv0l1ZuXAzHAjv16QruWq0ZFV2ZFVZ.CJGzbF70CgAfvP2U3Um', 1);
INSERT INTO users (id, username, password, role_id) VALUES (2, 'user2', '$2a$12$UfSyv0l1ZuXAzHAjv16QruWq0ZFV2ZFVZ.CJGzbF70CgAfvP2U3Um', 1);
INSERT INTO users (id, username, password, role_id) VALUES (3, 'admin', 'admin', 2);

INSERT INTO endpoints (id, username, password, role_id, url, period)
VALUES (1, 'endpoint1', '$2a$12$ONebYb21q/BIEbQfZNT5ieIBEEhoZWZTnp6Jvwk.fpf4J/NgPUHDm', 1,
        'http://sloth-2.suslovd.ru:8080/api/v1/', 30);

INSERT INTO emails (id, receiver) VALUES (1, 'nik.malykh.2024@mail.ru');
