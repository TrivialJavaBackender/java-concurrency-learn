# Java Concurrency — Вопросы для собеседований

Источники: JCP = "Java Concurrency in Practice" (Goetz), JLS = Java Language Specification, JD = Javadoc

---

## 1. Основы потоков и synchronized

### Q1: Чем отличается `Runnable` от `Callable`?
**A:** `Runnable.run()` — void, не бросает checked exceptions. `Callable.call()` — возвращает значение и может бросить Exception. `Callable` используется с `ExecutorService.submit()`, возвращает `Future<T>`.
> JCP §6.3.2

### Q2: Что такое monitor в Java? Как работает synchronized?
**A:** Каждый объект в Java имеет встроенный монитор (intrinsic lock). `synchronized` захватывает монитор объекта. Только один поток может владеть монитором. `synchronized` — reentrant: поток может повторно захватить уже захваченный им монитор. При synchronized-методе — монитор `this` (или `Class` для static).
> JLS §17.1, JCP §2.3.2

### Q3: Почему wait/notify должны вызываться внутри synchronized?
**A:** Потому что они работают с монитором объекта. `wait()` атомарно отпускает монитор и усыпляет поток. Без `synchronized` будет `IllegalMonitorStateException`. Это предотвращает lost wakeup race condition: между проверкой условия и вызовом wait() другой поток может вызвать notify().
> JCP §14.2.2

### Q4: В чём разница между `notify()` и `notifyAll()`?
**A:** `notify()` будит один произвольный поток, `notifyAll()` — все ждущие. Почти всегда следует использовать `notifyAll()`, потому что `notify()` может привести к «hijacked signal» — пробудится поток, которому уведомление не предназначено, и сигнал будет потерян для нужного потока.
> JCP §14.2.4

### Q5: Что такое daemon thread?
**A:** Фоновый поток. JVM завершается, когда остались только daemon-потоки. Создаётся через `thread.isDaemon = true` ДО `start()`. GC, finalizer — daemon threads.
> Javadoc Thread

---

## 2. volatile и Java Memory Model

### Q6: Что гарантирует volatile?
**A:** **Visibility** — запись в volatile переменную видна всем потокам немедленно (flush в main memory). **Ordering** — запись в volatile создаёт happens-before отношение с последующим чтением той же переменной. НЕ гарантирует атомарность: `volatile int count; count++` — НЕ атомарно (read-modify-write).
> JLS §17.4.5, JCP §3.1.4

### Q7: Что такое happens-before?
**A:** Отношение частичного порядка между операциями, определённое JMM. Если операция A happens-before B, то результаты A гарантированно видны B. Ключевые правила:
1. Внутри одного потока: program order
2. `synchronized`: unlock HB lock того же монитора
3. `volatile`: запись HB чтения той же переменной
4. `Thread.start()` HB первая операция в потоке
5. Последняя операция потока HB `join()`
6. Транзитивность: A HB B, B HB C → A HB C
> JLS §17.4.5, JCP §16.1

### Q8: Что такое false sharing и как его избежать?
**A:** Когда две переменные, используемые разными потоками, попадают в одну кэш-линию (64 байта). Изменение одной инвалидирует кэш-линию для другого ядра, хотя данные не связаны. Решение: `@Contended` аннотация (Java 8+), padding.
> JCP §Appendix A

---

## 3. Atomic и CAS

### Q9: Как работает CAS (Compare-And-Swap)?
**A:** Атомарная CPU-инструкция (CMPXCHG на x86). Три параметра: адрес, ожидаемое значение (expected), новое значение (new). Если текущее значение == expected → записывает new, возвращает true. Иначе ничего не делает, возвращает false. `AtomicInteger.compareAndSet()` — обёртка над CAS.
> JCP §15.3

### Q10: Что такое ABA-проблема? Как решить?
**A:** Поток читает A, другой поток меняет A→B→A. CAS видит A и думает что ничего не менялось, хотя состояние могло измениться. Решение: `AtomicStampedReference` — хранит версию (stamp) вместе со ссылкой. CAS проверяет и значение, и stamp.
> JCP §15.4.4

### Q11: Когда использовать LongAdder вместо AtomicLong?
**A:** `LongAdder` быстрее при высоком contention (много потоков инкрементируют). Внутри хранит массив ячеек (Cell[]), каждый поток инкрементирует свою ячейку, sum() складывает все. Минус: `sum()` не точен в момент конкурентных записей. Используй `AtomicLong` когда нужен точный get() и CAS, `LongAdder` — для статистики/метрик.
> Javadoc LongAdder

---

## 4. Concurrent Collections

### Q12: Как работает ConcurrentHashMap в Java 8?
**A:** Массив Node[], каждый bucket — связный список или TreeBin (при >8 элементах). Блокировка на уровне bucket'а через `synchronized(node)` (не сегменты как в Java 7). `putVal` использует CAS для пустого bucket'а и synchronized для существующего. Не допускает null ключи и значения (в отличие от HashMap).
> JD ConcurrentHashMap, исходники OpenJDK

