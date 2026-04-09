# Ex22: MDC propagation в Async

**Модуль:** 5 — Logging
**Сложность:** ★★★★☆
**Тема:** TaskDecorator, MDC propagation, ThreadPoolTaskExecutor

## Контекст

При использовании `@Async` методов requestId из MDC перестаёт появляться в логах —
async методы выполняются в другом потоке, где MDC пустой. Трассировать async операции
по requestId невозможно.

## Задача

Настроить `AsyncConfig` так, чтобы MDC контекст передавался в async потоки.

Требования:
- Класс `AsyncConfig` реализует `AsyncConfigurer`
- `ThreadPoolTaskExecutor` с `TaskDecorator`, копирующим MDC
- Decorator: перед выполнением задачи копирует `MDC.getCopyOfContextMap()` из родительского потока
- Decorator: после выполнения очищает MDC (`MDC.clear()`)
- Executor настроен с разумными параметрами: corePoolSize, maxPoolSize, queueCapacity
- `@EnableAsync` аннотация на классе или конфиг-классе

## Файлы для изменения

- `AsyncConfig.java` — написать с нуля

## Рабочий процесс

Реализуй `AsyncConfig.java` в этой директории, затем скопируй в проект и скомпилируй:
```bash
cp exercises/logging/Ex22_MDCContext/AsyncConfig.java src/main/java/by/pavel/logging/
mvn compile -q
```

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] `implements AsyncConfigurer`
- [ ] `setTaskDecorator(runnable -> { ... })` настроен
- [ ] `MDC.getCopyOfContextMap()` вызывается ДО создания lambda
- [ ] `MDC.setContextMap(mdcContext)` вызывается внутри задачи
- [ ] `MDC.clear()` вызывается в `finally` блоке задачи
- [ ] `@EnableAsync` присутствует

## Полезные ссылки

- [theory/LOGGING.md — раздел 3: MDC — Async проблема](../../theory/LOGGING.md)
