# Concurrent Collections — Полная теория

---

## Обзорная карта

```
                        ┌─────────────────────────────────────┐
                        │       Concurrent Collections        │
                        └──────────────┬──────────────────────┘
           ┌───────────┬───────────────┼──────────────┬────────────────┐
           ▼           ▼               ▼              ▼                ▼
        Maps        Lists           Queues          Sets           Deques
           │           │               │              │                │
  ConcurrentHashMap  CopyOnWrite    Blocking      CopyOnWrite    ConcurrentLinked
  ConcurrentSkipList ArrayList      Non-blocking  ArraySet       LinkedBlocking
  Map/Set                           Priority      SkipListSet    Deque
                                    Delay
                                    Synchronous
                                    Transfer
```

---

## 1. ConcurrentHashMap

### Внутреннее устройство (Java 8+)

```
┌──────────────────────────────────────────────────┐
│  Node[] table  (лениво инициализируется)          │
├────┬────┬────┬────┬────┬────┬────┬────┬─────────┤
│ 0  │ 1  │ 2  │ 3  │ 4  │ 5  │ 6  │... │ n-1     │
└─┬──┴────┴─┬──┴────┴────┴─┬──┴────┴────┴─────────┘
  │         │               │
  ▼         ▼               ▼
 Node     null          TreeBin (>8 элементов)
  │                      /    \
  ▼                   TreeNode TreeNode
 Node                  /  \      \
  │                  TN    TN    TN
  ▼
 null
```

**Эволюция:**
- **Java 7:** Массив сегментов (`Segment[]`), каждый сегмент — mini-HashMap со своим `ReentrantLock`. По умолчанию 16 сегментов → 16 потоков могут писать параллельно.
- **Java 8+:** Сегменты убраны. Блокировка на уровне bucket'а (первый Node). Пустой bucket → CAS. Непустой → `synchronized(firstNode)`. При >8 элементах в bucket → трансформация в красно-чёрное дерево (TreeBin).

**Ключевые свойства:**
- **Null запрещён** — ни key, ни value не могут быть null
- **Weakly consistent iterators** — не бросают CME, отражают состояние на момент создания
- **size()** — приблизительный (использует `baseCount` + `CounterCell[]`, как LongAdder)
- **Default concurrency level** — игнорируется с Java 8, но принимается для совместимости

### API, который надо знать наизусть

```java
// Атомарные compound operations — ЭТО ГЛАВНОЕ
V putIfAbsent(K key, V value)              // put только если key отсутствует
V computeIfAbsent(K key, Function<K,V> f)  // вычислить+put если отсутствует
V computeIfPresent(K key, BiFunction f)    // вычислить+replace если присутствует
V compute(K key, BiFunction f)             // вычислить всегда
V merge(K key, V value, BiFunction f)      // если есть — merge, если нет — put

// Bulk operations (Java 8, parallelismThreshold)
void forEach(long threshold, BiConsumer action)
<U> U reduce(long threshold, BiFunction transformer, BiFunction reducer)
<U> U search(long threshold, BiFunction<K,V,U> searchFunction)
```

### Типичные ошибки

```java
// ❌ НЕАТОМАРНО — race condition!
if (!map.containsKey(key)) {
    map.put(key, value);
}

// ✅ Атомарно
map.putIfAbsent(key, value);

// ❌ НЕАТОМАРНО — check-then-act
Integer count = map.get(word);
if (count == null) map.put(word, 1);
else map.put(word, count + 1);

// ✅ Атомарно
map.merge(word, 1, Integer::sum);
// или
map.compute(word, (k, v) -> v == null ? 1 : v + 1);
```

### computeIfAbsent для кэша/мемоизации

```java
// Thread-safe lazy cache
Map<String, Connection> pool = new ConcurrentHashMap<>();
Connection conn = pool.computeIfAbsent(url, this::createConnection);
// Гарантия: createConnection вызовется ровно 1 раз для данного url
// НО: не используй для долгих вычислений — bucket заблокирован!
```

> **Источник:** JCP §5.2.1, OpenJDK source `ConcurrentHashMap.java`

---

## 2. ConcurrentSkipListMap / ConcurrentSkipListSet

### Структура данных

```
Level 3:  HEAD ──────────────────────────────── 50 ──────────── NIL
Level 2:  HEAD ──────── 15 ──────────────────── 50 ──── 72 ─── NIL
Level 1:  HEAD ── 7 ─── 15 ──── 25 ──────────── 50 ──── 72 ─── NIL
Level 0:  HEAD ── 7 ─── 15 ── 20 ── 25 ── 31 ── 50 ── 65 ── 72 ── NIL
```

**Что это:** Concurrent sorted map. Аналог `TreeMap`, но lock-free (CAS).

