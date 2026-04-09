package exercises

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * УПРАЖНЕНИЕ 3: Thread-safe кэш на ReentrantLock + Condition
 *
 * Реализуй потокобезопасный LRU-кэш с ограниченным размером.
 * Поля уже объявлены — заполни реализацию методов.
 *
 * Задание 1: get() и put()
 *   - get(): захвати lock, верни значение
 *   - put(): захвати lock; если кэш полон — вытесни самый старый элемент; положи новый;
 *     сигнализируй через Condition (keyAdded) что появился новый ключ
 *
 * Задание 2: waitForKey(key)
 *   Блокирует вызывающий поток, пока ключ не появится в кэше.
 *   Используй Condition.await() / signalAll() — НЕ Thread.sleep.
 *   Подумай: что делать если несколько потоков ждут разные ключи?
 *
 * Задание 3: getOrCompute(key, loader)
 *   Классическая задача: проверь кэш, если нет — вычисли и положи.
 *   Проблема: между "проверил — нет" и "вычислил — положил" другой поток
 *   может тоже начать вычисление. Реализуй так, чтобы loader вызывался ровно 1 раз.
 *   Подумай: почему нельзя вызвать loader() внутри lock?
 *   Hint: проверь кэш повторно после получения write lock (double-check).
 *
 * Задание 4: getOrTimeout(key, timeout, unit)
 *   Как waitForKey, но с таймаутом.
 *   Используй tryLock(timeout) или Condition.await(timeout).
 *   Верни null если ключ так и не появился.
 */

class ConcurrentLRUCache<K, V>(private val maxSize: Int) {
    private val lock = ReentrantLock()
    private val keyAdded = lock.newCondition()
    private val map = LinkedHashMap<K, V>(maxSize, 0.75f, true) // access-order LRU

    fun get(key: K): V? {
        // TODO
        return null
    }

    fun put(key: K, value: V) {
        // TODO: при заполненном кэше вытесни oldest элемент перед вставкой
    }

    fun waitForKey(key: K): V {
        // TODO: блокируй до появления ключа
        throw NotImplementedError()
    }

    fun getOrCompute(key: K, loader: (K) -> V): V {
        // TODO: верни из кэша если есть; иначе вычисли и положи
        // Подумай: можно ли вызывать loader под локом?
        throw NotImplementedError()
    }

    fun getOrTimeout(key: K, timeout: Long, unit: TimeUnit): V? {
        // TODO: жди появления ключа не дольше timeout, верни null если не дождался
        return null
    }

    fun size(): Int = lock.lock().let { try { map.size } finally { lock.unlock() } }
}

fun main() {
    val cache = ConcurrentLRUCache<String, Int>(3)
    val latch = CountDownLatch(1)

    // Два потока ждут разные ключи
    val waiter1 = Thread {
        println("Waiter1: waiting for 'result'...")
        val v = cache.waitForKey("result")
        println("Waiter1: got result=$v")
    }
    val waiter2 = Thread {
        println("Waiter2: waiting for 'bonus' (timeout 800ms)...")
        val v = cache.getOrTimeout("bonus", 800, TimeUnit.MILLISECONDS)
        println("Waiter2: got bonus=$v") // null — не появится
    }

    waiter1.start()
    waiter2.start()

    Thread.sleep(300)
    cache.put("a", 1)
    cache.put("b", 2)
    cache.put("c", 3)
    println("size=${cache.size()}") // 3

    cache.put("result", 42) // вытесняет "a", будит waiter1
    println("size after eviction=${cache.size()}") // 3

    waiter1.join()
    waiter2.join()

    println("get('a')=${cache.get("a")}")       // null — вытеснен
    println("get('result')=${cache.get("result")}") // 42

    // getOrCompute — loader вызывается ровно 1 раз при 50 конкурентных потоках
    val computeCache = ConcurrentLRUCache<String, String>(10)
    var computeCount = 0
    val threads = (1..50).map {
        Thread {
            latch.await()
            computeCache.getOrCompute("key") {
                Thread.sleep(50)
                synchronized(Unit) { computeCount++ }
                "computed"
            }
        }
    }
    threads.forEach { it.start() }
    latch.countDown()
    threads.forEach { it.join() }
    println("getOrCompute loader calls: $computeCount (expected 1)")
}
