package exercises

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.Condition
import kotlin.concurrent.withLock

/**
 * УПРАЖНЕНИЕ 3: Thread-safe кэш на ReentrantLock + Condition
 *
 * Задание: Реализуй потокобезопасный кэш с ограниченным размером (LRU eviction).
 * - Максимум maxSize элементов
 * - get() возвращает значение или null
 * - put() добавляет элемент; если кэш полон — удаляет самый старый
 * - waitForKey() — блокирует поток до тех пор, пока ключ не появится в кэше (используй Condition)
 *
 * Бонус: Используй tryLock(timeout) в отдельном методе getOrTimeout()
 */

class ConcurrentLRUCache<K, V>(private val maxSize: Int) {
    private val lock = ReentrantLock()
    private val keyAdded: Condition = lock.newCondition()
    private val map = LinkedHashMap<K, V>(maxSize, 0.75f, true) // access-order

    fun get(key: K): V? {
        // TODO: Захвати lock, верни map[key]
        return null
    }

    fun put(key: K, value: V) {
        // TODO: Захвати lock
        // Если map.size >= maxSize, удали первый элемент (iterator().next())
        // Положи значение
        // Сигнализируй keyAdded.signalAll()
    }

    fun waitForKey(key: K): V {
        // TODO: Захвати lock
        // Пока map[key] == null — keyAdded.await()
        // Верни map[key]!!
        lock.withLock {
            while (!map.containsKey(key)) {
                keyAdded.await()
            }
            return map[key]!!
        }
    }

    fun size(): Int {
        lock.withLock { return map.size }
    }
}

fun main() {
    val cache = ConcurrentLRUCache<String, Int>(3)

    // Поток ждёт появления ключа "result"
    val waiter = Thread {
        println("Waiting for key 'result'...")
        val value = cache.waitForKey("result")
        println("Got result: $value")
    }
    waiter.start()

    Thread.sleep(500)

    // Заполняем кэш
    cache.put("a", 1)
    cache.put("b", 2)
    cache.put("c", 3)
    println("Cache size: ${cache.size()}") // 3

    cache.put("result", 42) // Это должно разбудить waiter
    println("Cache size after eviction: ${cache.size()}") // 3 (a вытеснен)

    waiter.join()
    println("get('a'): ${cache.get("a")}") // null — вытеснен
    println("get('result'): ${cache.get("result")}") // 42
}