**Свойства:**
- O(log n) для get/put/remove — как TreeMap
- **Sorted** — навигация: `firstKey()`, `lastKey()`, `headMap()`, `tailMap()`, `subMap()`
- **Lock-free** — никаких блокировок, только CAS
- Weakly consistent iterators
- `ConcurrentSkipListSet` — обёртка, аналог `TreeSet`

**Когда использовать:**
- Нужна concurrent sorted map
- Нужны range queries (`subMap(from, to)`)
- Нужен concurrent NavigableMap

```java
ConcurrentSkipListMap<Long, Order> orders = new ConcurrentSkipListMap<>();
// Все заказы за последний час:
orders.subMap(System.currentTimeMillis() - 3600_000, System.currentTimeMillis());
```

> **Источник:** JCP §5.2.3, Javadoc ConcurrentSkipListMap

---

## 3. CopyOnWriteArrayList / CopyOnWriteArraySet

### Принцип работы

```
                  Поток-Writer
                      │ add("D")
                      ▼
  Старый массив:  [A, B, C]     ← Поток-Reader1 итерирует (snapshot)
  Новый массив:   [A, B, C, D]  ← Новые читатели видят это
                      │
         volatile ссылка обновлена
```

**Механизм:** Каждая мутация (add, set, remove) создаёт ПОЛНУЮ КОПИЮ внутреннего массива. Чтение — без блокировок. Итератор работает со snapshot'ом массива на момент создания.

**Свойства:**
- Чтение: O(1), без блокировок
- Запись: O(n) — копирование массива + lock
- Итератор: snapshot, никогда не бросит CME
- Итератор НЕ поддерживает `remove()` — `UnsupportedOperationException`

**Когда использовать:**
- Чтений >> записей (listener lists, config, routing tables)
- Маленькие коллекции (десятки элементов, не тысячи)
- Нужна безопасная итерация без внешней синхронизации

**Когда НЕ использовать:**
- Частые записи — каждая копирует весь массив
- Большие коллекции — O(n) на каждую мутацию

```java
// Классический use case — список слушателей
List<EventListener> listeners = new CopyOnWriteArrayList<>();

// Безопасная итерация + модификация
for (EventListener l : listeners) {  // snapshot iterator
    l.onEvent(event);
    if (l.isExpired()) listeners.remove(l);  // не сломает итерацию
}
```

> **Источник:** JCP §5.2.3, Javadoc CopyOnWriteArrayList

---

## 4. BlockingQueue — семейство

### Сравнительная таблица

| Реализация | Bounded | Структура | Locks | Особенности |
|---|---|---|---|---|
| `ArrayBlockingQueue` | Да (обязательно) | Circular array | 1 ReentrantLock | Fair mode доступен |
| `LinkedBlockingQueue` | Опционально (default MAX_INT) | Linked nodes | 2 locks (put+take) | Выше throughput |
| `PriorityBlockingQueue` | Нет (unbounded) | Heap | 1 ReentrantLock | Элементы по приоритету |
| `SynchronousQueue` | 0 (нет буфера) | — | Lock-free (unfair) | Прямая передача |
| `DelayQueue` | Нет | Heap | 1 ReentrantLock | Элементы по времени |
| `LinkedTransferQueue` | Нет | Linked nodes | Lock-free | transfer() блокирует |

### API BlockingQueue

```java
// Три группы методов:
//                 Бросает исключение    Возвращает значение    Блокирует     Блокирует с timeout
// Insert:         add(e)                offer(e) → bool        put(e)        offer(e, time, unit)
// Remove:         remove() → E          poll() → E|null        take() → E    poll(time, unit)
// Examine:        element() → E         peek() → E|null        —             —
```

### ArrayBlockingQueue — подробно

```
Circular Buffer:
  ┌───┬───┬───┬───┬───┬───┬───┬───┐
  │ D │ E │   │   │   │ A │ B │ C │    capacity = 8
  └───┴───┴───┴───┴───┴───┴───┴───┘
                        ↑       ↑
                      take    put
                      index   index
```

- **ОДИН lock** для put и take → ниже throughput чем LinkedBlockingQueue
- Fair mode: `new ArrayBlockingQueue<>(capacity, true)` — FIFO для ожидающих потоков
- Предсказуемый memory footprint (массив фиксированного размера)

### LinkedBlockingQueue — подробно

```
  head → Node1 → Node2 → Node3 → ... → NodeN ← tail

  putLock (для записи)     takeLock (для чтения)
  ↓                         ↓
  Можно класть и брать ОДНОВРЕМЕННО!
```

- **ДВА lock'а** — `putLock` и `takeLock` → выше throughput
- Default capacity = `Integer.MAX_VALUE` — **ОСТОРОЖНО: OOM!**
- Используется внутри `Executors.newFixedThreadPool()`

### SynchronousQueue — подробно

```
  Producer.put(X) ─────── БЛОКИРУЕТСЯ ─────── Consumer.take() → X
                    Нет буфера!
                    Прямая передача.
```

