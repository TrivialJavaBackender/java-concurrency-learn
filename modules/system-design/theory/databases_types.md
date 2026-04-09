# Типы баз данных

## Реляционные (RDBMS)

Данные в таблицах, связи через foreign keys, строгая схема, ACID-транзакции.

| СУБД | Особенности |
|------|-------------|
| PostgreSQL | Расширяемый, JSONB, PostGIS, MVCC, открытый |
| MySQL/MariaDB | Простота, широкое распространение, InnoDB |
| Oracle | Enterprise, RAC, PL/SQL |
| SQL Server | Windows-экосистема, хорошая интеграция с .NET |

**PostgreSQL** — объектно-реляционная СУБД: поддерживает наследование таблиц, пользовательские типы, операторы и индексные методы. Тип БД — **ORDBMS (Object-Relational)**.

---

## Нереляционные (NoSQL)

### Key-Value

Простейшая модель: ключ → значение. Максимальная скорость для простых операций.

| СУБД | Особенности |
|------|-------------|
| **Redis** | In-memory, структуры данных, pub/sub, TTL |
| DynamoDB | Managed, бесконечное масштабирование, PAY-per-request |
| Riak | Distributed, AP-система |

**Когда:** сессии, кэш, rate limiting, real-time лидерборды.

### Document

Документы (JSON/BSON) без жёсткой схемы. Документ = самодостаточная единица данных.

| СУБД | Особенности |
|------|-------------|
| **MongoDB** | BSON, гибкая схема, горизонтальное шардирование |
| CouchDB | HTTP API, eventually consistent |
| Firestore | Managed, real-time sync |

**Когда:** CMS, каталоги товаров, пользовательские профили с разной структурой.

### Wide-Column (Column Family)

Строки с произвольным числом колонок, сгруппированных в "families". Хорош для разреженных данных и write-heavy нагрузки.

| СУБД | Особенности |
|------|-------------|
| **Cassandra** | AP, линейное масштабирование, нет joins |
| HBase | На Hadoop, CP |
| ScyllaDB | C++ Cassandra-совместимая, меньше latency |

**Когда:** IoT-данные, временные ряды, очень высокий write throughput.

### Graph

Узлы (vertices) и рёбра (edges) с атрибутами. Эффективны для обходов связей.

| СУБД | Особенности |
|------|-------------|
| **Neo4j** | Cypher query language, ACID |
| Amazon Neptune | Managed, RDF и Property Graph |
| ArangoDB | Multi-model: document + graph |

**Когда:** социальные сети, рекомендательные системы, fraud detection.

### Time-Series

Оптимизированы для данных с временной меткой — компрессия, быстрые range-запросы.

| СУБД | Особенности |
|------|-------------|
| **InfluxDB** | Push-based, Flux query language |
| TimescaleDB | PostgreSQL-расширение, SQL |
| Prometheus | Pull-based, PromQL, retention |

**Когда:** метрики, мониторинг, финансовые тики, IoT-сенсоры.

### Search Engines

Инвертированные индексы для полнотекстового поиска, фасетный поиск, scoring.

| СУБД | Особенности |
|------|-------------|
| **Elasticsearch** | Distributed, REST API, Kibana |
| OpenSearch | Open source форк ES |
| Apache Solr | Старше, Lucene-based |

**Когда:** поиск по сайту, логи (ELK stack), аналитика текста.

---

## OLTP vs OLAP vs HTAP

| | OLTP | OLAP | HTAP |
|---|---|---|---|
| Нагрузка | Много коротких транзакций | Долгие аналитические запросы | Оба |
| Оптимизация | Низкая latency, high throughput | Высокая пропускная способность | — |
| Данные | Строки | Колонки | Смешанно |
| Пример | PostgreSQL, MySQL | ClickHouse, BigQuery, Redshift | TiDB, SingleStore |

---

## Колоночные БД — преимущества

В строковых БД строка хранится физически последовательно. В колоночных — каждая колонка отдельно.

```
Строковая:  [id=1, name="Alice", age=30, salary=100k] [id=2, name="Bob", age=25, salary=80k]
Колоночная: [id: 1,2,3...] [name: Alice,Bob,...] [age: 30,25,...] [salary: 100k,80k,...]
```

**Преимущества для аналитики:**
- `SELECT AVG(salary)` читает только колонку `salary`, остальные пропускает → в 10-100x меньше I/O
- Колонка однородная → лучше сжимается (RLE, dictionary encoding): числа одного диапазона, строки из словаря
- Векторизованное выполнение: CPU обрабатывает батчи значений одного типа, использует SIMD

**Недостатки:**
- INSERT/UPDATE медленнее — нужно обновить каждую колонку отдельно
- Плохо для transactional workload (OLTP)

