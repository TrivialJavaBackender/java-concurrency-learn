package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 6: ConcurrentHashMap и CopyOnWriteArrayList
 *
 * Задание 1: Параллельный подсчёт слов.
 *            Раздели текст на 4 части и посчитай частоту слов параллельно.
 *            Результаты потоков должны корректно агрегироваться без потерь.
 *
 * Задание 2: Покажи разницу между небезопасной составной операцией и атомарной.
 *
 *            Часть A: 10 потоков одновременно пытаются "занять" ресурс через
 *            двухшаговую проверку + запись. Посчитай сколько потоков считают
 *            себя победителями — их должно быть больше одного.
 *
 *            Часть B: та же задача, но гарантированно ровно один победитель.
 *
 * Задание 3: Реализуй SimpleEventBus.
 *
 *            Демонстрация уже написана в main(): 50 потоков одновременно подписываются,
 *            пока publisher рассылает события. "Одноразовый" listener отписывается
 *            сам во время обработки события — прямо внутри итерации publish().
 *
 *            Подумай: что произойдёт если использовать обычный изменяемый список?
 */

// ===== Задание 1: Word Count =====

fun parallelWordCount(text: String): Map<String, Int> {
    val result = ConcurrentHashMap<String, Int>()
    val words = text.lowercase().split("\\s+".toRegex())

    // TODO Split by 4 chunks and merge in parallel
    return result
}

// ===== Задание 2: check-then-put vs putIfAbsent =====

fun raceDemo() {
    println("=== Часть A: сломанный check-then-put ===")
    repeat(20) { run ->
        val map = ConcurrentHashMap<String, String>()
        val winners = AtomicInteger(0)

        val threads = (1..10).map { i ->
            Thread {
                // TODO
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        println("  Run ${run + 1}: winners=${winners.get()}, owner=${map["owner"]}")
    }

    println("\n=== Часть B: атомарная операция ===")
    repeat(5) { run ->
        val map = ConcurrentHashMap<String, String>()
        val winners = AtomicInteger(0)

        val threads = (1..10).map { i ->
            Thread {
                //TODO
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        println("  Run ${run + 1}: winners=${winners.get()}, owner=${map["owner"]}")
    }
}

// ===== Задание 3: Event Bus =====

class SimpleEventBus {
    fun subscribe(listener: (String) -> Unit) {
        // TODO
    }

    fun unsubscribe(listener: (String) -> Unit) {
        // TODO
    }

    fun publish(event: String) {
        // TODO
    }
}

fun main() {
    println("\n=== Задание 1: Word Count ===\n")
    val text = "the quick brown fox jumps over the lazy dog the fox the dog"
    val counts = parallelWordCount(text).toSortedMap()
    println("Word counts: $counts")
    println("Expected:    {the=4, fox=2, dog=2, quick=1, brown=1, jumps=1, over=1, lazy=1}")

    println("=== Задание 2: check-then-put vs putIfAbsent ===\n")
    raceDemo()

    println("\n=== Задание 3: Event Bus — concurrent demo ===\n")
    val bus = SimpleEventBus()
    val latch = CountDownLatch(1)
    val receivedCounts = ConcurrentHashMap<String, AtomicInteger>()

    // "Одноразовый" listener: отписывается прямо внутри обработчика во время итерации publish()
    val oneshotReceived = AtomicInteger(0)
    var oneshot: (String) -> Unit = {}
    oneshot = { event ->
        oneshotReceived.incrementAndGet()
        bus.unsubscribe(oneshot)  // unsubscribe во время итерации!
        println("  [Oneshot] received '$event' and unsubscribed itself mid-iteration")
    }
    bus.subscribe(oneshot)

    // 50 потоков одновременно подписываются, пока publisher уже рассылает
    val subscriberThreads = (1..50).map { i ->
        val name = "L$i"
        val listener: (String) -> Unit = {
            receivedCounts.computeIfAbsent(name) { AtomicInteger() }.incrementAndGet()
        }
        Thread {
            latch.await()
            bus.subscribe(listener)
        }
    }

    val publisherThread = Thread {
        latch.await()
        repeat(3) { i ->
            Thread.sleep(15)
            println("Publishing Event-$i (${receivedCounts.size} listeners registered so far)")
            bus.publish("Event-$i")
        }
    }

    subscriberThreads.forEach { it.start() }
    publisherThread.start()
    latch.countDown()  // все стартуют одновременно!

    subscriberThreads.forEach { it.join() }
    publisherThread.join()

    println("\nResults:")
    println("  Unique listeners that received at least 1 event: ${receivedCounts.size} (of 50)")
    println("  Oneshot received ${oneshotReceived.get()} event(s) — expected 1 (snapshot semantics)")
    println("  No ConcurrentModificationException!")
    println("\nTry replacing CopyOnWriteArrayList with ArrayList — see what breaks.")
}
