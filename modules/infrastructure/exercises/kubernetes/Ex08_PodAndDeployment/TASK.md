# Ex08: Pod и Deployment

**Модуль:** 2 — Kubernetes
**Сложность:** ★★☆☆☆
**Тема:** Deployment, ReplicaSet, RollingUpdate, labels, selectors

## Контекст

Нужно задеплоить Spring Boot приложение в Kubernetes. Приложение должно работать в
нескольких репликах для высокой доступности, а обновления — происходить без даунтайма.

## Задача

Создать `deployment.yaml` и `service.yaml` для Spring Boot приложения.

Требования:
- 3 реплики приложения
- Стратегия обновления RollingUpdate: не более 1 Pod недоступно, не более 1 Pod сверх лимита
- Labels должны однозначно идентифицировать приложение (app + version)
- Selector в Deployment должен совпадать с labels в Pod template
- Resource requests и limits обязательны
- ClusterIP Service связан с Deployment через одинаковые labels

## Файлы для изменения

- `deployment.yaml` — написать с нуля
- `service.yaml` — написать с нуля

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex08_PodAndDeployment/` не выдаёт ошибок
- [ ] `replicas: 3`
- [ ] `strategy.type: RollingUpdate` с настроенными параметрами
- [ ] `selector.matchLabels` совпадает с `template.metadata.labels`
- [ ] `resources.requests` и `resources.limits` заданы для контейнера
- [ ] Service типа ClusterIP с правильным selector

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 3: Deployment](../../theory/KUBERNETES.md)
- [theory/KUBERNETES.md — раздел 4: Service](../../theory/KUBERNETES.md)
