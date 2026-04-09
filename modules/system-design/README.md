# System Design — Interview Prep

Применение Java Concurrency к реальным задачам: бронирование, банковские переводы, биржевой стакан, rate limiting, scheduling.

## Структура

```
├── ROADMAP.md                          # Порядок прохождения (7 модулей)
├── PROGRESS.md                         # Трекер прогресса
├── INTERVIEW_QUESTIONS.md              # 25 вопросов по system design
│
├── theory/
│   ├── database_transactions.md        # Транзакции, изоляция, аномалии
│   ├── database_indexes.md             # B-Tree, покрывающие индексы, EXPLAIN
│   ├── distributed_systems.md          # CAP, eventual consistency, репликация
│   ├── microservice_patterns.md        # Saga, Outbox, Circuit Breaker, Bulkhead
│   └── testing.md                      # Тестирование конкурентного кода
│
└── src/
    ├── main/java/by/pavel/
    │   ├── reservations/               # Pessimistic locking, TOCTOU
    │   ├── bank/                       # Optimistic locking, idempotency, deadlock prevention
    │   ├── cache/                      # Thread-safe LRU cache
    │   ├── orderbook/                  # Concurrent order book, atomic snapshot
    │   ├── scheduler/                  # Task scheduler, ScheduledExecutorService
    │   └── ratelimiter/               # Token bucket rate limiter
    └── test/java/by/pavel/            # JUnit 5 тесты для каждого пакета
```

## Запуск

```bash
# Компиляция
mvn compile

# Все тесты
mvn test

# Один тест
mvn test -Dtest=BankServiceTest
```

> **Теория concurrency** (локи, атомики, executor и т.д.) находится в [`modules/concurrency/`](../concurrency/).

## Стек

- Java 21
- Maven 3.9
- JUnit 5
