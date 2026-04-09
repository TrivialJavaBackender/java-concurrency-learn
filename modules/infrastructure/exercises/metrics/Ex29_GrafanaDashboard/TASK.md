# Ex29: Grafana Dashboard

**Модуль:** 6 — Metrics
**Сложность:** ★★★★☆
**Тема:** Grafana JSON, панели, переменные, PromQL в Grafana

## Контекст

У команды нет единого места для мониторинга приложения. Нужно создать Grafana dashboard
с ключевыми метриками Spring Boot сервиса: RPS, error rate, latency percentile, JVM метрики.

## Задача

Создать `dashboard.json` для Grafana и `docker-compose.yml` со всем стеком.

Требования:
- Dashboard содержит 6 панелей:
  1. **RPS** (Graph): `rate(http_server_requests_seconds_count[5m])`
  2. **Error Rate %** (Stat): процент 5xx запросов
  3. **p99 Latency** (Graph): `histogram_quantile(0.99, ...)`
  4. **p50 vs p99** (Graph): обе линии на одном графике
  5. **JVM Heap Used** (Graph): `jvm_memory_used_bytes{area="heap"}`
  6. **Active HTTP Requests** (Stat): текущие активные запросы
- Переменная `$instance` для выбора инстанса приложения
- Dashboard с `uid` и `title`
- `docker-compose.yml`: Prometheus + Grafana + app с provisioning

## Файлы для изменения

- `dashboard.json` — Grafana dashboard JSON
- `docker-compose.yml`
- `grafana-datasource.yaml` — Prometheus datasource provisioning
- `grafana-dashboard-provisioning.yaml` — provisioning dashboard из файла

## Проверка

Упражнение считается выполненным, когда:
- [ ] `docker-compose config` без ошибок
- [ ] `dashboard.json` валидный JSON с минимум 4 панелями
- [ ] Grafana datasource provisioning для Prometheus присутствует
- [ ] Dashboard provisioning файл ссылается на `dashboard.json`
- [ ] `histogram_quantile` используется для latency панелей

## Полезные ссылки

- [theory/METRICS.md — раздел 5: Grafana Dashboard](../../theory/METRICS.md)
