package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 13: ConcurrentHashMap — продвинутые операции
 *
 * Задание 1: Реализуй thread-safe кэш с ленивой инициализацией через computeIfAbsent.
 *            Покажи, что вычисление происходит ровно 1 раз даже при 100 конкурентных запросах.
 *
 * Задание 2: Реализуй concurrent frequency counter используя merge().
 *            10 потоков читают "логи" (массивы строк) и считают частоту каждого URL.
 *
 * Задание 3: Используй bulk operations (forEach, reduce, search) с parallelismThreshold.
 *            - forEach: напечатай все записи с count > 100
 *            - reduce: найди суммарное количество
 *            - search: найди первый ключ с count > 1000
 *
 * Задание 4: Покажи разницу между compute() и merge().
 *            compute() — полный контроль (может удалить запись, вернув null).
 *            merge() — только для агрегации существующего с новым.
 */

// ===== Задание 1: Lazy Cache =====

class LazyCache<K, V>(private val loader: (K) -> V) {
    private val cache = ConcurrentHashMap<K, V>()
    val computeCount = AtomicInteger(0)

    fun get(key: K): V {
        // TODO: Используй computeIfAbsent
        // Внутри loader — инкрементируй computeCount (чтобы доказать одноразовость)
        // return cache.computeIfAbsent(key) { k ->
        //     computeCount.incrementAndGet()
        //     loader(k)
        // }
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
            latch.await() // все стартуют одновременно
            cache.get("shared-key")
        }
    }
    threads.forEach { it.start() }
    latch.countDown() // GO!
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

    // TODO: Для каждого массива логов запусти поток.
    // Каждый поток использует merge() для подсчёта:
    //   logs.forEach { url -> frequency.merge(url, 1, Integer::sum) }
    // Дождись всех потоков, напечатай результат.

    println("Frequency: $frequency")
}

// ===== Задание 3: Bulk Operations =====

fun task3_bulkOperations() {
    val stats = ConcurrentHashMap<String, Long>()
    // Заполняем данными
    for (i in 1..1000) {
        stats["page-$i"] = (Math.random() * 2000).toLong()
    }

    // TODO: forEach с parallelismThreshold = 100
    // Напечатай все страницы с > 1500 просмотрами
    // stats.forEach(100) { key, value ->
    //     if (value > 1500) println("  $key: $value views")
    // }

    // TODO: reduce — суммарное количество просмотров
    // val total = stats.reduceValuesToLong(100, { it }, 0L, Long::plus)
    // println("Total views: $total")

    // TODO: search — первая страница с > 1900 просмотрами
    // val found = stats.search(100) { key, value ->
    //     if (value > 1900) "$key=$value" else null
    // }
    // println("Found: $found")
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
    //   - Инкрементируй счётчик
    //   - Если счётчик > 5, удали запись (верни null) — "сброс"
    //   - Запусти 10 потоков, каждый делает 3 инкремента ключа "counter"
    //   - Напечатай финальное значение (будет зависеть от порядка)

    println("compute vs merge demo done")
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
