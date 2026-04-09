# Interview Prep

Площадка для подготовки к техническим собеседованиям по backend-разработке.
Три независимых модуля с теорией, упражнениями и вопросами для собеседований.

## Модули

| Модуль | Тема | Стек |
|--------|------|------|
| [concurrency](modules/concurrency/) | Java Concurrency — потоки, локи, атомики, executor, виртуальные потоки | Kotlin, JUC, Maven |
| [system-design](modules/system-design/) | System Design — locking стратегии, cache, rate limiter, order book, scheduler | Java, JUnit, Maven |
| [infrastructure](modules/infrastructure/) | Docker, Kubernetes, Helm, Observability, Logging, Metrics | YAML, PromQL, Spring Boot |

## Структура

```
modules/
├── concurrency/
│   ├── theory/          # 8 файлов: потоки, локи, CAS, коллекции, executors, ...
│   ├── src/exercises/   # 18 Kotlin упражнений
│   ├── ROADMAP.md
│   ├── PROGRESS.md
│   └── INTERVIEW_QUESTIONS.md   # 32 вопроса
│
├── system-design/
│   ├── theory/          # DB транзакции, индексы, distributed systems, ...
│   ├── src/             # Java: reservations, bank, cache, orderbook, scheduler, ratelimiter
│   ├── ROADMAP.md
│   ├── PROGRESS.md
│   └── INTERVIEW_QUESTIONS.md   # 25 вопросов
│
└── infrastructure/
    ├── theory/          # Docker, K8s, Helm, Observability, Logging, Metrics
    ├── exercises/       # docker/, kubernetes/, helm/, logging/, metrics/
    ├── ROADMAP.md
    ├── PROGRESS.md
    └── INTERVIEW_QUESTIONS.md   # 36 вопросов
```

## Команды Claude

| Команда | Действие |
|---------|----------|
| `прогресс` | Показать статус модулей |
| `следующий` | Следующая незавершённая тема |
| `квиз` | 5 случайных вопросов для собеседования |
| `проверь concurrency Ex01` | Code review + запуск упражнения |
| `проверь system-design ReservationService` | Code review + тесты |
| `/new-module <name>` | Создать новый модуль |
