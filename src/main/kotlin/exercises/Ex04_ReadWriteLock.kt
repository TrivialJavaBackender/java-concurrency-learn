package exercises

import java.lang.Math.pow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.sqrt

/**
 * УПРАЖНЕНИЕ 4: Хранилище метрик на ReadWriteLock
 *
 * Задание 1: Реализуй MetricsStore
 *   - record(name, value) — записывает метрику (write lock)
 *   - get(name) — возвращает последнее значение (read lock)
 *   - snapshot() — возвращает копию всех метрик (read lock)
 *   - bulkUpdate(updates) — атомарно обновляет несколько метрик (write lock)
 *
 * Задание 2: Покажи проблему неконсистентного чтения
 *   Запусти writer, который вызывает bulkUpdate() каждые 50ms (обновляет 3 метрики сразу).
 *   Запусти reader без lock, который читает 3 метрики по отдельности.
 *   Покажи что reader иногда видит частично обновлённое состояние.
 *   Исправь через snapshot() — атомарное чтение под read lock.
 *
 * Задание 3: Benchmark — покажи преимущество ReadWriteLock над synchronized
 *   10 reader-потоков + 1 writer vs synchronized на тех же данных.
 *   Замерь throughput за 3 секунды (сколько read операций выполнено).
 *
 * Задание 4: Lock downgrade
 *   Реализуй getOrInit(name, initializer):
 *   - Сначала проверь под read lock (быстрый путь)
 *   - Если нет — отпусти read lock, возьми write lock, проверь снова, инициализируй
 *   - После записи: возьми read lock НЕ отпуская write lock, отпусти write lock
 *   - Работай с данными под read lock, отпусти
 *   Это называется lock downgrade — зачем это нужно?
 */

class MetricsStore {
    private val lock = ReentrantReadWriteLock()
    private val metrics = mutableMapOf<String, Long>()
    private val computedValues = ConcurrentHashMap<String, FutureTask<Long>>()

    fun record(name: String, value: Long) {
        lock.write {
            metrics[name] = value
        }
    }

    fun get(name: String): Long? {
        lock.read {
            return metrics[name]
        }
    }
    fun getUnsafe(name: String): Long? {
        return metrics[name]
    }

    @Synchronized
    fun snapshotSync(): Map<String, Long> {
            return HashMap(metrics)
    }

    @Synchronized
    fun bulkUpdateSync(updates: Map<String, Long>) {
        metrics.putAll(updates)
    }

    fun snapshot(): Map<String, Long> {
        lock.read {
            return HashMap(metrics)
        }
    }

    fun snapshotUnsafe(): Map<String, Long> {
        return HashMap(metrics)
    }

    fun bulkUpdate(updates: Map<String, Long>) {
        lock.write {
            metrics.putAll(updates)
        }
    }

    fun getOrInit(name: String, initializer: () -> Long): Long {

        val read = lock.read {
            metrics[name]
        }

        if (read != null) return read
        val task = computedValues.computeIfAbsent(name) { FutureTask { initializer() } }
        task.run()

        try {
            lock.writeLock().lock()
            val value = task.get()

            if (!metrics.containsKey(name)) {
                metrics[name] = value
            }
            lock.readLock().lock()
        } finally {
            lock.writeLock().unlock()
            computedValues.remove(name)
        }

        try {
            return metrics[name] ?: error("$name not found")
        } finally {
            lock.readLock().unlock()
        }
    }
}

fun main() {
    val store = MetricsStore()
    store.bulkUpdate(mapOf("cpu" to 0L, "mem" to 0L, "rps" to 0L))

    // === Задание 2: Consistency demo ===
    println("=== Consistency ===")
    var inconsistencies = 0
    val finished = AtomicBoolean(false)

    var writer = Thread {
        var v = 0L
        while (!finished.get()) {
            store.bulkUpdate(mapOf("cpu" to v, "mem" to v, "rps" to v))
            v++
        }
    }

    // Читатель БЕЗ snapshot — может поймать частичное состояние
    var reader = Thread {
        repeat(10_000) {
            val currentValue = store.getUnsafe("mem")
            val equals = currentValue == store.getUnsafe("rps")
                    && currentValue == store.getUnsafe("cpu")
            if (!equals) {
                inconsistencies++
            }
        }
    }

    writer.start()
    reader.start()
    reader.join()
    finished.set(true)
    writer.join()
    println("Inconsistencies without snapshot: $inconsistencies")

    // Повтори reader используя snapshot() — inconsistencies должны быть 0
    inconsistencies = 0
    finished.set(false)

    writer = Thread {
        var v = 0L
        while (!finished.get()) {
            store.bulkUpdate(mapOf("cpu" to v, "mem" to v, "rps" to v))
            v++
        }
    }

    reader = Thread {
        repeat(10_000) {
            val store = store.snapshot()
            val currentValue = store["mem"]
            val equals = currentValue == store["rps"]
                    && currentValue == store["cpu"]
            if (!equals) {
                inconsistencies++
            }
        }
    }

    writer.start()
    reader.start()
    reader.join()
    finished.set(true)
    writer.join()
    println("Inconsistencies with snapshot: $inconsistencies")

    // === Задание 3: Benchmark ===
    println("\n=== Benchmark: ReadWriteLock vs synchronized ===")

    val isFinished = AtomicBoolean(false)
    val syncReads = LongAdder()
    val syncWrites = LongAdder()

    val asyncReads = LongAdder()
    val asyncWrites = LongAdder()

    var readers = (1..10).map {
        Thread {
            while (!isFinished.get()) {
                val store = store.snapshotSync()
                store.forEach { string, num -> pow(sqrt(num.toDouble()), 24.0) }
                syncReads.increment()
            }
        }
    }.onEach { it.start() }

    writer = Thread {
        var v = 0L
        while (!isFinished.get()) {
            val map = (1..10000L).map { "value-$it" to v }.toMap()
            store.bulkUpdateSync(map)
            v++
            syncWrites.increment()
            Thread.sleep(1)
        }
    }.also { it.start() }

    Thread.sleep(3000)
    isFinished.set(true)
    readers.forEach { it.join() }
    writer.join()

    println("Sync reads  : $syncReads. Sync Writes:  $syncWrites")

    isFinished.set(false)
    readers = (1..10).map {
        Thread {
            while (!isFinished.get()) {
                val store = store.snapshot()
                store.forEach { string, num -> pow(sqrt(num.toDouble()), 24.0) }
                asyncReads.increment()
            }
        }
    }.onEach { it.start() }

    writer = Thread {
        var v = 0L
        while (!isFinished.get()) {
            val map = (1..10000L).map { "value-$it" to v }.toMap()
            store.bulkUpdate(map)
            v++
            asyncWrites.increment()
            Thread.sleep(1)
        }
    }.also { it.start() }

    Thread.sleep(3000)
    isFinished.set(true)
    readers.forEach { it.join() }
    writer.join()

    println("RWLock reads: $asyncReads. RWLock Writes: $asyncWrites")

    // Сравни throughput ReadWriteLock и synchronized версии

    // === Задание 4: Lock downgrade ===
    println("\n=== Lock Downgrade ===")
    val results = (1..10).map {
        Thread { store.getOrInit("expensive-key") { Thread.sleep(100); 42L } }
    }
    results.forEach { it.start() }
    results.forEach { it.join() }
    println("getOrInit result: ${store.get("expensive-key")} (expected 42)")
}
