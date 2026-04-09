# Java Concurrency Roadmap

Структурированный план для быстрого повторения Java Concurrency.
Теория в папке `theory/`. Каждый модуль — чеклист с ссылками на теорию и упражнения.

---

## Порядок прохождения

| Приоритет | Модуль | Частота на собесах |
|-----------|--------|--------------------|
| 1 | Модуль 1: Основы | ★★★★★ |
| 2 | Модуль 3: Atomic/CAS | ★★★★★ |
| 3 | Модуль 4: Concurrent Collections | ★★★★★ |
| 4 | Модуль 7: Проблемы | ★★★★☆ |
| 5 | Модуль 5: ExecutorService | ★★★★☆ |
| 6 | Модуль 2: Locks | ★★★☆☆ |
| 7 | Модуль 6: Synchronizers | ★★★☆☆ |
| 8 | Модуль 8: Virtual Threads | ★★☆☆☆ |

---

## Модуль 1: Основы потоков и синхронизации

📖 Теория: [theory/THREADS_BASICS.md](theory/THREADS_BASICS.md)

- [ ] Жизненный цикл потока: NEW → RUNNABLE → BLOCKED → WAITING → TERMINATED → [§1](theory/THREADS_BASICS.md#1-жизненный-цикл-потока)
- [ ] Thread API: start, join, interrupt, sleep, daemon → [§2](theory/THREADS_BASICS.md#2-thread-api)
- [ ] `synchronized`: монитор, reentrant, на объекте vs на классе → [§3](theory/THREADS_BASICS.md#3-synchronized--монитор)
- [ ] `volatile`: visibility, happens-before, когда НЕ достаточно → [§4](theory/THREADS_BASICS.md#4-volatile)
- [ ] Happens-Before правила JMM → [§5](theory/THREADS_BASICS.md#5-happens-before-jmm)
- [ ] `wait()` / `notify()` / `notifyAll()` — почему в synchronized, spurious wakeup → [§6](theory/THREADS_BASICS.md#6-wait--notify--notifyall)
- [ ] Типичные ошибки → [§7](theory/THREADS_BASICS.md#7-типичные-ошибки)

**Упражнения:**
- [ ] [Ex01: ThreadBasics](src/main/kotlin/exercises/Ex01_ThreadBasics.kt) — потоки, synchronized, wait/notify
- [ ] [Ex02: ProducerConsumer](src/main/kotlin/exercises/Ex02_ProducerConsumer.kt) — bounded buffer на wait/notify

---

## Модуль 2: java.util.concurrent.locks

📖 Теория: [theory/LOCKS.md](theory/LOCKS.md)

- [ ] `ReentrantLock` vs `synchronized` — tryLock, fairness, interruptible → [§1](theory/LOCKS.md#1-reentrantlock-vs-synchronized)
- [ ] `Condition` — множественные условия ожидания, await/signal → [§2](theory/LOCKS.md#2-condition--множественные-условия-ожидания)
- [ ] `ReentrantReadWriteLock` — read-sharing, write-exclusive → [§3](theory/LOCKS.md#3-reentrantreadwritelock)
- [ ] Lock downgrade (write → read) — зачем и как → [§3](theory/LOCKS.md#lock-downgrade-write--read)
- [ ] `StampedLock` — optimistic reads, performance → [§4](theory/LOCKS.md#4-stampedlock-java-8)
- [ ] `LockSupport` — park/unpark → [§5](theory/LOCKS.md#5-locksupport)

**Упражнения:**
- [ ] [Ex03: ReentrantLockCache](src/main/kotlin/exercises/Ex03_ReentrantLockCache.kt) — LRU-кэш, Condition, getOrCompute
- [ ] [Ex04: ReadWriteLock](src/main/kotlin/exercises/Ex04_ReadWriteLock.kt) — MetricsStore, lock downgrade, benchmark

---

## Модуль 3: Атомарные операции и CAS

📖 Теория: [theory/ATOMIC_CAS.md](theory/ATOMIC_CAS.md)

- [ ] CAS (Compare-And-Swap) — принцип работы, CPU инструкция → [§1](theory/ATOMIC_CAS.md#1-cas--compare-and-swap)
- [ ] Семейство Atomic классов, ключевые методы → [§2](theory/ATOMIC_CAS.md#2-семейство-atomic)
- [ ] ABA-проблема и `AtomicStampedReference` → [§3](theory/ATOMIC_CAS.md#3-aba-проблема)
- [ ] `LongAdder` vs `AtomicLong` — когда что → [§4](theory/ATOMIC_CAS.md#4-longadder-vs-atomiclong)
- [ ] Lock-free структуры: Treiber Stack → [§5](theory/ATOMIC_CAS.md#5-lock-free-структуры-данных)
- [ ] `VarHandle` (Java 9+) → [§6](theory/ATOMIC_CAS.md#6-varhandle-java-9)

**Упражнения:**
- [ ] [Ex05: AtomicCounter](src/main/kotlin/exercises/Ex05_AtomicCounter.kt) — Treiber Stack, CAS-цикл, benchmark

---

## Модуль 4: Concurrent Collections

📖 Теория: [theory/CONCURRENT_COLLECTIONS.md](theory/CONCURRENT_COLLECTIONS.md)

- [ ] `ConcurrentHashMap` — внутреннее устройство Java 8+, NO null keys → [§1](theory/CONCURRENT_COLLECTIONS.md#1-concurrenthashmap)
- [ ] Атомарные операции: compute, merge, putIfAbsent → [§1](theory/CONCURRENT_COLLECTIONS.md#api-который-надо-знать-наизусть)
- [ ] `ConcurrentSkipListMap` / `Set` — sorted, O(log n), range queries → [§2](theory/CONCURRENT_COLLECTIONS.md#2-concurrentskiplistmap--concurrentskiplistset)
- [ ] `CopyOnWriteArrayList` — snapshot iterator, когда использовать → [§3](theory/CONCURRENT_COLLECTIONS.md#3-copyonwritearraylist--copyonwritearrayset)
- [ ] `BlockingQueue` семейство: ABQ, LBQ, SynchronousQ, PriorityBQ, DelayQ → [§4](theory/CONCURRENT_COLLECTIONS.md#4-blockingqueue--семейство)
- [ ] `ConcurrentLinkedQueue` — lock-free, size() = O(n) → [§5](theory/CONCURRENT_COLLECTIONS.md#5-concurrentlinkedqueue--concurrentlinkeddeque)
- [ ] `Collections.synchronizedXxx` vs Concurrent — сравнение → [§6](theory/CONCURRENT_COLLECTIONS.md#6-collectionssynchronizedxxx-vs-concurrent)

**Упражнения:**
- [ ] [Ex06: ConcurrentMapWordCount](src/main/kotlin/exercises/Ex06_ConcurrentMapWordCount.kt) — CHM merge, COWAL
- [ ] [Ex07: BlockingQueuePipeline](src/main/kotlin/exercises/Ex07_BlockingQueuePipeline.kt) — pipeline, poison pill
- [ ] [Ex13: CHM Advanced](src/main/kotlin/exercises/Ex13_ConcurrentHashMapAdvanced.kt) — computeIfAbsent, bulk ops
- [ ] [Ex14: BlockingQueues Deep](src/main/kotlin/exercises/Ex14_BlockingQueuesDeep.kt) — все разновидности
- [ ] [Ex15: SkipList & Sets](src/main/kotlin/exercises/Ex15_ConcurrentSkipListAndSets.kt)

---

## Модуль 5: ExecutorService и пулы потоков

📖 Теория: [theory/EXECUTORS_FUTURES.md](theory/EXECUTORS_FUTURES.md)

- [ ] Виды пулов: Fixed, Cached, Single, Scheduled, WorkStealing, Virtual → [§0](theory/EXECUTORS_FUTURES.md#0-виды-thread-pools--сравнение)
- [ ] Опасности Executors.newFixedThreadPool / newCachedThreadPool → [§0](theory/EXECUTORS_FUTURES.md#опасности)
- [ ] `ThreadPoolExecutor` — 7 параметров, алгоритм принятия задачи → [§2](theory/EXECUTORS_FUTURES.md#2-threadpoolexecutor--7-параметров)
- [ ] Rejection policies: Abort, CallerRuns, Discard, DiscardOldest → [§2](theory/EXECUTORS_FUTURES.md#rejection-policies)
- [ ] `ScheduledExecutorService` — AtFixedRate vs WithFixedDelay → [§3](theory/EXECUTORS_FUTURES.md#3-scheduledexecutorservice)
- [ ] `Future<V>` — get(), cancel(), проблемы блокировки → [§4](theory/EXECUTORS_FUTURES.md#4-futurev)
- [ ] `CompletableFuture` — создание, цепочки, thenApply vs thenCompose → [§5](theory/EXECUTORS_FUTURES.md#5-completablefuture--полный-разбор)
- [ ] allOf / anyOf, обработка ошибок (exceptionally/handle/whenComplete) → [§5](theory/EXECUTORS_FUTURES.md#allof--anyof)
- [ ] `ForkJoinPool` — work-stealing, RecursiveTask/RecursiveAction → [§6](theory/EXECUTORS_FUTURES.md#6-forkjoinpool--подробно)
- [ ] invokeAll vs invokeAny vs CompletionService → [§7](theory/EXECUTORS_FUTURES.md#7-invoking-collections-of-tasks)

**Упражнения:**
- [ ] [Ex08: CompletableFutureChain](src/main/kotlin/exercises/Ex08_CompletableFutureChain.kt)
- [ ] [Ex09: ForkJoinMergeSort](src/main/kotlin/exercises/Ex09_ForkJoinMergeSort.kt)
- [ ] [Ex16: ExecutorService Deep](src/main/kotlin/exercises/Ex16_ExecutorServiceDeep.kt) — все типы пулов, rejection policies
- [ ] [Ex17: CF Advanced](src/main/kotlin/exercises/Ex17_CompletableFutureAdvanced.kt) — retry, timeout, race
- [ ] [Ex18: Scheduled & ForkJoin](src/main/kotlin/exercises/Ex18_ScheduledExecutorAndForkJoin.kt)

---

## Модуль 6: Synchronizers

📖 Теория: [theory/SYNCHRONIZERS.md](theory/SYNCHRONIZERS.md)

- [ ] `CountDownLatch` — одноразовый, countDown/await, паттерн стартового пистолета → [§1](theory/SYNCHRONIZERS.md#1-countdownlatch--одноразовый-барьер)
- [ ] `CyclicBarrier` — многоразовый, barrier action, BrokenBarrierException → [§2](theory/SYNCHRONIZERS.md#2-cyclicbarrier--многоразовый-барьер)
- [ ] CountDownLatch vs CyclicBarrier — сравнение → [§2](theory/SYNCHRONIZERS.md#countdownlatch-vs-cyclicbarrier)
- [ ] `Semaphore` — permits, acquire/release, отличие от Lock → [§3](theory/SYNCHRONIZERS.md#3-semaphore--ограничение-доступа)
- [ ] `Phaser` — динамические parties, onAdvance(), иерархический → [§4](theory/SYNCHRONIZERS.md#4-phaser--гибкий-multi-phase-барьер)
- [ ] `Exchanger` — обмен между двумя потоками → [§5](theory/SYNCHRONIZERS.md#5-exchanger--обмен-данными-между-двумя-потоками)

**Упражнения:**
- [ ] [Ex10: Synchronizers](src/main/kotlin/exercises/Ex10_Synchronizers.kt) — Latch, Barrier, Semaphore, Exchanger

---

## Модуль 7: Проблемы многопоточности

📖 Теория: [theory/PROBLEMS.md](theory/PROBLEMS.md)

- [ ] Deadlock — условия Коффмана, как избежать (lock ordering, tryLock) → [§1](theory/PROBLEMS.md#1-deadlock)
- [ ] Обнаружение deadlock: ThreadMXBean, jstack → [§1](theory/PROBLEMS.md#обнаружение)
- [ ] Livelock vs Deadlock — активны, но не прогрессируют → [§2](theory/PROBLEMS.md#2-livelock)
- [ ] Starvation — unfair locks, priority inversion → [§3](theory/PROBLEMS.md#3-starvation)
- [ ] Race condition vs Data race — в чём разница → [§4](theory/PROBLEMS.md#4-race-condition-vs-data-race)
- [ ] JMM — happens-before, safe publication, double-checked locking → [§5](theory/PROBLEMS.md#5-java-memory-model-jmm)
- [ ] False sharing, thread confinement → [§6](theory/PROBLEMS.md#6-дополнительные-проблемы)

**Упражнения:**
- [ ] [Ex11: DeadlockDetection](src/main/kotlin/exercises/Ex11_DeadlockDetection.kt) — создание, обнаружение, исправление

---

## Модуль 8: Virtual Threads (Java 21+)

📖 Теория: [theory/VIRTUAL_THREADS.md](theory/VIRTUAL_THREADS.md)

- [ ] Platform vs Virtual Threads — M:N scheduling, carrier threads → [§1](theory/VIRTUAL_THREADS.md#1-platform-vs-virtual-threads)
- [ ] Как работает scheduling: mount/unmount при блокировке → [§2](theory/VIRTUAL_THREADS.md#2-как-работает-scheduling)
- [ ] Pinning — synchronized vs ReentrantLock, диагностика → [§3](theory/VIRTUAL_THREADS.md#3-pinning--главная-проблема)
- [ ] ThreadLocal vs ScopedValue для VT → [§4](theory/VIRTUAL_THREADS.md#4-threadlocal-с-virtual-threads)
- [ ] Structured Concurrency: StructuredTaskScope → [§5](theory/VIRTUAL_THREADS.md#5-structured-concurrency-java-21-preview)
- [ ] Когда использовать, когда нет → [§6](theory/VIRTUAL_THREADS.md#6-практические-рекомендации)

**Упражнения:**
- [ ] [Ex12: VirtualThreads](src/main/kotlin/exercises/Ex12_VirtualThreads.kt) — массовые VT, pinning, сравнение

---

## Файлы теории

| Файл | Модуль |
|------|--------|
| [theory/THREADS_BASICS.md](theory/THREADS_BASICS.md) | Модуль 1 |
| [theory/LOCKS.md](theory/LOCKS.md) | Модуль 2 |
| [theory/ATOMIC_CAS.md](theory/ATOMIC_CAS.md) | Модуль 3 |
| [theory/CONCURRENT_COLLECTIONS.md](theory/CONCURRENT_COLLECTIONS.md) | Модуль 4 |
| [theory/EXECUTORS_FUTURES.md](theory/EXECUTORS_FUTURES.md) | Модуль 5 |
| [theory/SYNCHRONIZERS.md](theory/SYNCHRONIZERS.md) | Модуль 6 |
| [theory/PROBLEMS.md](theory/PROBLEMS.md) | Модуль 7 |
| [theory/VIRTUAL_THREADS.md](theory/VIRTUAL_THREADS.md) | Модуль 8 |