### Q13: Зачем ConcurrentHashMap запрещает null?
**A:** Неоднозначность: `map.get(key) == null` — ключа нет или значение null? В однопоточном HashMap можно проверить `containsKey()`, но в concurrent среде между get и containsKey другой поток мог изменить map. Doug Lea: "null in concurrent collections is a recipe for hidden bugs."
> Doug Lea's Concurrency FAQ

### Q14: Что такое weakly consistent iterator?
**A:** Итератор ConcurrentHashMap гарантирует: не бросит ConcurrentModificationException, отразит состояние map на момент создания итератора, МОЖЕТ (но не обязан) отразить изменения после создания. В отличие от fail-fast итераторов HashMap.
> JD ConcurrentHashMap

### Q15: Когда использовать CopyOnWriteArrayList?
**A:** Когда чтений намного больше, чем записей. Каждая мутация создаёт новую копию массива → дорого для записи, но итератор никогда не бросит CME и не нужна синхронизация для чтения. Пример: список listeners/observers.
> JCP §5.2.3

### Q16: Какие BlockingQueue реализации и когда какую?
**A:**
- `ArrayBlockingQueue` — bounded, fair/unfair, backed by array. Для классического producer-consumer.
- `LinkedBlockingQueue` — optionally bounded (default Integer.MAX_VALUE), обычно выше throughput чем ABQ.
- `PriorityBlockingQueue` — unbounded, элементы по приоритету.
- `SynchronousQueue` — zero capacity, put блокируется пока кто-то не сделает take. Для handoff.
- `DelayQueue` — элементы доступны только после задержки.
> JCP §5.3

---

## 5. ExecutorService и пулы потоков

### Q17: Почему нельзя использовать `Executors.newFixedThreadPool()` в продакшене?
**A:** Внутри использует `LinkedBlockingQueue` (unbounded). При быстром поступлении задач и медленной обработке очередь растёт бесконечно → OutOfMemoryError. `Executors.newCachedThreadPool()` — другая проблема: maxPoolSize = Integer.MAX_VALUE, может создать слишком много потоков. Лучше создавать `ThreadPoolExecutor` напрямую с bounded queue и rejection policy.
> JCP §8.3.2, Alibaba Java Coding Guidelines

### Q18: Какие rejection policies есть у ThreadPoolExecutor?
**A:**
- `AbortPolicy` (default) — бросает `RejectedExecutionException`
- `CallerRunsPolicy` — задача выполняется в вызывающем потоке (back-pressure)
- `DiscardPolicy` — молча отбрасывает задачу
- `DiscardOldestPolicy` — удаляет самую старую задачу из очереди, ставит новую
> JD ThreadPoolExecutor

### Q19: В чём разница thenApply vs thenCompose в CompletableFuture?
**A:** `thenApply(fn)` — fn возвращает значение T → `CF<T>`. Аналог `map`. `thenCompose(fn)` — fn возвращает `CF<T>` → `CF<T>` (flatMap). Если fn уже возвращает CompletableFuture, используй thenCompose, иначе получишь `CF<CF<T>>`.
> JD CompletableFuture

### Q20: Как работает ForkJoinPool и work-stealing?
**A:** Каждый поток имеет deque задач. Когда deque пуст — поток «ворует» задачи из хвоста deque другого потока. Fork() кладёт подзадачу в свой deque, compute() выполняет синхронно, join() ждёт результат. `commonPool()` используется parallel streams. Для CPU-bound задач: parallelism = кол-во ядер.
> JCP §11.4, JD ForkJoinPool

---

## 6. Synchronizers

### Q21: CountDownLatch vs CyclicBarrier?
**A:**
| | CountDownLatch | CyclicBarrier |
|---|---|---|
| Переиспользование | Одноразовый | Многоразовый (reset) |
| Кто делает countdown | Любой поток | Только участники (await) |
| Barrier action | Нет | Да (Runnable при достижении) |
| Сценарий | "Ждать N событий" | "Все потоки на точке синхронизации" |
> JCP §5.5.1-5.5.2

### Q22: Зачем нужен Semaphore?
**A:** Ограничение количества одновременных доступов к ресурсу. `acquire()` уменьшает permits, `release()` увеличивает. Permits может быть >1 (в отличие от Lock). Бинарный семафор (permits=1) ≈ Lock, но non-reentrant и без владельца (любой поток может release).
> JCP §5.5.3

---

## 7. Deadlock и проблемы многопоточности

### Q23: Какие 4 условия необходимы для deadlock (условия Коффмана)?
**A:**
1. **Mutual exclusion** — ресурс занят эксклюзивно
2. **Hold and wait** — поток держит ресурс и ждёт другой
3. **No preemption** — ресурс нельзя отобрать принудительно
4. **Circular wait** — циклическая зависимость A→B→...→A

