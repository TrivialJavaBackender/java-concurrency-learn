package exercises

import java.util.concurrent.locks.ReentrantReadWriteLock

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

    fun record(name: String, value: Long) {
        // TODO
    }

    fun get(name: String): Long? {
        // TODO
        return null
    }

    fun snapshot(): Map<String, Long> {
        // TODO: верни копию под read lock
        return emptyMap()
    }

    fun bulkUpdate(updates: Map<String, Long>) {
        // TODO: обнови все ключи атомарно под единым write lock
    }

    fun getOrInit(name: String, initializer: () -> Long): Long {
        // TODO: реализуй lock downgrade
        // 1. read lock → проверь
        // 2. если нет: release read, acquire write, double-check, вычисли, запиши
        // 3. пока держишь write lock — возьми read lock
        // 4. release write lock
        // 5. используй значение под read lock, release read lock
        // Зачем п.3-4? Чтобы никакой writer не успел изменить значение между release(write) и acquire(read)
        throw NotImplementedError()
    }
}

fun main() {
    val store = MetricsStore()
    store.bulkUpdate(mapOf("cpu" to 0L, "mem" to 0L, "rps" to 0L))

    // === Задание 2: Consistency demo ===
    println("=== Consistency ===")
    var inconsistencies = 0
    var finished = false

    val writer = Thread {
        var v = 0L
        while (!finished) {
            store.bulkUpdate(mapOf("cpu" to v, "mem" to v, "rps" to v))
            v++
            Thread.sleep(1)
        }
    }

    // Читатель БЕЗ snapshot — может поймать частичное состояние
    val reader = Thread {
        repeat(10_000) {
            // TODO: прочитай cpu, mem, rps по отдельности через get()
            // Проверь что все три равны (если нет — inconsistencies++)
        }
    }

    writer.start()
    reader.start()
    reader.join()
    finished = true
    writer.join()
    println("Inconsistencies without snapshot: $inconsistencies")

    // Повтори reader используя snapshot() — inconsistencies должны быть 0
    // TODO

    // === Задание 3: Benchmark ===
    println("\n=== Benchmark: ReadWriteLock vs synchronized ===")
    // TODO: Запусти 10 reader-потоков + 1 writer на 3 секунды
    // Считай количество read-операций для RWLock и для synchronized версии
    // Напечатай throughput каждого

    // === Задание 4: Lock downgrade ===
    println("\n=== Lock Downgrade ===")
    val results = (1..10).map {
        Thread { store.getOrInit("expensive-key") { Thread.sleep(100); 42L } }
    }
    results.forEach { it.start() }
    results.forEach { it.join() }
    println("getOrInit result: ${store.get("expensive-key")} (expected 42)")
}
