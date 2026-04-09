# Ex15: Базовый Helm Chart

**Модуль:** 3 — Helm
**Сложность:** ★★★☆☆
**Тема:** Chart структура, Chart.yaml, values.yaml, шаблоны

## Контекст

Команда устала копировать Kubernetes YAML файлы для каждого окружения и вручную менять
image tag, replicas и resource limits. Нужно упаковать приложение в Helm chart.

## Задача

Создать Helm chart с нуля для Spring Boot приложения.

Требования:
- `Chart.yaml` заполнен корректно: apiVersion v2, имя, описание, версии
- `values.yaml` параметризует: image (repo + tag), replicas, resources, service type
- `templates/deployment.yaml` использует values через `{{ .Values.* }}`
- `templates/service.yaml` тип и порт из values
- `templates/_helpers.tpl` содержит `fullname` и `labels` named templates
- Все ресурсы используют стандартные Kubernetes labels (app.kubernetes.io/*)

## Файлы для изменения

- `myapp/Chart.yaml`
- `myapp/values.yaml`
- `myapp/templates/deployment.yaml`
- `myapp/templates/service.yaml`
- `myapp/templates/_helpers.tpl`

## Проверка

Упражнение считается выполненным, когда:
- [ ] `helm lint exercises/helm/Ex15_BasicChart/myapp/` без ошибок
- [ ] `helm template myapp exercises/helm/Ex15_BasicChart/myapp/ | kubectl apply --dry-run=client -f -` без ошибок
- [ ] `values.yaml` содержит image.repository, image.tag, replicaCount, resources
- [ ] `_helpers.tpl` определяет минимум 2 named template
- [ ] Метки `app.kubernetes.io/name` и `app.kubernetes.io/instance` присутствуют

## Полезные ссылки

- [theory/HELM.md — раздел 2: Анатомия Chart](../../theory/HELM.md)
- [theory/HELM.md — раздел 3: Шаблонизация](../../theory/HELM.md)