Убери любое из 4 — deadlock невозможен. На практике проще всего предотвратить circular wait (упорядочить блокировки).
> JCP §10.1.1

### Q24: Как обнаружить deadlock?
**A:**
- `ThreadMXBean.findDeadlockedThreads()` — программно
- `jstack <pid>` — дамп стеков, показывает "Found one Java-level deadlock"
- `jconsole` / `VisualVM` — GUI
- Thread dump через `kill -3 <pid>` (Unix) или Ctrl+Break (Windows)
> JCP §10.2

### Q25: Что такое livelock? Пример?
**A:** Потоки активны (не заблокированы), но не прогрессируют — постоянно реагируют на действия друг друга. Пример: два потока пытаются избежать deadlock'а через tryLock, отпускают блокировки одновременно и повторяют → бесконечный цикл. Решение: добавить случайную задержку (backoff).
> JCP §10.3.3

### Q26: Что такое starvation? Как предотвратить?
**A:** Поток не может получить доступ к ресурсу неопределённо долго. Причины: unfair locks (потоки с более высоким приоритетом всегда выигрывают), длительные synchronized блоки. Решение: fair locks (`ReentrantLock(true)`), избегать priority manipulation, минимизировать scope блокировки.
> JCP §10.3.1

### Q27: В чём разница race condition и data race?
**A:** **Race condition** — результат зависит от порядка выполнения потоков (логическая ошибка). Пример: check-then-act (`if (!map.containsKey(k)) map.put(k, v)`). **Data race** — два потока обращаются к одной переменной, хотя бы один пишет, без happens-before. Data race — undefined behavior по JMM. Race condition может быть без data race (с корректной синхронизацией, но неверной логикой).
> JLS §17.4.5, JCP §2.2

---

## 8. Locks

### Q28: ReentrantLock vs synchronized?
**A:**
| | synchronized | ReentrantLock |
|---|---|---|
| Синтаксис | Блок/метод | Явный lock/unlock |
| tryLock | Нет | Да (с таймаутом) |
| Interruptible | Нет | `lockInterruptibly()` |
| Fairness | Unfair only | Fair/unfair |
| Condition | 1 (wait/notify) | Множество Condition |
| Производительность | ~Одинаково с Java 6+ |
| Рекомендация | По умолчанию | Когда нужны фичи выше |
> JCP §13.4

### Q29: Что такое StampedLock? Когда использовать?
**A:** Lock с 3 режимами: write lock, read lock, optimistic read. Optimistic read НЕ блокирует — получаешь stamp, читаешь данные, проверяешь `validate(stamp)`. Если невалидный — fallback на обычный read lock. Быстрее ReadWriteLock при редких записях. НЕ reentrant, нельзя использовать с Condition.
> Javadoc StampedLock (Java 8+)

---

## 9. Virtual Threads (Java 21+)

### Q30: Чем виртуальные потоки отличаются от платформенных?
**A:** Platform thread = OS thread (1:1). Virtual thread — легковесный, управляется JVM (M:N scheduling). Миллионы виртуальных потоков на нескольких carrier threads. Блокирующие операции (sleep, I/O) не блокируют carrier — виртуальный поток unmount. Идеальны для I/O-bound задач.
> JEP 444

### Q31: Что такое pinning? Когда виртуальный поток "прибит"?
**A:** Виртуальный поток не может unmount от carrier при: (1) выполнении внутри `synchronized` блока, (2) вызове native метода. Используй `ReentrantLock` вместо `synchronized` с виртуальными потоками. Pinning можно обнаружить через `-Djdk.tracePinnedThreads=full`.
> JEP 444, JEP 491 (Java 24 — synchronized pinning removed)

### Q32: ThreadLocal vs ScopedValue?
**A:** `ThreadLocal` — переменная привязана к потоку, mutable, наследуется через `InheritableThreadLocal`. Проблема с virtual threads: миллионы потоков = миллионы копий. `ScopedValue` (preview) — immutable, автоматически ограничен scope'ом, эффективнее для structured concurrency.
> JEP 481

---

## Шпаргалка: топ-10 тем по частоте на собеседованиях

1. `synchronized` vs `ReentrantLock` (Q2, Q28)
2. `volatile` и happens-before (Q6, Q7)
3. `ConcurrentHashMap` внутреннее устройство (Q12, Q13)
4. `ThreadPoolExecutor` параметры и проблемы (Q17, Q18)
5. Deadlock — условия, обнаружение, предотвращение (Q23, Q24)
6. `CompletableFuture` — цепочки (Q19)
7. CAS и AtomicInteger (Q9, Q10)
8. `CountDownLatch` vs `CyclicBarrier` (Q21)
9. Race condition vs data race (Q27)
10. Virtual Threads (Q30, Q31)
