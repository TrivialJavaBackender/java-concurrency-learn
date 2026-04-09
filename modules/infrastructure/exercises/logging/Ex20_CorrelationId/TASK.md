# Ex20: Correlation ID Filter

**Модуль:** 5 — Logging
**Сложность:** ★★★☆☆
**Тема:** OncePerRequestFilter, MDC, X-Request-ID, correlation

## Контекст

При расследовании инцидентов невозможно найти все логи одного запроса — нет общего
идентификатора. Нужно реализовать correlation ID: каждый входящий HTTP запрос получает
уникальный ID, который присутствует во всех строках лога этого запроса.

## Задача

Написать `CorrelationFilter` — Spring фильтр, добавляющий request ID в MDC.

Требования:
- Если заголовок `X-Request-ID` есть в запросе — использовать его значение
- Если нет — генерировать `UUID.randomUUID().toString()`
- Поместить в MDC под ключом `requestId`
- Добавить заголовок `X-Request-ID` в HTTP ответ
- Обязательно очищать MDC в `finally` блоке
- Фильтр должен быть зарегистрирован как Spring Bean

## Файлы для изменения

- `CorrelationFilter.java` — написать с нуля

## Рабочий процесс

Реализуй `CorrelationFilter.java` в этой директории, затем скопируй в проект и скомпилируй:
```bash
cp exercises/logging/Ex20_CorrelationId/CorrelationFilter.java src/main/java/by/pavel/logging/
mvn compile -q
```

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] Класс наследует `OncePerRequestFilter`
- [ ] Логика: берём из заголовка, иначе генерируем UUID
- [ ] `MDC.put("requestId", ...)` вызывается до `chain.doFilter(...)`
- [ ] `MDC.clear()` вызывается в `finally` блоке
- [ ] `response.setHeader("X-Request-ID", requestId)` присутствует
- [ ] `@Component` аннотация

## Полезные ссылки

- [theory/LOGGING.md — раздел 4: Correlation ID](../../theory/LOGGING.md)
