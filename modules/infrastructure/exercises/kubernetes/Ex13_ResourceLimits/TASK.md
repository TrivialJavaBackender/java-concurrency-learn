# Ex13: Resource Requests и Limits

**Модуль:** 2 — Kubernetes
**Сложность:** ★★★★☆
**Тема:** requests, limits, QoS классы, OOMKilled

## Контекст

Команда наблюдает периодические OOMKilled у Pod с приложением и одновременно жалобы
на CPU throttling, замедляющий ответы API. При анализе выясняется: limits.memory
выставлен слишком мало, а limits.cpu — в 2x от requests, что вызывает throttling под
нагрузкой. Нужно разобраться с QoS классами и правильно выставить ресурсы.

## Задача

Создать три Deployment демонстрирующих разные QoS классы, и один "правильный" Deployment
для production.

Требования:
- `deployment-besteffort.yaml` — без requests и limits (BestEffort QoS)
- `deployment-burstable.yaml` — requests < limits (Burstable QoS)
- `deployment-guaranteed.yaml` — requests == limits (Guaranteed QoS)
- `deployment-production.yaml` — правильная конфигурация для Spring Boot: Guaranteed QoS,
  memory limit с запасом 2x от нормального потребления, CPU limit спорен (см. комментарий)
- В каждом файле комментарий объясняющий QoS класс и его поведение при нехватке ресурсов

## Файлы для изменения

- `deployment-besteffort.yaml`
- `deployment-burstable.yaml`
- `deployment-guaranteed.yaml`
- `deployment-production.yaml`

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex13_ResourceLimits/` без ошибок
- [ ] BestEffort: нет requests и limits
- [ ] Burstable: requests < limits хотя бы по одному ресурсу
- [ ] Guaranteed: requests == limits для CPU и memory
- [ ] Production deployment: Guaranteed QoS, адекватные значения для JVM

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 9: Resource Requests vs Limits](../../theory/KUBERNETES.md)
