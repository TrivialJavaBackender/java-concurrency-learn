# Ex26: Histogram и Timer

**Модуль:** 6 — Metrics
**Сложность:** ★★★★☆
**Тема:** Timer, Histogram, histogram_quantile, percentile

## Контекст

Команда хочет видеть не только среднее время ответа API, но и p95/p99 latency —
именно они показывают "медленный хвост", который влияет на пользовательский опыт.
Нужно добавить Timer метрику для HTTP обработчиков.

## Задача

Реализовать `LatencyMetrics` с Timer и написать PromQL запросы для percentile.

Требования:
- `Timer` с именем `http_request_duration_seconds` и тегами `method`, `uri`, `status`
- `publishPercentileHistogram()` включён (нужен для `histogram_quantile` в PromQL)
- Service Level bucket'ы: 50ms, 100ms, 200ms, 500ms, 1s, 2s, 5s
- Метод `record(Runnable)` обёртка для автоматического измерения
- Файл `queries.promql` с запросами p50, p95, p99 для endpoint `/api/orders`

## Файлы для изменения

- `LatencyMetrics.java` — написать с нуля
- `queries.promql` — 3 PromQL запроса

## Рабочий процесс

Реализуй `LatencyMetrics.java` в этой директории, затем скопируй в проект и скомпилируй:
```bash
cp exercises/metrics/Ex26_Histogram/LatencyMetrics.java src/main/java/by/pavel/metrics/
mvn compile -q
```

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] `Timer.builder(...).publishPercentileHistogram().register(registry)`
- [ ] `.serviceLevelObjectives(...)` с конкретными bucket'ами
- [ ] Теги: method, uri, status
- [ ] `queries.promql` содержит `histogram_quantile(0.95, ...)` и `histogram_quantile(0.99, ...)`

## Полезные ссылки

- [theory/METRICS.md — раздел 1: Типы метрик — Histogram](../../theory/METRICS.md)
- [theory/METRICS.md — раздел 4: PromQL](../../theory/METRICS.md)
