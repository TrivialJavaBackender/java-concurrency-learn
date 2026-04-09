# Ex10: Health Probes

**Модуль:** 2 — Kubernetes
**Сложность:** ★★★☆☆
**Тема:** livenessProbe, readinessProbe, startupProbe

## Контекст

Spring Boot приложение стартует около 40 секунд (прогрев кэшей при старте). После запуска
периодически уходит в "задумчивость" из-за долгих GC пауз. Kubernetes постоянно
перезапускает Pod во время старта, потому что liveness probe срабатывает слишком рано.
Трафик приходит на Pod, который ещё не готов к работе.

## Задача

Добавить три типа probe в `deployment.yaml` корректно, решив описанные проблемы.

Требования:
- `startupProbe` защищает медленный старт: максимальное время ожидания 60 секунд
- `readinessProbe` использует `/actuator/health/readiness` — трафик только на готовый Pod
- `livenessProbe` использует `/actuator/health/liveness` — ДРУГОЙ endpoint
- `livenessProbe` терпим к кратковременным задержкам (GC паузы до 5 секунд)
- Все probe с разумными `timeoutSeconds` и `failureThreshold`

## Файлы для изменения

- `deployment.yaml` — написать с нуля (включает все три типа probe)

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex10_Probes/` без ошибок
- [ ] `startupProbe.failureThreshold * startupProbe.periodSeconds >= 60`
- [ ] `livenessProbe.path != readinessProbe.path`
- [ ] `livenessProbe.timeoutSeconds >= 5` (учитывает GC паузы)
- [ ] Все три probe используют HTTP GET на порт 8080

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 6: Health Probes](../../theory/KUBERNETES.md)
