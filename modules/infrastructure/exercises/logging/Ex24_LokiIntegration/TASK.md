# Ex24: Loki + Promtail + Grafana

**Модуль:** 5 — Logging
**Сложность:** ★★★★★
**Тема:** Loki, Promtail, Grafana, docker-compose, LogQL

## Контекст

Логи приложения уходят в stdout контейнера и нигде не агрегируются. При проблемах нужно
заходить в каждый Pod и смотреть логи вручную. Нужно поднять стек централизованного
логирования: Loki как хранилище, Promtail для сбора логов контейнеров, Grafana для UI.

## Задача

Написать `docker-compose.yml` для стека Loki + Promtail + Grafana + приложение.

Требования:
- `loki`: образ `grafana/loki:2.9.0`, конфиг `./loki-config.yaml`
- `promtail`: образ `grafana/promtail:2.9.0`, читает логи всех Docker контейнеров
- `grafana`: образ `grafana/grafana:10.0.0`, Loki datasource преднастроен
- Приложение логирует в JSON (используй logback-spring.xml из Ex19)
- Promtail парсит JSON поля и создаёт labels: `app`, `level`
- Все сервисы в одной сети, Grafana доступна на порту 3000
- Health check для Loki и Grafana

## Файлы для изменения

- `docker-compose.yml`
- `loki-config.yaml`
- `promtail-config.yaml`
- `grafana-datasource.yaml` (provisioning)

## Проверка

Упражнение считается выполненным, когда:
- [ ] `docker-compose config` выполняется без ошибок
- [ ] Все сервисы в одной сети
- [ ] Loki datasource в Grafana provisioning
- [ ] Promtail конфиг: `docker_sd_configs` или `static_configs` с docker socket
- [ ] `pipeline_stages` с `json` и `labels` в promtail конфиге
- [ ] Healthcheck для Loki: `http://localhost:3100/ready`

## Полезные ссылки

- [theory/LOGGING.md — раздел 6: Loki](../../theory/LOGGING.md)
