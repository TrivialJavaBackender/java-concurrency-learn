# Ex19: Logback JSON конфигурация

**Модуль:** 5 — Logging
**Сложность:** ★★☆☆☆
**Тема:** logback-spring.xml, LogstashEncoder, Spring profiles

## Контекст

Приложение логирует в plain text формате. В production логи собираются Loki/Elasticsearch,
которые не могут нормально парсить plain text. Нужно переключить production на JSON формат,
сохранив читаемый формат для разработки.

## Задача

Написать `logback-spring.xml` с JSON форматом для production и текстовым для разработки.

Требования:
- В профиле `production`: JSON через `LogstashEncoder`, уровень INFO
- В профиле `!production` (dev): `PatternLayout` с читаемым форматом, уровень DEBUG
- Кастомные поля в JSON: `app` (имя приложения), `env` (окружение)
- MDC поля автоматически добавляются к каждой строке лога
- Async appender в production для производительности

## Файлы для изменения

- `logback-spring.xml` — написать с нуля

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] `<springProfile name="production">` содержит `LogstashEncoder`
- [ ] `<springProfile name="!production">` содержит `PatternLayout`
- [ ] `<customFields>` с app и env присутствуют
- [ ] `AsyncAppender` используется в production профиле
- [ ] `PatternLayout` в dev включает `%X{requestId}` для MDC

## Полезные ссылки

- [theory/LOGGING.md — раздел 5: Logback конфигурация](../../theory/LOGGING.md)
