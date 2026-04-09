# Ex11: Horizontal Pod Autoscaler

**Модуль:** 2 — Kubernetes
**Сложность:** ★★★☆☆
**Тема:** HPA, CPU autoscaling, resource requests

## Контекст

Приложение испытывает пиковую нагрузку в рабочие часы. В ночное время запросов мало.
Держать 10 реплик круглосуточно расточительно. Нужно автоматически масштабировать
количество Pod в зависимости от нагрузки на CPU.

## Задача

Создать `deployment.yaml` с правильными resource requests и `hpa.yaml` для автоскейлинга.

Требования:
- Deployment с 2 репликами (минимум для production)
- Resource requests обязательны (HPA не работает без них)
- HPA масштабирует от 2 до 10 реплик при CPU утилизации > 70%
- При снижении нагрузки scale down происходит постепенно (не резко)
- HPA должен иметь разумные stabilizationWindowSeconds для scale down

## Файлы для изменения

- `deployment.yaml` — с resource requests/limits
- `hpa.yaml` — написать с нуля

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex11_HPA/` без ошибок
- [ ] `resources.requests.cpu` задан в Deployment
- [ ] HPA `minReplicas: 2`, `maxReplicas: 10`
- [ ] CPU target `averageUtilization: 70`
- [ ] `behavior.scaleDown.stabilizationWindowSeconds` задан
- [ ] `apiVersion: autoscaling/v2`

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 7: HPA](../../theory/KUBERNETES.md)
