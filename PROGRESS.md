# Progress Tracker

## Статус модулей

| Модуль | Статус | Дата начала | Дата завершения |
|--------|--------|-------------|-----------------|
| 1. Основы потоков | ⬜ не начат | — | — |
| 2. Locks | ⬜ не начат | — | — |
| 3. Atomic/CAS | ⬜ не начат | — | — |
| 4. Concurrent Collections | ⬜ не начат | — | — |
| 5. ExecutorService | ⬜ не начат | — | — |
| 6. Synchronizers | ⬜ не начат | — | — |
| 7. Проблемы многопоточности | ⬜ не начат | — | — |
| 8. Virtual Threads | ⬜ не начат | — | — |

## Упражнения

| # | Упражнение | Тема | Статус |
|---|-----------|------|--------|
| 01 | ThreadBasics | Потоки, synchronized, wait/notify | ⬜ |
| 02 | ProducerConsumer | Bounded buffer, wait/notify | ⬜ |
| 03 | ReentrantLockCache | ReentrantLock, Condition, getOrCompute | ⬜ |
| 04 | ReadWriteLock | MetricsStore, lock downgrade, benchmark | ⬜ |
| 05 | AtomicCounter | CAS, Treiber Stack, LongAdder | ⬜ |
| 06 | ConcurrentMapWordCount | CHM merge, COWAL event bus | ⬜ |
| 07 | BlockingQueuePipeline | ABQ, LBQ, pipeline, poison pill | ⬜ |
| 08 | CompletableFutureChain | CF цепочки, allOf, anyOf | ⬜ |
| 09 | ForkJoinMergeSort | ForkJoinPool, RecursiveTask | ⬜ |
| 10 | Synchronizers | Latch, Barrier, Semaphore, Exchanger | ⬜ |
| 11 | DeadlockDetection | Deadlock, ThreadMXBean, prevention | ⬜ |
| 12 | VirtualThreads | Virtual Threads, pinning (Java 21+) | ⬜ |
| 13 | CHM Advanced | computeIfAbsent, merge, bulk ops | ⬜ |
| 14 | BlockingQueues Deep | SynchronousQ, PriorityBQ, DelayQ, TransferQ | ⬜ |
| 15 | SkipList & Sets | ConcurrentSkipListMap, newKeySet, COWAS | ⬜ |
| 16 | ExecutorService Deep | Все типы пулов, rejection policies, shutdown | ⬜ |
| 17 | CF Advanced | thenCombine, handle, retry, timeout | ⬜ |
| 18 | Scheduled & ForkJoin | Rate vs Delay, rate limiter, map-reduce | ⬜ |

## Теория

| Файл | Тема | Изучено |
|------|------|---------|
| [theory/THREADS_BASICS.md](theory/THREADS_BASICS.md) | Потоки, synchronized, volatile, JMM | ⬜ |
| [theory/LOCKS.md](theory/LOCKS.md) | ReentrantLock, ReadWriteLock, StampedLock | ⬜ |
| [theory/ATOMIC_CAS.md](theory/ATOMIC_CAS.md) | CAS, Atomic*, LongAdder, ABA | ⬜ |
| [theory/CONCURRENT_COLLECTIONS.md](theory/CONCURRENT_COLLECTIONS.md) | CHM, BlockingQueue, CopyOnWrite | ⬜ |
| [theory/EXECUTORS_FUTURES.md](theory/EXECUTORS_FUTURES.md) | Все пулы, CompletableFuture, ForkJoin | ⬜ |
| [theory/SYNCHRONIZERS.md](theory/SYNCHRONIZERS.md) | Latch, Barrier, Semaphore, Phaser | ⬜ |
| [theory/PROBLEMS.md](theory/PROBLEMS.md) | Deadlock, livelock, race condition, JMM | ⬜ |
| [theory/VIRTUAL_THREADS.md](theory/VIRTUAL_THREADS.md) | VT, pinning, ScopedValue | ⬜ |
| [INTERVIEW_QUESTIONS.md](INTERVIEW_QUESTIONS.md) | 32 вопроса для собеседований | ⬜ |

## Вопросы для собеседований

| Секция | Вопросы | Изучено |
|--------|---------|---------|
| Основы потоков и synchronized | Q1–Q5 | ⬜ |
| volatile и JMM | Q6–Q8 | ⬜ |
| Atomic и CAS | Q9–Q11 | ⬜ |
| Concurrent Collections | Q12–Q16 | ⬜ |
| ExecutorService и пулы | Q17–Q20 | ⬜ |
| Synchronizers | Q21–Q22 | ⬜ |
| Deadlock и проблемы | Q23–Q27 | ⬜ |
| Locks | Q28–Q29 | ⬜ |
| Virtual Threads | Q30–Q32 | ⬜ |

---
Легенда: ⬜ не начато | 🔄 в процессе | ✅ завершено
