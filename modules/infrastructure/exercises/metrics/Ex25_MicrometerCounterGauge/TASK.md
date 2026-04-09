# Ex25: Counter и Gauge с Micrometer

**Модуль:** 6 — Metrics
**Сложность:** ★★★☆☆
**Тема:** Counter, Gauge, MeterRegistry, теги, именование метрик

## Контекст

Product менеджер хочет видеть сколько заказов создаётся в минуту, сколько сейчас
в обработке, и какой процент успешных vs неуспешных. Нужно добавить бизнес-метрики
в `OrderMetrics` сервис.

## Задача

Реализовать `OrderMetrics` с Counter и Gauge метриками.

Требования:
- Counter `orders_created_total` с тегами `status` (success/failed) и `type` (standard/express)
- Gauge `orders_active` — текущее количество заказов в обработке (атомарный счётчик)
- Gauge `orders_queue_size` — размер очереди (внешний поставщик значения)
- Метрики доступны на `/actuator/prometheus`
- Именование по Prometheus соглашению: snake_case, суффикс `_total` для Counter
- Каждая метрика с `description()`

## Файлы для изменения

- `OrderMetrics.java` — написать с нуля

## Рабочий процесс

Реализуй `OrderMetrics.java` в этой директории, затем скопируй в проект и скомпилируй:
```bash
cp exercises/metrics/Ex25_MicrometerCounterGauge/OrderMetrics.java src/main/java/by/pavel/metrics/
mvn compile -q
```

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] Counter создан через `Counter.builder("orders_created_total").tag(...).register(registry)`
- [ ] Gauge создан через `registry.gauge("orders_active", atomicInteger)`
- [ ] Метрики имеют `description()`
- [ ] Теги: `status` (success/failed), `type` (standard/express)
- [ ] Нет camelCase в именах метрик

## Полезные ссылки

- [theory/METRICS.md — раздел 1: Типы метрик](../../theory/METRICS.md)
- [theory/METRICS.md — раздел 3: Micrometer](../../theory/METRICS.md)
