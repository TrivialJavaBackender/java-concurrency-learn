# Ex23: Log Levels по профилям

**Модуль:** 5 — Logging
**Сложность:** ★★☆☆☆
**Тема:** logback-spring.xml профили, Spring Actuator loggers endpoint

## Контекст

В production уровень DEBUG генерирует слишком много логов и замедляет приложение.
В разработке INFO недостаточно — нужны подробности. Иногда в production нужно временно
включить DEBUG для конкретного пакета без перезапуска приложения.

## Задача

Настроить `logback-spring.xml` с разными уровнями по профилям и проверить динамическое
изменение уровня через Actuator.

Требования:
- Профиль `production`: root уровень INFO, пакет `by.pavel` — INFO
- Профиль `!production`: root уровень INFO, пакет `by.pavel` — DEBUG
- Профиль `production`: пакет `org.springframework` — WARN (убрать лишние логи фреймворка)
- `application.properties` должен экспонировать endpoint `/actuator/loggers`
- В README объяснить как динамически сменить уровень через curl

## Файлы для изменения

- `logback-spring.xml` — написать
- `README.md` — с примером curl команды для смены уровня

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] `<springProfile name="production">` имеет root level INFO
- [ ] `<springProfile name="!production">` имеет `by.pavel` level DEBUG
- [ ] `org.springframework` ограничен до WARN в production
- [ ] README содержит правильную curl команду для Actuator loggers

## Полезные ссылки

- [theory/LOGGING.md — раздел 7: Антипаттерны](../../theory/LOGGING.md)
