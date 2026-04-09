# Ex07: Docker Security

**Модуль:** 1 — Docker
**Сложность:** ★★★★☆
**Тема:** Container security, capabilities, read-only filesystem, seccomp

## Контекст

Security-аудит выявил, что контейнеры в production запускаются с избыточными привилегиями:
root пользователь, все Linux capabilities, writable filesystem. Это увеличивает blast radius
при компрометации приложения. Нужно применить принцип наименьших привилегий.

## Задача

Написать `Dockerfile` и `docker-compose.yml` с усиленными настройками безопасности
для Spring Boot приложения.

Требования:
- Контейнер запускается от non-root пользователя (uid > 1000)
- Все Linux capabilities сброшены, добавлены только необходимые
- Файловая система контейнера только для чтения, кроме директорий tmp и logs
- В compose файле явно запрещено повышение привилегий (`no-new-privileges`)
- Образ не содержит shell (или shell недоступен для non-root)
- Health check работает без curl (используй wget или Java)

## Файлы для изменения

- `Dockerfile` — с security настройками
- `docker-compose.yml` — с security_opt и cap_drop

## Проверка

Упражнение считается выполненным, когда:
- [ ] `USER` с uid > 1000 (не только имя пользователя)
- [ ] `docker-compose.yml` содержит `cap_drop: [ALL]`
- [ ] `docker-compose.yml` содержит `security_opt: [no-new-privileges:true]`
- [ ] `read_only: true` для файловой системы контейнера
- [ ] `tmpfs` или writable volume для директорий где нужна запись
- [ ] `docker buildx build --dry-run` проходит без ошибок

## Полезные ссылки

- [theory/DOCKER.md — раздел 7: Антипаттерны](../../theory/DOCKER.md)
