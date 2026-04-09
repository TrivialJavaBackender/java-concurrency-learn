# Indexes in PostgreSQL

## Почему нет кластерных индексов

В SQL Server / MySQL InnoDB кластерный индекс — физическое хранение строк в порядке ключа. В PostgreSQL это невозможно из-за MVCC: UPDATE пишет новый tuple туда, где есть место — физический порядок немедленно нарушается. Команда `CLUSTER` существует, но одноразовая, требует `ACCESS EXCLUSIVE` lock и деградирует после первого же UPDATE.

PostgreSQL компенсирует через **Index-Only Scan** и **covering indexes**.

---

## B-tree (по умолчанию)

`CREATE INDEX` без указания типа создаёт B-tree. Сбалансированное дерево, все листовые узлы на одной глубине.

**Операторы:** `=`, `<`, `>`, `<=`, `>=`, `BETWEEN`, `IN`, `IS NULL`, `LIKE 'foo%'` (только prefix).

```sql
CREATE INDEX ON reservations(res_date);
CREATE UNIQUE INDEX ON users(email);

-- Составной: использовать для (table_id) или (table_id, res_date),
-- но НЕ для (res_date) отдельно — leftmost prefix rule
CREATE INDEX ON reservations(table_id, res_date);
```

**Index-Only Scan** — если запрос запрашивает только колонки из индекса, heap не нужен:
```sql
CREATE INDEX ON orders(user_id, created_at);
SELECT user_id, created_at FROM orders WHERE user_id = 42; -- Index-Only Scan
```

**INCLUDE — covering index** — добавить колонки только для покрытия, без участия в поиске:
```sql
CREATE INDEX ON orders(user_id) INCLUDE (total_amount);
SELECT total_amount FROM orders WHERE user_id = 42; -- Index-Only Scan
-- Разница с (user_id, total_amount): INCLUDE-колонки не влияют на сортировку и WHERE
```

---

## Hash

Поддерживает **только `=`**. Компактнее B-tree при длинных ключах (UUID, токены).

```sql
CREATE INDEX ON sessions USING hash(session_token);
```

**Ограничения:** нет `<`/`>`, нет составных, нет сортировки. На практике B-tree на коротких ключах не медленнее → Hash используется редко.

---

## GIN (Generalized Inverted Index)

Для каждого **элемента** составного значения хранит список строк, где он встречается. Применение: `jsonb`, массивы, полнотекстовый поиск.

```sql
-- Массивы
CREATE INDEX ON products USING gin(tags);
SELECT * FROM products WHERE tags @> ARRAY['sale', 'new']; -- содержит все

-- JSONB
CREATE INDEX ON events USING gin(payload);
SELECT * FROM events WHERE payload @> '{"type": "click"}';
SELECT * FROM events WHERE payload ? 'user_id'; -- ключ существует

-- Полнотекст
CREATE INDEX ON articles USING gin(to_tsvector('english', body));
SELECT * FROM articles WHERE to_tsvector('english', body) @@ to_tsquery('postgres & index');
```

**Характеристики:** быстрые запросы, медленные обновления (дорого обновлять инвертированный индекс), большой размер.

---

## GiST (Generalized Search Tree)

Для нестандартных типов: близость, пересечение, вхождение. Extensible framework.

```sql
-- Range types — для EXCLUDE constraint
CREATE INDEX ON reservations USING gist(time_range);
EXCLUDE USING gist(table_id WITH =, time_range WITH &&);

-- Геометрия (PostGIS)
CREATE INDEX ON locations USING gist(coordinates);

-- Полнотекст (альтернатива GIN)
CREATE INDEX ON articles USING gist(to_tsvector('english', body));
```

**GiST vs GIN для полнотекста:**

| | GIN | GiST |
|---|---|---|
| Скорость запросов | Быстрее | Медленнее |
| Скорость обновлений | Медленнее | Быстрее |
| Размер | Больше | Меньше |
| Lossy (recheck) | Нет | Да |

GiST может возвращать false positives — PostgreSQL дополнительно перепроверяет по heap.

---

## BRIN (Block Range INdex)

Хранит **min/max** для диапазонов физических страниц. Размер индекса в сотни раз меньше B-tree.

```sql
CREATE INDEX ON events USING brin(created_at);
```

**Работает только если** данные физически упорядочены по колонке — append-only таблицы: логи, события, временные ряды. Бесполезен при произвольных вставках.

```sql
-- Проверить корреляцию (близко к 1.0 = хорошо):
SELECT correlation FROM pg_stats WHERE tablename='events' AND attname='created_at';
```

---

## Partial Index

Строится только по **подмножеству строк** — тех, что удовлетворяют WHERE.

```sql
-- Индексируем только незакрытые заказы — их мало
CREATE INDEX ON orders(user_id) WHERE status != 'completed';

-- Уникальность только для активных записей
CREATE UNIQUE INDEX ON users(email) WHERE deleted_at IS NULL;
```

Запрос должен содержать то же условие WHERE. Индекс на 10% строк работает в 10 раз быстрее.

---

## Expression Index

Строится по **результату выражения**.

```sql
-- Поиск без учёта регистра
CREATE INDEX ON users(lower(email));
SELECT * FROM users WHERE lower(email) = lower('User@Example.com');

-- Индекс по полю jsonb
CREATE INDEX ON events((payload->>'user_id'));
SELECT * FROM events WHERE payload->>'user_id' = '42';

-- Индекс по дате из timestamp
CREATE INDEX ON orders((created_at::date));
```

PostgreSQL использует expression index только если запрос содержит **точно такое же выражение**.

---

## Шпаргалка

| Тип данных / запрос | Индекс |
|---|---|
| Числа, даты, строки, `=` `<` `>` `LIKE 'x%'` | **B-tree** (по умолчанию) |
| Только `=` на длинных ключах (UUID, токены) | **Hash** |
| `jsonb`, массивы, `@>`, `?`, `&&` | **GIN** |
| Полнотекстовый поиск, высокая запись | **GiST** |
| Полнотекстовый поиск, быстрые запросы | **GIN** |
| Геометрия, ranges, пересечения (`EXCLUDE`) | **GiST** |
| Временные ряды, логи, append-only | **BRIN** |
| IP-адреса, точки, префиксы строк | **SP-GiST** |
| Только часть строк (`WHERE status='active'`) | **Partial B-tree** |
| Поиск по `lower(col)`, выражениям | **Expression B-tree** |
| Покрыть запрос без обращения к heap | **Covering / INCLUDE** |
