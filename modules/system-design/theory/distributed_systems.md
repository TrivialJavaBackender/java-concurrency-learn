# Distributed Systems

## Distributed Lock (Redis / Redisson)

In-memory `synchronized` не работает при нескольких инстансах. Нужен распределённый lock.

```java
RLock lock = redisson.getLock("reservation:table:" + tableId + ":date:" + date);
try {
    if (lock.tryLock(3, 5, TimeUnit.SECONDS)) { // ждём 3с, держим 5с
        // check + save
    } else {
        throw new NoAvailableTimeSlotException("System busy, retry");
    }
} finally {
    lock.unlock();
}
```

Гранулярность ключа `table:{id}:date:{date}` — разные столы не блокируют друг друга.

**Проблема Redlock:** при network partition два процесса могут одновременно считать себя владельцами лока. Для большинства задач допустимо при наличии database constraint как страховки.

---

## Idempotency Key

Клиент генерирует уникальный ключ (UUID) и передаёт с каждым запросом. Сервер хранит `(key → result)` в Redis с TTL.

```
Client → POST /reservations {idempotencyKey: "uuid-123", ...}
Server: ключ есть в Redis? → вернуть сохранённый результат
Server: ключа нет? → создать резервацию, сохранить результат в Redis с TTL
```

Защищает от дублей при retry после network timeout — клиент не знает, дошёл ли запрос.

---

## Outbox Pattern

**Проблема:** INSERT в БД и публикация события в Kafka — две операции. Сбой между ними = потеря события или дубль.

```
Без outbox:
INSERT reservation; -- успех
publish to Kafka;   -- сбой → событие потеряно

С outbox:
BEGIN;
INSERT INTO reservations ...;
INSERT INTO outbox_events (type='RESERVATION_CREATED', payload=...); -- в одной транзакции
COMMIT; -- атомарно

-- Отдельный процесс читает outbox и публикует в Kafka (at-least-once)
```

Outbox + идемпотентный consumer = exactly-once семантика.

---

## CQRS (Command Query Responsibility Segregation)

Разделение на write-side (команды) и read-side (запросы).

```
Write side: POST /reservations → PostgreSQL (строгая консистентность, блокировки)
Read side:  GET /availability  → Redis cache (денормализованный, быстро, eventual consistency)
```

Запись инвалидирует или обновляет кэш. Чтение никогда не идёт в основную БД.

---

## Clock Skew

`LocalDate.now()` на разных инстансах может вернуть разное значение из-за рассинхронизации часов или разных таймзон.

**Решение:** дата всегда приходит от клиента, сервер её не генерирует. Или централизованное время через NTP + monotonic clock.

---

## CAP Theorem

Распределённая система может гарантировать только 2 из 3:
- **C**onsistency — все узлы видят одинаковые данные
- **A**vailability — каждый запрос получает ответ
- **P**artition Tolerance — система работает при сетевом разрыве

При network partition нужно выбирать между CP (жертвуем доступностью) и AP (жертвуем консистентностью). P отказаться нельзя — сеть всегда может упасть.

**Практика:** большинство NoSQL баз — AP (eventual consistency). PostgreSQL в кластере — CP.

---

## Eventual Consistency vs Strong Consistency

**Strong consistency** (PostgreSQL, ZooKeeper): после записи все читают актуальные данные. Цена — latency и availability.

**Eventual consistency** (Cassandra, DynamoDB, Redis cache): данные рано или поздно синхронизируются. Читающий может увидеть старое значение. Цена — сложность обработки конфликтов.

**Где допустима eventual consistency:** availability-виджет, лента новостей, счётчики просмотров.

**Где нужна strong consistency:** баланс счёта, инвентарь, резервации.
