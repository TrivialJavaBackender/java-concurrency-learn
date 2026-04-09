# Ex03: Docker Compose — App + Postgres + Redis

**Модуль:** 1 — Docker
**Сложность:** ★★★☆☆
**Тема:** docker-compose, сети, depends_on, healthcheck, volumes

## Контекст

Приложение в разработке требует базу данных PostgreSQL и кэш Redis. Нужно настроить
локальное окружение через docker-compose так, чтобы одна команда `docker compose up`
поднимала всю инфраструктуру в правильном порядке.

## Задача

Создать `docker-compose.yml` с тремя сервисами: приложение, PostgreSQL и Redis.

Требования:
- Сервис `app` должен стартовать только после того, как `postgres` реально готов
  принимать соединения (не просто запустился)
- Postgres данные должны сохраняться между перезапусками
- Все сервисы должны быть в одной сети и видеть друг друга по имени сервиса
- Для `postgres` и `redis` настроить healthcheck
- Конфигурация подключения передаётся в `app` через environment variables
- Порт `8080` приложения доступен на хосте

## Файлы для изменения

- `docker-compose.yml` — написать с нуля

## Проверка

Упражнение считается выполненным, когда:
- [ ] `docker-compose config` выполняется без ошибок
- [ ] `depends_on` с `condition: service_healthy` настроен для postgres
- [ ] Named volume для postgres данных
- [ ] Healthcheck для postgres: `pg_isready`
- [ ] Healthcheck для redis: `redis-cli ping`
- [ ] Все сервисы в явно объявленной сети

## Полезные ссылки

- [theory/DOCKER.md — раздел 6: Docker Compose](../../theory/DOCKER.md)
