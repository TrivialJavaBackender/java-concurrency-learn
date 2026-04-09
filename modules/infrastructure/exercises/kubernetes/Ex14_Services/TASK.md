# Ex14: Kubernetes Services

**Модуль:** 2 — Kubernetes
**Сложность:** ★★★☆☆
**Тема:** ClusterIP, NodePort, Service DNS, selector

## Контекст

Архитектура состоит из двух сервисов: `order-service` (внутренний API) и `api-gateway`
(публичный endpoint). `order-service` должен быть доступен только внутри кластера.
`api-gateway` должен быть доступен снаружи. Оба должны находить друг друга по DNS имени.

## Задача

Создать два Deployment и два Service с правильными типами и настройками.

Требования:
- `order-service`: Deployment + ClusterIP Service на порту 80 → 8080
- `api-gateway`: Deployment + NodePort Service (порт 30080 на ноде)
- `api-gateway` должен обращаться к `order-service` по DNS имени `order-service`
- Labels и selectors настроены корректно для каждой пары
- Оба Deployment с resource requests/limits

## Файлы для изменения

- `order-service-deployment.yaml`
- `order-service-clusterip.yaml`
- `api-gateway-deployment.yaml`
- `api-gateway-nodeport.yaml`

## Проверка

Упражнение считается выполненным, когда:
- [ ] `kubectl apply --dry-run=client -f exercises/kubernetes/Ex14_Services/` без ошибок
- [ ] `order-service` Service типа ClusterIP
- [ ] `api-gateway` Service типа NodePort с `nodePort: 30080`
- [ ] Selectors соответствуют labels в Pod templates
- [ ] В env переменных api-gateway есть `ORDER_SERVICE_URL: http://order-service`

## Полезные ссылки

- [theory/KUBERNETES.md — раздел 4: Service](../../theory/KUBERNETES.md)
