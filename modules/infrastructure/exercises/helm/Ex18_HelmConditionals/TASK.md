# Ex18: Условная шаблонизация и _helpers.tpl

**Модуль:** 3 — Helm
**Сложность:** ★★★★★
**Тема:** if/else, range, with, _helpers.tpl, named templates, quote/nindent

## Контекст

Chart растёт: в deployment.yaml дублируются одни и те же блоки labels. Нужно вынести
общую логику в `_helpers.tpl`. Также нужно поддержать опциональные features: PodDisruptionBudget
(только если replicas > 1) и ServiceAccount (с флагом enabled).

## Задача

Рефакторинг chart с выносом общей логики в helpers и добавлением условных ресурсов.

Требования:
- `_helpers.tpl` содержит: `fullname`, `labels`, `selectorLabels`, `image` (с digest опционально)
- `PodDisruptionBudget` создаётся только если `replicaCount > 1` (через `if gt`)
- `ServiceAccount` создаётся только если `serviceAccount.create: true`
- Аннотации ServiceAccount настраиваются через `range .Values.serviceAccount.annotations`
- Все строковые values из values.yaml обёрнуты в `| quote`
- `| nindent N` используется для правильного отступа при `include`

## Файлы для изменения

- `myapp/Chart.yaml`
- `myapp/values.yaml`
- `myapp/templates/_helpers.tpl` — расширить
- `myapp/templates/deployment.yaml`
- `myapp/templates/pdb.yaml` — условный PodDisruptionBudget
- `myapp/templates/serviceaccount.yaml` — условный ServiceAccount

## Проверка

Упражнение считается выполненным, когда:
- [ ] `helm lint myapp/` без ошибок и warning
- [ ] `helm template myapp myapp/ --set replicaCount=1` — PDB НЕ создаётся
- [ ] `helm template myapp myapp/ --set replicaCount=3` — PDB создаётся
- [ ] `helm template myapp myapp/ --set serviceAccount.create=false` — SA не создаётся
- [ ] `_helpers.tpl` используется в deployment через `include`

## Полезные ссылки

- [theory/HELM.md — раздел 6: Условная шаблонизация](../../theory/HELM.md)
