# Java Concurrency Roadmap

Структурированный план для быстрого повторения Java Concurrency.
Каждый модуль содержит теорию, ключевые классы и упражнения.

---

## Модуль 1: Основы потоков и синхронизации
**Классы:** `Thread`, `Runnable`, `synchronized`, `volatile`, `wait/notify/notifyAll`

- [ ] Жизненный цикл потока (NEW → RUNNABLE → BLOCKED → WAITING → TIMED_WAITING → TERMINATED)
- [ ] `synchronized` — monitor lock, reentrant, на объекте vs на классе
- [ ] `volatile` — visibility guarantee, happens-before, когда НЕ достаточно
- [ ] `wait()` / `notify()` / `notifyAll()` — почему только внутри synchronized
- [ ] **Упражнение:** `exercises/Ex01_ThreadBasics.kt`
- [ ] **Упражнение:** `exercises/Ex02_ProducerConsumer.kt`

---

## Модуль 2: java.util.concurrent.locks
**Классы:** `ReentrantLock`, `ReadWriteLock`, `ReentrantReadWriteLock`, `StampedLock`, `Condition`

- [ ] `ReentrantLock` vs `synchronized` — tryLock, fairness, interruptible
- [ ] `ReadWriteLock` — read-sharing, write-exclusive
- [ ] `StampedLock` — optimistic reads, performance
- [ ] `Condition` — замена wait/notify с множественными условиями
- [ ] **Упражнение:** `exercises/Ex03_ReentrantLockCache.kt`
- [ ] **Упражнение:** `exercises/Ex04_ReadWriteLock.kt`

---

## Модуль 3: Атомарные операции и CAS
**Классы:** `AtomicInteger`, `AtomicLong`, `AtomicReference`, `AtomicStampedReference`, `LongAdder`, `LongAccumulator`

- [ ] CAS (Compare-And-Swap) — принцип работы, ABA-проблема
- [ ] `AtomicInteger` / `AtomicLong` — lock-free счётчики
- [ ] `AtomicReference` — lock-free обновление ссылок
- [ ] `AtomicStampedReference` — решение ABA
- [ ] `LongAdder` vs `AtomicLong` — когда что использовать
- [ ] **Упражнение:** `exercises/Ex05_AtomicCounter.kt`

---

## Модуль 4: Concurrent Collections
**Классы:** `ConcurrentHashMap`, `CopyOnWriteArrayList`, `ConcurrentLinkedQueue`, `BlockingQueue`, `LinkedBlockingQueue`, `ArrayBlockingQueue`, `ConcurrentSkipListMap`

- [ ] `ConcurrentHashMap` — сегментация (Java 7 vs 8), compute/merge, NO null keys/values
- [ ] `CopyOnWriteArrayList` — snapshot iterator, когда использовать
- [ ] `BlockingQueue` — put/take vs offer/poll, bounded vs unbounded
- [ ] `ConcurrentSkipListMap` — sorted concurrent map, O(log n)
- [ ] **Упражнение:** `exercises/Ex06_ConcurrentMapWordCount.kt`
- [ ] **Упражнение:** `exercises/Ex07_BlockingQueuePipeline.kt`

---

## Модуль 5: ExecutorService и пулы потоков
**Классы:** `ExecutorService`, `ThreadPoolExecutor`, `ScheduledExecutorService`, `Executors`, `Future`, `CompletableFuture`, `ForkJoinPool`

- [ ] `ThreadPoolExecutor` — corePoolSize, maxPoolSize, queue, rejection policies
- [ ] Почему `Executors.newFixedThreadPool()` может быть опасен (unbounded queue)
- [ ] `Future` vs `CompletableFuture` — blocking vs async composition
- [ ] `CompletableFuture` — thenApply, thenCompose, thenCombine, exceptionally, allOf
- [ ] `ForkJoinPool` — work-stealing, RecursiveTask/RecursiveAction
- [ ] `ScheduledExecutorService` — scheduleAtFixedRate vs scheduleWithFixedDelay
- [ ] **Упражнение:** `exercises/Ex08_CompletableFutureChain.kt`
- [ ] **Упражнение:** `exercises/Ex09_ForkJoinMergeSort.kt`

---

## Модуль 6: Synchronizers
**Классы:** `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `Phaser`, `Exchanger`

- [ ] `CountDownLatch` — одноразовый барьер, countDown/await
- [ ] `CyclicBarrier` — многоразовый, с optional barrier action
- [ ] `Semaphore` — ограничение количества одновременных доступов
- [ ] `Phaser` — гибкий аналог CyclicBarrier с динамическими parties
- [ ] `Exchanger` — обмен данными между двумя потоками
- [ ] **Упражнение:** `exercises/Ex10_Synchronizers.kt`

---

## Модуль 7: Проблемы многопоточности
**Концепции:** deadlock, livelock, starvation, race condition, happens-before

- [ ] Deadlock — условия Коффмана, как избежать (lock ordering)
- [ ] Livelock vs Deadlock — потоки активны, но не прогрессируют
- [ ] Starvation — unfair locks, priority inversion
- [ ] Race condition — check-then-act, read-modify-write
- [ ] Java Memory Model — happens-before rules
- [ ] **Упражнение:** `exercises/Ex11_DeadlockDetection.kt`

---

## Модуль 8: Virtual Threads (Java 21+) и современные подходы
**Классы:** `Thread.ofVirtual()`, `StructuredTaskScope`, `ScopedValue`

- [ ] Virtual Threads vs Platform Threads — когда использовать
- [ ] Structured Concurrency — StructuredTaskScope
- [ ] ScopedValue vs ThreadLocal
- [ ] **Упражнение:** `exercises/Ex12_VirtualThreads.kt`

---

## Порядок прохождения

| Приоритет | Модуль | Частота на собесах |
|-----------|--------|--------------------|
| 1 | Модуль 1: Основы | ★★★★★ |
| 2 | Модуль 3: Atomic/CAS | ★★★★★ |
| 3 | Модуль 4: Concurrent Collections | ★★★★★ |
| 4 | Модуль 5: ExecutorService | ★★★★☆ |
| 5 | Модуль 7: Проблемы | ★★★★☆ |
| 6 | Модуль 6: Synchronizers | ★★★☆☆ |
| 7 | Модуль 2: Locks | ★★★☆☆ |
| 8 | Модуль 8: Virtual Threads | ★★☆☆☆ |
