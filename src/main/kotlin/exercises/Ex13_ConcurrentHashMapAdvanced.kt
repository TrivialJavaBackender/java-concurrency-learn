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
        // TODO: Используй computeIfAbsent
        // Внутри loader — инкрементируй computeCount
        return loader(key) // placeholder
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
    )
    val frequency = ConcurrentHashMap<String, Int>()

    // TODO: Для каждого массива логов запусти отдельный поток
    // Используй merge() для накопления счётчиков без synchronized
    // Дождись всех потоков, напечатай результат

    println("Frequency: $frequency")
}

// ===== Задание 3: Bulk Operations =====

fun task3_bulkOperations() {
    val stats = ConcurrentHashMap<String, Long>()
    for (i in 1..1000) {
        stats["page-$i"] = (Math.random() * 2000).toLong()
    }

    // TODO: forEach с parallelismThreshold = 100 — напечатай страницы с > 1500 просмотров
    // TODO: reduceValuesToLong — суммарное количество просмотров
    // TODO: search — первая страница с > 1900 просмотрами
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

    // TODO: Реализуй "expiring counter" через compute():
    // Инкрементируй "counter", при превышении 5 — удаляй (возвращай null из лямбды)
    // 10 потоков по 3 инкремента — что получится?

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
