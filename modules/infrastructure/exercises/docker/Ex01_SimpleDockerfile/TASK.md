# Ex01: Базовый Dockerfile

**Модуль:** 1 — Docker
**Сложность:** ★★☆☆☆
**Тема:** Dockerfile, non-root user, HEALTHCHECK, ENTRYPOINT vs CMD

## Контекст

У тебя есть Spring Boot приложение, которое собирается в `jar` файл. Нужно упаковать его
в Docker образ для запуска в production. Приложение слушает порт 8080 и предоставляет
эндпоинт `/actuator/health` для проверки здоровья.

## Задача

Написать `Dockerfile` для Spring Boot jar приложения так, чтобы контейнер был готов к
production использованию.

Требования:
- Использовать `eclipse-temurin:21-jre-alpine` как базовый образ
- Процесс не должен запускаться от root пользователя
- Docker должен иметь возможность автоматически проверять здоровье контейнера
- Образ должен корректно обрабатывать сигналы завершения (SIGTERM)
- Рабочая директория должна быть явно задана

## Файлы для изменения

- `Dockerfile` — написать с нуля

## Проверка

Упражнение считается выполненным, когда:
- [ ] `docker buildx build --dry-run -f exercises/docker/Ex01_SimpleDockerfile/Dockerfile .` не выдаёт ошибок
- [ ] В Dockerfile есть инструкция `USER` с non-root пользователем
- [ ] В Dockerfile есть `HEALTHCHECK`
- [ ] Используется exec-форма ENTRYPOINT: `["java", ...]`
- [ ] Используется `COPY`, а не `ADD`

## Полезные ссылки

- [theory/DOCKER.md — раздел 3: Dockerfile](../../theory/DOCKER.md)
