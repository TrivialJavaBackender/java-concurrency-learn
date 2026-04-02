package exercises

import java.lang.Thread.sleep
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.measureTime

/**
 * УПРАЖНЕНИЕ 12: Virtual Threads (Java 21+)
 *
 * Задание 1: Создай 100_000 виртуальных потоков, каждый делает Thread.sleep(1000).
 *            Замерь общее время. Объясни почему это работает, а platform threads — нет.
 *
 * Задание 2: Реализуй HTTP-подобный "сервер":
 *            - Принимает "запросы" (список строк)
 *            - Каждый запрос обрабатывается в виртуальном потоке
 *            - Обработка включает "блокирующий I/O" (Thread.sleep)
 *            - Используй Executors.newVirtualThreadPerTaskExecutor()
 *
 * Задание 3: Покажи проблему с pinning — synchronized блокирует carrier thread.
 *            Замерь время 100 виртуальных потоков с synchronized vs ReentrantLock.
 *            Объясни разницу.
 *
 * ВАЖНО: Требует Java 21+.
 */


fun task1_massiveVirtualThreads() {
    Executors.newVirtualThreadPerTaskExecutor().use { vThreadFactory ->

        val measureTime = measureTime {
            (1..100_000).map {
                vThreadFactory.submit { sleep(1000) }
            }.forEach { it.get() }
        }


        // Дождись всех. Замерь время. Объясни почему это работает, а platform threads — нет.
        println("Time to create 100_000 vThreads: $measureTime")
    }
}

fun task2_virtualThreadServer() {
    Executors.newVirtualThreadPerTaskExecutor().use { vThreadFactory ->

        val count = AtomicInteger(0)

        (1..1000).map { i ->
            vThreadFactory.submit {
                println("Processing task: $i")
                sleep(100)
                count.incrementAndGet()
            }
        }.map { it.get() }

        println("Total processed: ${count.get()}")
    }
}

fun task3_pinningProblem() {

    val syncTime = measureTime {
        val lock = Object()
        val threads = (1..100).map {
            Thread.ofVirtual().start {
                synchronized(lock) {
                    sleep(100)
                }
            }
        }
        threads.onEach { it.join() }
    }

    val lockTime = measureTime {
        val lock = ReentrantLock()
        val threads = (1..100).map {
            Thread.ofVirtual().start {
                lock.withLock {
                    sleep(100)
                }
            }
        }
        threads.onEach { it.join() }
    }

    println("Syncronized time: $syncTime; Lock time: $lockTime")
}

fun main() {
    println("=== Virtual Threads (Java 21+) ===")
    task1_massiveVirtualThreads()
    task2_virtualThreadServer()
    task3_pinningProblem()
}
