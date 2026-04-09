# Ex05: Docker Volumes — Персистентность данных

**Модуль:** 1 — Docker
**Сложность:** ★★☆☆☆
**Тема:** Named volumes, bind mounts, персистентность

## Контекст

После каждого `docker compose down` данные в PostgreSQL исчезают. Это ломает локальную
разработку: нужно заново создавать данные после каждого рестарта. Также в разработке
удобно монтировать конфиги приложения с хоста, чтобы не пересобирать образ при каждом
изменении настроек.

## Задача

Настроить `docker-compose.yml` с правильным использованием volumes для разных целей.

Требования:
- Postgres данные сохраняются между `docker compose down` и `docker compose up` (named volume)
- Файл `./config/application-local.properties` монтируется в `/app/config/` (bind mount)
- Postgres также монтирует `./init.sql` в директорию для инициализационных скриптов
- Named volumes явно объявлены в секции `volumes:`
- Права доступа к volumes настроены корректно (non-root user в приложении)

## Файлы для изменения

- `docker-compose.yml` — написать с нуля
- `config/application-local.properties` — создать с любым содержимым

## Проверка

Упражнение считается выполненным, когда:
- [ ] Named volume для `/var/lib/postgresql/data`
- [ ] Bind mount для config файла приложения
- [ ] Bind mount для `init.sql` в `/docker-entrypoint-initdb.d/`
- [ ] `docker-compose config` выполняется без ошибок
- [ ] Объяснение: чем named volume отличается от bind mount (в комментарии в yml)

## Полезные ссылки

- [theory/DOCKER.md — раздел 5: Docker Networking](../../theory/DOCKER.md)
