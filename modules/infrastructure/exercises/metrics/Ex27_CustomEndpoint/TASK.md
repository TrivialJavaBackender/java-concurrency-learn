# Ex27: Custom HealthIndicator

**Модуль:** 6 — Metrics
**Сложность:** ★★★☆☆
**Тема:** HealthIndicator, Actuator health, кастомные health details

## Контекст

`/actuator/health` возвращает только базовую информацию. При проблемах с внешними
зависимостями (payment gateway, SMS provider) нужно видеть детали: недоступен ли
сервис, какова latency последнего вызова, когда последний успешный вызов.

## Задача

Реализовать `PaymentGatewayHealthIndicator` — кастомный health indicator.

Требования:
- Реализует `HealthIndicator` интерфейс
- Проверяет доступность payment gateway (имитация через HTTP ping или флаг)
- В `Health.up()` включает детали: `latencyMs`, `lastSuccessfulCheck`
- В `Health.down()` включает: `error`, `lastSuccessfulCheck`
- Зарегистрирован как Spring Bean
- Доступен в `/actuator/health/paymentGateway`

## Файлы для изменения

- `PaymentGatewayHealthIndicator.java` — написать с нуля

## Рабочий процесс

Реализуй `PaymentGatewayHealthIndicator.java` в этой директории, затем скопируй в проект и скомпилируй:
```bash
cp exercises/metrics/Ex27_CustomEndpoint/PaymentGatewayHealthIndicator.java src/main/java/by/pavel/metrics/
mvn compile -q
```

## Проверка

Упражнение считается выполненным, когда:
- [ ] `mvn compile -q` проходит без ошибок
- [ ] Реализует `HealthIndicator` (не `AbstractHealthIndicator`)
- [ ] `Health.up().withDetail("latencyMs", ...).build()`
- [ ] `Health.down().withDetail("error", ...).build()`
- [ ] `@Component` с именем `paymentGateway` (для URL)

## Полезные ссылки

- [theory/METRICS.md — раздел 3: Micrometer](../../theory/METRICS.md)
