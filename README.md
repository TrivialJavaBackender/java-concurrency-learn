# Java Concurrency — Interview Prep

Structured hands-on project for refreshing Java Concurrency knowledge before technical interviews. Covers all major classes, concepts, and common interview questions from `java.util.concurrent`.

## Structure

```
├── ROADMAP.md                          # 8 modules, prioritized by interview frequency
├── PROGRESS.md                         # Progress tracker (modules, exercises, theory)
├── INTERVIEW_QUESTIONS.md              # 32 Q&A with sources (JCP, JLS, Javadoc)
├── THEORY_CONCURRENT_COLLECTIONS.md    # Deep dive: CHM, BlockingQueue, COWAL, SkipList
├── THEORY_EXECUTORS_FUTURES.md         # Deep dive: ThreadPoolExecutor, CompletableFuture, ForkJoin
└── src/main/kotlin/exercises/
    ├── Ex01  Thread basics, synchronized, wait/notify
    ├── Ex02  Producer-Consumer (bounded buffer)
    ├── Ex03  ReentrantLock + Condition (LRU cache)
    ├── Ex04  ReadWriteLock (app config)
    ├── Ex05  CAS, Treiber Stack, AtomicLong vs LongAdder
    ├── Ex06  ConcurrentHashMap merge, CopyOnWriteArrayList
    ├── Ex07  BlockingQueue pipeline (ABQ + LBQ)
    ├── Ex08  CompletableFuture chains, allOf, anyOf
    ├── Ex09  ForkJoinPool, RecursiveTask (merge sort)
    ├── Ex10  CountDownLatch, CyclicBarrier, Semaphore, Exchanger
    ├── Ex11  Deadlock creation, detection (ThreadMXBean), prevention
    ├── Ex12  Virtual Threads, pinning (Java 21+)
    ├── Ex13  CHM advanced: computeIfAbsent, bulk ops, compute vs merge
    ├── Ex14  All BlockingQueue types: SynchronousQ, PriorityBQ, DelayQ, TransferQ
    ├── Ex15  ConcurrentSkipListMap, newKeySet, CopyOnWriteArraySet
    ├── Ex16  ThreadPoolExecutor internals, custom factory/rejection, shutdown
    ├── Ex17  CompletableFuture: thenCombine, error handling, retry, timeout
    └── Ex18  ScheduledExecutor, rate limiter, ForkJoin map-reduce
```

## Topics covered

| Topic | Key classes | Exercises |
|-------|------------|-----------|
| Threads & synchronization | `Thread`, `synchronized`, `volatile`, `wait/notify` | 01, 02 |
| Locks | `ReentrantLock`, `ReadWriteLock`, `StampedLock`, `Condition` | 03, 04 |
| Atomic & CAS | `AtomicInteger`, `AtomicReference`, `LongAdder` | 05 |
| Concurrent collections | `ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue`, `ConcurrentSkipListMap` | 06, 07, 13, 14, 15 |
| Executors & futures | `ThreadPoolExecutor`, `CompletableFuture`, `ForkJoinPool`, `ScheduledExecutorService` | 08, 09, 16, 17, 18 |
| Synchronizers | `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `Phaser`, `Exchanger` | 10 |
| Concurrency problems | Deadlock, livelock, starvation, race condition, JMM | 11 |
| Virtual Threads (Java 21+) | `Thread.ofVirtual()`, pinning, `ScopedValue` | 12 |

## How to use

Each exercise file contains TODO comments with instructions and hints. Implement the solution, then run:

```bash
# compile
mvn compile

# run a specific exercise
mvn exec:java -Dexec.mainClass="exercises.Ex01_ThreadBasicsKt"
```

## Tech stack

- Kotlin 2.3 / JVM
- Maven
- JUnit 5 (for tests)

## References

- *Java Concurrency in Practice* — Brian Goetz
- Java Language Specification §17 (Memory Model)
- OpenJDK source code
