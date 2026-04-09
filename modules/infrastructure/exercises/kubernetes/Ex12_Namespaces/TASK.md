# Ex12: Namespaces и ResourceQuota

**Модуль:** 2 — Kubernetes
**Сложность:** ★★★☆☆
**Тема:** Namespace, ResourceQuota, LimitRange

## Контекст

Кластер используется несколькими командами: team-a (production приложение) и team-b
(staging). Команды не должны мешать друг другу: team-b не должна "съесть" все ресурсы
кластера, оставив team-a без CPU и памяти.

## Задача

Создать namespace `production` с ограничениями ресурсов, и `staging` с более мягкими
ограничениями. Задеплоить приложение в namespace `production`.

Требования:
- Namespace `production` с ResourceQuota: максимум 8 CPU и 16Gi memory суммарно
- Namespace `staging` с ResourceQuota: максимум 2 CPU и 4Gi memory
- LimitRange в `production`: дефолтные requests если не указаны (250m CPU, 256Mi RAM)
- Deployment в namespace `production` (явно указан в metadata.namespace)
- Все ресурсы в отдельных yaml файлах

## Файлы для изменения

- `namespace-production.yaml` — Namespace + ResourceQuota + LimitRange
- `namespace-staging.yaml` — Namespace + ResourceQuota
- `deployment.yaml` — Deployment в namespace production

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex12_Namespaces/` без ошибок
- [ ] Два Namespace объявлены
- [ ] ResourceQuota для обоих namespace
- [ ] LimitRange в production с дефолтными requests
- [ ] Deployment имеет `metadata.namespace: production`

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 8: Namespaces](../../theory/KUBERNETES.md)