**Примеры:** ClickHouse, Apache Parquet (формат), BigQuery, Redshift, DuckDB.

---

## Redis — структуры данных

| Структура | Команды | Применение |
|-----------|---------|------------|
| **String** | `SET`, `GET`, `INCR`, `EXPIRE` | Кэш, счётчики, rate limiting |
| **Hash** | `HSET`, `HGET`, `HGETALL` | Объекты/профили пользователей |
| **List** | `LPUSH`, `RPUSH`, `LRANGE`, `BLPOP` | Очереди, стеки, recent items |
| **Set** | `SADD`, `SMEMBERS`, `SINTER`, `SUNION` | Уникальные элементы, теги |
| **Sorted Set (ZSet)** | `ZADD`, `ZRANGE`, `ZRANK`, `ZRANGEBYSCORE` | Лидерборды, очереди с приоритетом |
| **Bitmap** | `SETBIT`, `BITCOUNT`, `BITOP` | Флаги активности, DAU tracking |
| **HyperLogLog** | `PFADD`, `PFCOUNT` | Приблизительный count distinct |
| **Stream** | `XADD`, `XREAD`, `XGROUP` | Event streaming, замена Kafka для простых случаев |

**Практические паттерны:**

```
# Rate limiting — sliding window с Sorted Set
ZADD ratelimit:user:123 <now_ms> <request_id>
ZREMRANGEBYSCORE ratelimit:user:123 0 <now_ms - window_ms>
count = ZCARD ratelimit:user:123
if count > limit: reject

# Session store
SET session:<token> <json_data> EX 3600  -- TTL 1 час

# Distributed lock (простой)
SET lock:resource1 <uuid> NX EX 30  -- NX: только если нет, EX: TTL
# NX гарантирует атомарность set-if-not-exists

# Leaderboard
ZADD leaderboard <score> <user_id>
ZREVRANGE leaderboard 0 9 WITHSCORES  -- топ 10
ZRANK leaderboard <user_id>           -- место пользователя

# Pub/Sub
SUBSCRIBE channel-name
PUBLISH channel-name "message"
```

**Redis vs Memcached:**
- Redis: персистентность (RDB/AOF), структуры данных, Lua скрипты, кластер, pub/sub
- Memcached: только строки, нет персистентности, проще, чуть быстрее для pure cache

---

## ORM паттерны доступа к данным

### Active Record

Объект содержит и данные, и логику доступа к БД. Строка таблицы = объект класса.

```java
// Active Record: User.find(), user.save()
User user = User.find(id);
user.setEmail("new@email.com");
user.save(); // SQL внутри объекта
```

**Плюсы:** просто, мало кода. **Минусы:** смешивает бизнес-логику и доступ к данным.
**Пример:** Ruby on Rails ActiveRecord.

### Data Mapper

Объекты домена ничего не знают о БД. Отдельный маппер (repository) отвечает за преобразование.

```java
// Data Mapper: repository отдельно от сущности
User user = userRepository.findById(id);
user.setEmail("new@email.com");
userRepository.save(user); // маппер знает как сохранить User
// User ничего не знает о SQL
```

**Плюсы:** чистая архитектура, Unit of Work, тестируемость. **Минусы:** больше кода.
**Пример:** Hibernate (JPA), Doctrine (PHP).

### Identity Map

Кэш загруженных объектов в рамках одной Unit of Work (сессии). Гарантирует, что каждая строка БД = один объект в памяти.

```
userRepo.findById(1) → SELECT ... → User@0x1a2b, кладёт в map {1 → User@0x1a2b}
userRepo.findById(1) → map hit! → возвращает тот же User@0x1a2b, SQL не идёт
```

**Зачем:** предотвращает дублирование объектов, консистентность графа объектов, экономит запросы.

### Unit of Work

Отслеживает все изменения в объектах за одну "работу" (request/transaction). В конце сбрасывает изменения в БД одним батчем.

```java
// Hibernate Session = Unit of Work + Identity Map
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

User user = session.get(User.class, 1L); // SELECT, добавляет в Identity Map
user.setEmail("new@email.com");          // отмечает как "dirty"
// никаких SQL!

tx.commit(); // Unit of Work: flush → UPDATE users SET email=? WHERE id=1
session.close();
```

**Как работает в Hibernate:**
1. `get()` → SELECT + в Identity Map
2. Изменение поля → объект помечается dirty (через dirty checking при flush)
3. `flush()` / `commit()` → INSERT/UPDATE/DELETE для всех dirty объектов

**Lazy Loading:** `@OneToMany(fetch=LAZY)` — коллекция не загружается до первого обращения. Проблема N+1: загрузить 100 заказов → 100 запросов за пользователями. Решение: `JOIN FETCH` или `@EntityGraph`.
