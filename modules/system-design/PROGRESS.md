# Progress Tracker — System Design

## Статус модулей

| Модуль | Статус | Дата начала | Дата завершения |
|--------|--------|-------------|-----------------|
| 1. Pessimistic Locking in Practice | ⬜ не начат | — | — |
| 2. Optimistic Locking in Practice  | ⬜ не начат | — | — |
| 3. Lock-Free Data Structures       | ⬜ не начат | — | — |
| 4. Rate Limiting & Scheduling      | ⬜ не начат | — | — |
| 5. Database Theory                 | ⬜ не начат | — | — |
| 6. Distributed Systems & Microservices | ⬜ не начат | — | — |
| 7. Testing                         | ⬜ не начат | — | — |

## Упражнения (Java)

| Пакет | Класс | Тема | Статус |
|-------|-------|------|--------|
| reservations | ReservationService | Pessimistic locking, TOCTOU | ⬜ |
| bank | BankServiceImpl | Optimistic locking, deadlock prevention | ⬜ |
| cache | LRUCache | Thread-safe cache, ReadWriteLock | ⬜ |
| orderbook | OrderBookService | Concurrent order book, atomic snapshot | ⬜ |
| scheduler | SimpleTaskScheduler | Scheduled executor, concurrent task queue | ⬜ |
| ratelimiter | TokenBucketRateLimiter | Token bucket, atomic operations | ⬜ |

## Теория

| Файл | Тема | Изучено |
|------|------|---------|
| [theory/database_transactions.md](theory/database_transactions.md) | Транзакции, изоляция, аномалии | ⬜ |
| [theory/database_indexes.md](theory/database_indexes.md) | Индексы, B-Tree, покрывающие | ⬜ |
| [theory/distributed_systems.md](theory/distributed_systems.md) | CAP, консистентность, репликация | ⬜ |
| [theory/microservice_patterns.md](theory/microservice_patterns.md) | Паттерны: Saga, Circuit Breaker и др. | ⬜ |
| [theory/testing.md](theory/testing.md) | Тестирование многопоточного кода | ⬜ |
| [INTERVIEW_QUESTIONS.md](INTERVIEW_QUESTIONS.md) | Вопросы по system design | ⬜ |

---
Легенда: ⬜ не начато | 🔄 в процессе | ✅ завершено

> **Важно:** Теория по concurrency (локи, атомики, executor и т.д.) находится в модуле `modules/concurrency/`.
