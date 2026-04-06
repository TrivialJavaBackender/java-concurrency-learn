package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 13: ConcurrentHashMap — продвинутые операции
 *
 * Задание 1: Thread-safe кэш с ленивой инициализацией через computeIfAbsent.
 *            Докажи, что вычисление происходит ровно 1 раз даже при 100 конкурентных запросах.
 *            Подсчитай вызовы loader через AtomicInteger.
 *
 * Задание 2: Concurrent frequency counter через merge().
 *            3 потока читают "логи" (массивы URL) и считают частоту каждого.
 *            Используй merge() — без synchronized, без race condition.
 *
 * Задание 3: Bulk operations (forEach, reduce, search) с parallelismThreshold.
 *            - forEach: напечатай все записи с count > 1500
 *            - reduce: найди суммарное количество через reduceValuesToLong
 *            - search: найди первый ключ с count > 1900
 *            Подумай: что означает parallelismThreshold?
 *
 * Задание 4: compute() vs merge() — в чём разница?
 *            Реализуй "expiring counter": инкрементируй счётчик,
 *            но при превышении 5 — удали запись (сброс).
 *            Запусти 10 потоков, каждый делает 3 инкремента ключа "counter".
 */

// ===== Задание 1: Lazy Cache =====

class LazyCache<K, V>(private val loader: (K) -> V) {
    private val cache = ConcurrentHashMap<K, V>()
    val computeCount = AtomicInteger(0)

    fun get(key: K): V {
        val value = cache.computeIfAbsent(key) {
            computeCount.incrementAndGet()
            loader(key)
        }

        return value
    }
}

fun task1_lazyCache() {
    val cache = LazyCache<String, String> { key ->
        Thread.sleep(100) // имитация тяжёлого вычисления
        "computed-$key"
    }

    val latch = CountDownLatch(1)
    val threads = (1..100).map {
        Thread {
            latch.await()
            cache.get("shared-key")
        }
    }
    threads.forEach { it.start() }
    latch.countDown()
    threads.forEach { it.join() }

    println("Compute count: ${cache.computeCount.get()} (expected: 1)")
}

// ===== Задание 2: Frequency Counter =====

fun task2_frequencyCounter() {
    val logs = listOf(
        arrayOf("/api/users", "/api/orders", "/api/users", "/health"),
        arrayOf("/api/orders", "/api/users", "/health", "/api/products"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
        arrayOf("/api/users", "/api/products", "/api/users", "/api/orders"),
    )
    val frequency = ConcurrentHashMap<String, Int>()

    val workers = logs.map { list ->
        Thread {
            list.forEach { api ->
                frequency.merge(api, 1) { v1, v2 ->
                    v1 + v2
                }
            }
        }
    }.onEach { it.start() }
    workers.forEach { it.join() }

    println("Frequency: $frequency")
}

// ===== Задание 3: Bulk Operations =====

fun task3_bulkOperations() {
    val stats = ConcurrentHashMap<String, Long>()
    for (i in 1..1000) {
        stats["page-$i"] = (Math.random() * 2000).toLong()
    }

    stats.forEach(125) { k, v ->
        if (v > 1500) println("$k: $v views")
    }

    val sum = stats.reduceValuesToLong(125, { it }, 0) { v1, v2 ->
        v1 + v2
    }
    println("Sum: $sum")

    val firstSearch = stats.search(125) { k, v -> if (v > 1900) k else null }

    println("First page with 1900 views: $firstSearch")
}

// ===== Задание 4: compute vs merge =====

fun task4_computeVsMerge() {
    val map = ConcurrentHashMap<String, Int>()

    // merge: если ключ есть — применить BiFunction, если нет — вставить value
    map.merge("visits", 1, Integer::sum)  // visits = 1
    map.merge("visits", 1, Integer::sum)  // visits = 2

    // compute: полный контроль, может вернуть null (удалить запись)
    map.compute("visits") { _, v ->
        val newVal = (v ?: 0) + 1
        if (newVal > 10) null  // удалить запись если > 10
        else newVal
    }

    val workers = (1..10).map {
        Thread {
            repeat(3) {
                map.compute("visits") { _, v ->
                    val newVal = (v ?: 0) + 1
                    if (newVal > 5) null
                    else newVal
                }
            }
        }
    }.onEach { it.start() }
    workers.forEach { it.join() }

    println("compute vs merge demo done, visits=${map["visits"]}")
}

fun main() {
    println("=== Task 1: Lazy Cache ===")
    task1_lazyCache()

    println("\n=== Task 2: Frequency Counter ===")
    task2_frequencyCounter()

    println("\n=== Task 3: Bulk Operations ===")
    task3_bulkOperations()

    println("\n=== Task 4: compute vs merge ===")
    task4_computeVsMerge()
}
