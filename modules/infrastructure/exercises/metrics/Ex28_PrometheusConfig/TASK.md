# Ex28: Prometheus конфигурация и Alerting Rules

**Модуль:** 6 — Metrics
**Сложность:** ★★★★☆
**Тема:** prometheus.yml, alerting rules, scrape config, SLO-based alerts

## Контекст

Prometheus настроен с дефолтной конфигурацией — scrape только себя. Нужно добавить
scrape Spring Boot приложения, настроить alerting rules по SLO: error rate > 1%
и p99 latency > 1 секунды должны поднимать алерт.

## Задача

Написать `prometheus.yml` с scrape конфигурацией и `rules.yml` с alerting rules.

Требования:
- `prometheus.yml`: scrape_interval 15s, scrape Spring Boot на порту 8080 с path `/actuator/prometheus`
- `prometheus.yml`: подключает `rules.yml` через `rule_files`
- `rules.yml` — 3 alerting правила:
  - `HighErrorRate`: error rate (5xx) > 1% за последние 5 минут, severity: critical
  - `HighP99Latency`: p99 latency > 1s за последние 5 минут, severity: warning
  - `AppDown`: приложение не отвечает (нет метрик 2 минуты), severity: critical
- Каждое правило имеет `annotations.summary` и `annotations.description`
- `docker-compose.yml` поднимает Prometheus + Spring Boot

## Файлы для изменения

- `prometheus.yml`
- `rules.yml`
- `docker-compose.yml`

## Проверка

Упражнение считается выполненным, когда:
- [ ] `docker-compose config` без ошибок
- [ ] `prometheus.yml` имеет scrape_configs для Spring Boot
- [ ] `rule_files` ссылается на `rules.yml`
- [ ] 3 alerting правила присутствуют
- [ ] `rate()` используется в alert expressions (не `increase()`)
- [ ] `for: 5m` или аналогичный период перед срабатыванием

## Полезные ссылки

- [theory/METRICS.md — раздел 2: Prometheus Pull Model](../../theory/METRICS.md)
- [theory/METRICS.md — раздел 5: Grafana Dashboard](../../theory/METRICS.md)