- Ёмкость = 0. Нет хранилища.
- `put()` блокируется пока кто-то не вызовет `take()` (и наоборот)
- Используется в `Executors.newCachedThreadPool()`
- Fair mode: очередь ожидающих (FIFO). Unfair: стек (LIFO, быстрее).

### PriorityBlockingQueue

- Unbounded, backed by heap (array)
- Элементы должны реализовывать `Comparable` или передать `Comparator`
- `take()` возвращает элемент с наименьшим значением
- Нет гарантий порядка для элементов с одинаковым приоритетом

### DelayQueue

```java
class DelayedTask implements Delayed {
    private final long executeAt;

    public long getDelay(TimeUnit unit) {
        return unit.convert(executeAt - System.currentTimeMillis(), MILLISECONDS);
    }
}
// take() блокируется пока delay не истечёт
```

- Элементы реализуют `Delayed`
- `take()` возвращает элемент только когда его delay истёк
- Use cases: scheduled tasks, cache expiration, rate limiting

### LinkedTransferQueue (Java 7+)

```java
// transfer() — как SynchronousQueue, но с буфером
queue.transfer(item);  // блокируется пока consumer не заберёт ЭТОТ элемент
queue.tryTransfer(item);  // не блокируется, true если consumer ждал
queue.put(item);  // как обычная очередь — не ждёт consumer'а
```

- Объединяет лучшее из `LinkedBlockingQueue` и `SynchronousQueue`
- Lock-free (CAS)

> **Источник:** JCP §5.3, Javadoc BlockingQueue

---

## 5. ConcurrentLinkedQueue / ConcurrentLinkedDeque

### Свойства
- **Non-blocking** (lock-free, CAS)
- Unbounded
- Weakly consistent iterator
- `size()` — O(n)! Обходит всю очередь. Используй `isEmpty()`.

### Когда использовать
- Высокий contention, нужна максимальная производительность
- Не нужна блокировка (если нет элементов — `poll()` вернёт null, не заблокируется)
- Producer-consumer где consumer не должен ждать

```java
// ❌ Антипаттерн — busy wait
while (true) {
    Item item = queue.poll();
    if (item != null) process(item);
    // else — впустую крутит CPU
}

// ✅ Для ожидания используй BlockingQueue.take()
```

> **Источник:** JCP §15.4, Javadoc ConcurrentLinkedQueue

---

## 6. Collections.synchronizedXxx vs Concurrent

```
              Collections.synchronized*           Concurrent*
Механизм:    Обёртка с synchronized(mutex)       Специальные алгоритмы
Granularity:  Весь collection                    Bucket/segment/node
Throughput:   Низкий (один lock)                 Высокий
Iterator:     Fail-fast (CME!)                   Weakly consistent
Compound ops: НЕ атомарны!                      Атомарны (putIfAbsent и др.)
```

```java
// ❌ synchronizedMap — compound op НЕ атомарна
Map<K,V> map = Collections.synchronizedMap(new HashMap<>());
// Race condition!
if (!map.containsKey(key)) {   // может быть прерван
    map.put(key, value);       // другой поток уже положил
}

// ✅ ConcurrentHashMap — атомарно
map.putIfAbsent(key, value);
```

> **Источник:** JCP §5.1, §5.2

---

## 7. Шпаргалка: какую коллекцию выбрать

```
Нужна Map?
  ├─ Sorted? → ConcurrentSkipListMap
  └─ Нет    → ConcurrentHashMap

Нужна List?
  ├─ Чтений >> записей? → CopyOnWriteArrayList
  └─ Иначе → Collections.synchronizedList или переосмысли дизайн

Нужна Queue?
  ├─ Producer должен ждать если полна?
  │   ├─ Да → BlockingQueue
  │   │   ├─ Фиксированный размер → ArrayBlockingQueue
  │   │   ├─ Максимальный throughput → LinkedBlockingQueue(capacity!)
  │   │   ├─ Приоритеты → PriorityBlockingQueue
  │   │   ├─ Прямая передача → SynchronousQueue
  │   │   └─ Задержка → DelayQueue
  │   └─ Нет → ConcurrentLinkedQueue
  └─ Deque? → ConcurrentLinkedDeque / LinkedBlockingDeque

Нужен Set?
  ├─ Sorted? → ConcurrentSkipListSet
  ├─ Чтений >> записей? → CopyOnWriteArraySet
  └─ Нет → ConcurrentHashMap.newKeySet() (Java 8+)
```

---

## Вопросы для самопроверки

1. Почему ConcurrentHashMap не разрешает null ключи/значения?
2. Чем отличается weakly consistent iterator от fail-fast?
3. Почему LinkedBlockingQueue имеет выше throughput чем ArrayBlockingQueue?
4. В каком Executors фабричном методе используется SynchronousQueue?
5. Когда CopyOnWriteArrayList — плохой выбор?
6. Как ConcurrentHashMap.size() считает количество элементов?
7. Чем transfer() отличается от put() в LinkedTransferQueue?
