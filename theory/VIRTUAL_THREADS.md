# Virtual Threads (Java 21+)

---

## 1. Platform vs Virtual Threads

```
Platform Thread:
  Java Thread ──1:1──► OS Thread ──► CPU Core
  ~1MB stack, дорогой context switch, ~10K потоков на JVM

Virtual Thread:
  Virtual Thread ──M:N──► Carrier Thread (Platform) ──► CPU Core
  ~KB stack, JVM управляет, миллионы потоков
```

| | Platform Thread | Virtual Thread |
|---|---|---|
| Создание | `new Thread()` | `Thread.ofVirtual().start()` |
| Стоимость | ~1 MB RAM + OS overhead | ~1 KB, JVM managed |
| Max на JVM | ~10K–50K | Миллионы |
| Блокировка I/O | Блокирует OS thread | Unmount (carrier свободен) |
| CPU-bound | Одинаково | Одинаково (нет преимущества) |
| Лучший use case | CPU-bound, legacy code | I/O-bound, много concurrent задач |

### Создание

```java
// Один виртуальный поток
Thread vt = Thread.ofVirtual()
    .name("my-vt")
    .start(() -> doWork());

// Executor — один поток на задачу
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> fetchFromDatabase());

// Builder для настройки
Thread.ofVirtual()
    .name("worker-", 0)  // нумерованные имена
    .inheritInheritableThreadLocals(false)
    .factory();  // ThreadFactory
```

---

## 2. Как работает scheduling

```
Virtual Thread "VT-1" выполняет blocking I/O:
  1. VT-1 вызывает socket.read()
  2. JVM: unmount VT-1 с carrier thread → continuation сохранена в heap
  3. Carrier thread свободен → берёт следующий Virtual Thread
  4. I/O готово → VT-1 mount на свободный carrier → продолжает

Carrier threads = ForkJoinPool (parallelism = CPU cores)
```

**Преимущество:** 10 000 concurrent HTTP-запросов → 10 000 virtual threads, но лишь 8 carrier threads (на 8-ядерной машине). Нет overhead от 10 000 OS threads.

---

## 3. Pinning — главная проблема

**Pinning** — virtual thread не может unmount от carrier thread:

```java
// ❌ Pinning: synchronized блок
synchronized (lock) {
    Thread.sleep(1000);  // sleep вызовет unmount, но carrier заблокирован!
    // Carrier thread недоступен для других VT всё это время
}

// ✅ ReentrantLock — поддерживает unmount
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    Thread.sleep(1000);  // VT unmounts, carrier свободен
} finally {
    lock.unlock();
}
```

**Когда происходит pinning:**
1. Virtual thread выполняется внутри `synchronized` блока
2. Virtual thread вызывает native метод

> **Java 24 (JEP 491):** `synchronized` больше не вызывает pinning! Но до Java 24 — используй `ReentrantLock`.

### Диагностика pinning

```bash
-Djdk.tracePinnedThreads=full   # логировать каждый pinning event
-Djdk.tracePinnedThreads=short  # только стек-трейс без деталей
```

---

## 4. ThreadLocal с Virtual Threads

**Проблема:** при миллионе virtual threads — миллион копий ThreadLocal.

```java
// ❌ Дорого при большом количестве VT
ThreadLocal<UserContext> ctx = new ThreadLocal<>();

// ✅ ScopedValue (Java 21+, preview → finalized в 24)
ScopedValue<UserContext> USER_CTX = ScopedValue.newInstance();

ScopedValue.where(USER_CTX, new UserContext("admin"))
    .run(() -> {
        // USER_CTX.get() доступен здесь и во всех вложенных вызовах
        doWork();
    });
// После run() — значение автоматически убрано
```

**ScopedValue vs ThreadLocal:**

| | ThreadLocal | ScopedValue |
|---|---|---|
| Mutability | Mutable | Immutable |
| Scope | На весь lifetime потока | Ограниченный блок |
| Наследование | InheritableThreadLocal | Автоматически в дочерние VT |
| Производительность | O(1) get | Быстрее при большом числе потоков |
| Cleanup | Нужен remove() | Автоматически |

---

## 5. Structured Concurrency (Java 21+, preview)

Дочерние потоки живут строго внутри блока родителя — нет утечек.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Supplier<String> user    = scope.fork(() -> fetchUser(id));
    Supplier<String> address = scope.fork(() -> fetchAddress(id));

    scope.join();           // ждём оба
    scope.throwIfFailed();  // пробрасываем ошибки

    return user.get() + " at " + address.get();
}
// Выход из try-with-resources: все дочерние задачи гарантированно завершены
```

**Политики:**
- `ShutdownOnFailure` — если любой fork упал → отменить остальные
- `ShutdownOnSuccess` — если любой fork успешен → отменить остальные (anyOf)

**Преимущества перед CompletableFuture:**
- Никаких утечек потоков (структурированная иерархия)
- Чёткая отмена (если родитель отменён — дети тоже)
- Читаемый код (нет callback hell)

---

## 6. Практические рекомендации

```
✅ Используй VT для:
  - HTTP серверы (Netty-free: один VT на запрос)
  - JDBC запросы (блокирующий драйвер)
  - Много параллельных I/O операций
  - Замена пула с newFixedThreadPool(много)

❌ НЕ используй VT для:
  - CPU-bound задач (нет преимущества)
  - Задач с synchronized (pinning до Java 24)
  - Там где нужен точный контроль над потоками

⚠️ Заменяй synchronized на ReentrantLock в критических путях
⚠️ Не используй ThreadLocal для большого состояния — ScopedValue
⚠️ Не используй thread priorities у VT — игнорируются
```

---

## Вопросы для самопроверки

1. Что такое pinning? Как его избежать (до и после Java 24)?
2. Сколько carrier threads у newVirtualThreadPerTaskExecutor?
3. Чем ScopedValue лучше ThreadLocal для Virtual Threads?
4. Что такое structured concurrency? Чем лучше CompletableFuture?
5. Для каких задач Virtual Threads НЕ дают преимущества?
6. Как диагностировать pinning?
7. Что произойдёт если запустить 1 000 000 virtual threads с Thread.sleep(1000)?
