# System Design — Roadmap

Применение паттернов многопоточности к реальным задачам backend-разработки.
Теория concurrency живёт в `modules/concurrency/`. Здесь — практика и смежные темы.

---

## Порядок прохождения

| Приоритет | Модуль | Частота на собесах |
|-----------|--------|--------------------|
| 1 | Pessimistic Locking in Practice | ★★★★★ |
| 2 | Optimistic Locking in Practice  | ★★★★★ |
| 3 | Database Theory                 | ★★★★★ |
| 4 | Lock-Free Data Structures       | ★★★★☆ |
| 5 | Rate Limiting & Scheduling      | ★★★★☆ |
| 6 | Distributed Systems & Microservices | ★★★☆☆ |
| 7 | Testing                         | ★★★☆☆ |

---

## Модуль 1: Pessimistic Locking in Practice

📖 Теория concurrency: [modules/concurrency/theory/LOCKS.md](../concurrency/theory/LOCKS.md)

- [ ] TOCTOU — check-then-act под единой блокировкой
- [ ] Гранулярные локи per-resource (ConcurrentHashMap + computeIfAbsent)
- [ ] Порядок захвата локов при нескольких ресурсах (deadlock prevention)

**Упражнение:**
- [ ] [reservations/ReservationService.java](src/main/java/by/pavel/reservations/ReservationService.java) — бронирование стола без race condition

**Тест:** `mvn test -pl . -Dtest=ReservationServiceTest`

---

## Модуль 2: Optimistic Locking in Practice

📖 Теория concurrency: [modules/concurrency/theory/PROBLEMS.md](../concurrency/theory/PROBLEMS.md)

- [ ] Версионирование записей (version field)
- [ ] Retry-логика при конфликте
- [ ] Нельзя смешивать optimistic + pessimistic в одном блоке
- [ ] Double-checked locking для идемпотентности

**Упражнение:**
- [ ] [bank/BankServiceImpl.java](src/main/java/by/pavel/bank/BankServiceImpl.java) — перевод средств с optimistic locking и idempotency

**Тест:** `mvn test -pl . -Dtest=BankServiceTest`

---

## Модуль 3: Lock-Free Data Structures

📖 Теория concurrency: [modules/concurrency/theory/ATOMIC_CAS.md](../concurrency/theory/ATOMIC_CAS.md)

- [ ] Thread-safe LRU Cache — ReadWriteLock vs ConcurrentHashMap
- [ ] Concurrent Order Book — атомарный snapshot, ConcurrentSkipListMap
- [ ] Балансировка чтений и записей в high-throughput структурах

**Упражнения:**
- [ ] [cache/LRUCache.java](src/main/java/by/pavel/cache/LRUCache.java) — LRU-кэш с TTL, thread-safe
- [ ] [orderbook/OrderBookService.java](src/main/java/by/pavel/orderbook/OrderBookService.java) — биржевой стакан с атомарным снимком

**Тесты:** `mvn test -pl . -Dtest="CacheTest,OrderBookTest"`

---

## Модуль 4: Rate Limiting & Scheduling

📖 Теория concurrency: [modules/concurrency/theory/EXECUTORS_FUTURES.md](../concurrency/theory/EXECUTORS_FUTURES.md)

- [ ] Token Bucket алгоритм — атомарные операции, время
- [ ] Task Scheduler — ScheduledExecutorService, приоритеты
- [ ] Корректное завершение (shutdown, await termination)

**Упражнения:**
- [ ] [ratelimiter/TokenBucketRateLimiter.java](src/main/java/by/pavel/ratelimiter/TokenBucketRateLimiter.java)
- [ ] [scheduler/SimpleTaskScheduler.java](src/main/java/by/pavel/scheduler/SimpleTaskScheduler.java)

**Тесты:** `mvn test -pl . -Dtest="RateLimiterTest,TaskSchedulerTest"`

---

## Модуль 5: Database Theory

📖 Теория: [theory/database_transactions.md](theory/database_transactions.md) | [theory/database_indexes.md](theory/database_indexes.md)

- [ ] Аномалии: dirty read, non-repeatable read, phantom read, lost update
- [ ] Уровни изоляции: READ UNCOMMITTED → SERIALIZABLE
- [ ] Оптимистическая блокировка на уровне БД (SELECT FOR UPDATE, version column)
- [ ] Индексы: B-Tree, Hash, покрывающие, когда индекс не помогает
- [ ] EXPLAIN ANALYZE — чтение плана запроса

---

## Модуль 6: Distributed Systems & Microservices

📖 Теория: [theory/distributed_systems.md](theory/distributed_systems.md) | [theory/microservice_patterns.md](theory/microservice_patterns.md)

- [ ] CAP теорема — CP vs AP системы
- [ ] Eventual consistency, strong consistency
- [ ] Паттерны: Saga, Outbox, Circuit Breaker, Bulkhead, Retry
- [ ] Idempotency в распределённых системах

---

## Модуль 7: Testing

📖 Теория: [theory/testing.md](theory/testing.md)

- [ ] Тестирование конкурентного кода — race condition воспроизведение
- [ ] CountDownLatch в тестах
- [ ] Stress-тесты и проверка инвариантов под нагрузкой

**Все тесты:** `mvn test`
