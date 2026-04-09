# Ex30: PromQL запросы

**Модуль:** 6 — Metrics
**Сложность:** ★★★★★
**Тема:** rate, irate, histogram_quantile, topk, recording rules, label_replace

## Контекст

Команда умеет писать простые PromQL запросы, но при написании алертов и дашбордов
возникают сложности: как правильно агрегировать по нескольким инстансам, как написать
SLO-based алерт, как использовать recording rules для тяжёлых запросов.

## Задача

Написать 10 PromQL запросов в файле `queries.promql` и 3 recording rules в `recording-rules.yml`.

Требования (каждый запрос с комментарием объясняющим что он делает):

1. RPS всего приложения (все endpoint)
2. Error rate (5xx) за 5 минут в процентах
3. p99 latency из histogram по всем endpoint
4. p99 latency сгруппированный по `uri` (по endpoint отдельно)
5. Топ 3 самых медленных endpoint (по p99)
6. Доступность за 1 час (SLI для SLO 99.9%)
7. Активные HTTP соединения (Gauge, без rate)
8. Рост heap памяти за последний час (deriv или delta)
9. Количество GC пауз в минуту (GC counter)
10. Запрос с `label_replace` — нормализовать URI убрав числовые ID

Recording rules в `recording-rules.yml`:
1. `job:http_requests:rate5m` — precomputed RPS
2. `job:http_errors:rate5m` — precomputed error rate
3. `job:http_request_duration_p99:rate5m` — precomputed p99

## Файлы для изменения

- `queries.promql` — 10 запросов с комментариями
- `recording-rules.yml` — 3 recording rules

## Проверка

Упражнение считается выполненным, когда:
- [ ] 10 запросов присутствуют, каждый с комментарием
- [ ] `rate()` используется для Counter метрик (не для Gauge)
- [ ] `histogram_quantile(0.99, sum(rate(...bucket[5m])) by (le, uri))` для запроса 4
- [ ] `topk(3, ...)` присутствует
- [ ] `label_replace` использован синтаксически корректно
- [ ] Recording rules с правильными именами (job:metric:aggregation)

## Полезные ссылки

- [theory/METRICS.md — раздел 4: PromQL](../../theory/METRICS.md)
- [theory/METRICS.md — раздел 6: Кардинальность](../../theory/METRICS.md)
