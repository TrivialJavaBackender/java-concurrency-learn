# Ex16: Values и шаблонизация

**Модуль:** 3 — Helm
**Сложность:** ★★★★☆
**Тема:** values.yaml, --set, --values, range, env vars

## Контекст

Chart из Ex15 работает, но нужно поддержать несколько окружений. В production нужно
больше реплик, другие resource limits, другой image tag и дополнительные env переменные.
В staging — дефолтные значения. Переопределять вручную каждый раз неудобно.

## Задача

Расширить chart для поддержки нескольких окружений через values файлы.

Требования:
- `values.yaml` — дефолтные значения (staging-like)
- `values-production.yaml` — overrides для production (больше replicas, resources, тег)
- Env переменные в Deployment настраиваются через `values.env` (список name/value)
- Ingress ресурс с флагом `enabled` (если false — ресурс не создаётся)
- `values-production.yaml` включает ingress с host
- `helm template ... -f values-production.yaml` генерирует корректный Deployment и Ingress

## Файлы для изменения

- `myapp/Chart.yaml`
- `myapp/values.yaml` — дефолты
- `myapp/values-production.yaml` — production overrides
- `myapp/templates/deployment.yaml` — с range для env
- `myapp/templates/ingress.yaml` — условный ресурс

## Проверка

Упражнение считается выполненным, когда:
- [ ] `helm lint myapp/` без ошибок
- [ ] `helm template myapp myapp/` — ingress НЕ создаётся (disabled по умолчанию)
- [ ] `helm template myapp myapp/ -f myapp/values-production.yaml` — ingress создаётся
- [ ] `range .Values.env` используется в deployment template
- [ ] `--set image.tag=2.0.0` переопределяет тег

## Полезные ссылки

- [theory/HELM.md — раздел 3: Шаблонизация](../../theory/HELM.md)
- [theory/HELM.md — раздел 6: Условная шаблонизация](../../theory/HELM.md)
