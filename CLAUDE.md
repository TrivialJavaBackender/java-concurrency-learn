# Interview Prep — Multi-Module

## Структура

Репо содержит три независимых модуля в папке `modules/`:

| Модуль | Путь | Тема |
|--------|------|------|
| `concurrency` | `modules/concurrency/` | Java Concurrency (Kotlin + JUC) |
| `system-design` | `modules/system-design/` | System Design (Java, применение паттернов) |
| `infrastructure` | `modules/infrastructure/` | Docker, K8s, Helm, Observability, Logging, Metrics |
| `spring-frameworks` | `modules/spring-frameworks/` | Spring Core/Boot/MVC/Data/Security/Cloud |

## Правило теории — NO OVERLAP

**Каждая тема принадлежит ровно одному модулю:**

| Тема | Модуль |
|------|--------|
| Потоки, synchronized, volatile, JMM, локи, атомики, concurrent collections, executors, synchronizers, virtual threads | `concurrency` |
| Database transactions/indexes, distributed systems, CAP, microservice patterns (Saga, Outbox, Circuit Breaker), testing | `system-design` |
| Docker, Kubernetes, Helm, Observability, Logging, Metrics | `infrastructure` |
| Spring Core/DI/IoC, Spring Boot/Starters/Auto-Configuration, Spring MVC/REST, Spring Data JPA/Hibernate (включая все уровни кэша), Spring Security, Spring Cloud | `spring-frameworks` |

**При добавлении теории** — всегда проверяй, не принадлежит ли тема уже другому модулю.
Если перекрытие есть — теория должна остаться в «исходном» модуле, а в новом — дать ссылку.

## Выбор активного модуля

Пользователь может сказать "переключись на concurrency/system-design/infra" или уточнить команду:
- "проверь concurrency Ex01" — работаем в modules/concurrency/
- "проверь system-design ReservationService" — работаем в modules/system-design/
- "следующий по infra" — модуль infrastructure

Если модуль не указан, читай контекст (о чём идёт речь) или уточни у пользователя.

---

## Команды

### Прогресс
Когда пользователь говорит "прогресс", "статус" или "как дела":
- Прочитай PROGRESS.md активного модуля и покажи статус.
- Если модуль не ясен — покажи статус всех трёх.

### Проверка упражнения

**Для `concurrency` (Ex01–Ex18, Kotlin):**
1. Прочитай `modules/concurrency/src/main/kotlin/exercises/ExXX_Name.kt`
2. Проверь, что TODO заменены на реализацию
3. Скомпилируй: `cd modules/concurrency && mvn compile -q`
4. Запусти: `cd modules/concurrency && mvn exec:java -Dexec.mainClass="exercises.ExXX_NameKt" -q`
5. Проведи детальный code review — race condition, неверные локи, утечки, логика wait/notify
6. Помечай ✅ в `modules/concurrency/PROGRESS.md` только без серьёзных замечаний

**Для `system-design` (Java-классы):**
1. Прочитай нужный класс в `modules/system-design/src/main/java/by/pavel/`
2. Проверь реализацию
3. Скомпилируй и запусти тесты: `cd modules/system-design && mvn test -Dtest=ClassName`
4. Проведи code review: thread safety, locking strategy, корректность
5. Помечай ✅ в `modules/system-design/PROGRESS.md` только без серьёзных замечаний

**Для `infrastructure` (конфиги, YAML, PromQL):**
1. Прочитай файлы упражнения в `modules/infrastructure/exercises/`
2. Проверь корректность конфигурации
3. При необходимости запусти `docker-compose up` для проверки
4. Помечай ✅ в `modules/infrastructure/PROGRESS.md`

**Общие правила review:**
- Найди все race condition, неправильное использование локов, утечки, неверную логику
- Укажи на субоптимальные решения и объясни почему они плохи
- Проверь соответствие условию задачи
- Не пропускай замечания — лучше написать лишнее, чем упустить баг

### Следующий модуль
Когда пользователь говорит "следующий" или "next":
1. Определи активный модуль
2. Найди первый незавершённый модуль в соответствующем PROGRESS.md
3. Прочитай файл теории
4. Покажи краткое содержание и ключевые вопросы
5. Предложи начать с первого невыполненного упражнения

### Квиз
Когда пользователь говорит "квиз" или "quiz":
- Задай 5 случайных вопросов из INTERVIEW_QUESTIONS.md активного модуля
- Жди ответа, оцени

### Добавить/обновить упражнение
- Упражнение должно быть **challenging**, без прямых подсказок в коде
- Никогда не давай подсказки по реализации, если пользователь явно не попросил
- Когда пользователь говорит "начать" — сообщи что файл готов, жди

---

## Сборка

### concurrency (Kotlin)
```bash
cd modules/concurrency
mvn compile
mvn exec:java -Dexec.mainClass="exercises.Ex01_ThreadBasicsKt"
```

### system-design (Java)
```bash
cd modules/system-design
mvn compile
mvn test
mvn test -Dtest=BankServiceTest
```

### infrastructure (Spring Boot)
```bash
cd modules/infrastructure
mvn compile
mvn spring-boot:run
```

---

## Структура теории по модулям

### concurrency (`modules/concurrency/theory/`)
- THREADS_BASICS.md — Модуль 1
- LOCKS.md — Модуль 2
- ATOMIC_CAS.md — Модуль 3
- CONCURRENT_COLLECTIONS.md — Модуль 4
- EXECUTORS_FUTURES.md — Модуль 5
- SYNCHRONIZERS.md — Модуль 6
- PROBLEMS.md — Модуль 7
- VIRTUAL_THREADS.md — Модуль 8

### system-design (`modules/system-design/theory/`)
- database_transactions.md
- database_indexes.md
- distributed_systems.md
- microservice_patterns.md
- testing.md

### spring-frameworks (`modules/spring-frameworks/theory/`)
- SPRING_CORE_DI.md — IoC, DI, Bean Scopes, AOP, GoF паттерны
- SPRING_BOOT.md — Starters, Auto-Configuration, Actuator, Profiles
- SPRING_MVC_REST.md — DispatcherServlet, REST, Validation, Filters
- SPRING_DATA_JPA.md — JPA, Hibernate L1/L2/Query кэши, N+1, @Transactional
- SPRING_SECURITY.md — Filter Chain, JWT, Method Security, CSRF
- SPRING_CLOUD.md — Config, Eureka, Gateway, Feign, Circuit Breaker

### infrastructure (`modules/infrastructure/theory/`)
- DOCKER.md, KUBERNETES.md, HELM.md
- OBSERVABILITY.md, LOGGING.md, METRICS.md

---

## Создать новый модуль

Используй команду `/new-module` (slash command).
