# Ex17: Helm Hooks

**Модуль:** 3 — Helm
**Сложность:** ★★★★☆
**Тема:** pre-install hook, post-upgrade hook, Job, hook-delete-policy

## Контекст

При деплое новой версии приложения нужно сначала выполнить DB миграцию. Если миграция
провалилась — новая версия не должна подняться. После успешного upgrade нужно запустить
smoke-тест, проверяющий что приложение работает.

## Задача

Добавить в chart два Helm hook: Job для миграции и Job для smoke-теста.

Требования:
- `pre-upgrade-migration` Job с аннотацией `helm.sh/hook: pre-install,pre-upgrade`
- Если migration Job упал — upgrade не продолжается
- `post-upgrade-test` Job с аннотацией `helm.sh/hook: post-install,post-upgrade`
- Оба Job имеют `hook-delete-policy: before-hook-creation` (чтобы не накапливались)
- `restartPolicy: Never` для обоих Job
- Migration Job использует тот же image что и основное приложение

## Файлы для изменения

- `myapp/Chart.yaml`
- `myapp/values.yaml`
- `myapp/templates/migration-job.yaml`
- `myapp/templates/smoke-test-job.yaml`
- `myapp/templates/deployment.yaml` (базовый)

## Проверка

Упражнение считается выполненным, когда:
- [ ] `helm lint myapp/` без ошибок
- [ ] Migration Job имеет аннотацию `helm.sh/hook: pre-install,pre-upgrade`
- [ ] Smoke test Job имеет аннотацию `helm.sh/hook: post-install,post-upgrade`
- [ ] `hook-delete-policy` задан для обоих
- [ ] `restartPolicy: Never` (не OnFailure — иначе hook не завершится для Helm)

## Полезные ссылки

- [theory/HELM.md — раздел 5: Helm Hooks](../../theory/HELM.md)
