# Ex09: ConfigMap и Secret

**Модуль:** 2 — Kubernetes
**Сложность:** ★★★☆☆
**Тема:** ConfigMap, Secret, env variables, volume mount

## Контекст

Приложение требует конфигурацию: URL базы данных, уровень логирования, имя окружения —
это нечувствительные данные. Пароль базы данных и JWT секрет — чувствительные.
Хранить всё в Deployment yaml — плохая практика и нарушение безопасности.

## Задача

Вынести конфигурацию в ConfigMap и Secret, подключить их к Deployment двумя способами:
через env variables и через volume mount.

Требования:
- `ConfigMap` содержит: `APP_ENV`, `LOG_LEVEL`, `DB_HOST`, `DB_PORT`
- `Secret` содержит: `DB_PASSWORD`, `JWT_SECRET` (значения должны быть в base64)
- Все поля ConfigMap подключены как env variables через `envFrom`
- Только `DB_PASSWORD` из Secret подключён как env variable через `valueFrom`
- ConfigMap также монтируется как файл `/app/config/app.properties` через volume
- Deployment обновлён для использования ConfigMap и Secret

## Файлы для изменения

- `configmap.yaml` — создать
- `secret.yaml` — создать
- `deployment.yaml` — создать (использует configmap и secret)

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex09_ConfigMapsSecrets/` без ошибок
- [ ] Secret значения закодированы в base64 (не plain text)
- [ ] `envFrom.configMapRef` присутствует в Deployment
- [ ] `valueFrom.secretKeyRef` для DB_PASSWORD
- [ ] Volume mount для ConfigMap как файла

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 5: ConfigMap и Secret](../../theory/KUBERNETES.md)
