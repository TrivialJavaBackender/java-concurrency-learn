package exercises

import java.lang.Math.random
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Semaphore
import java.util.concurrent.Exchanger
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTime

/**
 * УПРАЖНЕНИЕ 10: Synchronizers
 *
 * Задание 1: CountDownLatch — "стартовый пистолет"
 *   5 потоков-бегунов ждут сигнала старта (latch.await()).
 *   Главный поток даёт сигнал. Все бегуны стартуют одновременно.
 *   Второй latch — ждём пока все финишируют.
 *
 * Задание 2: CyclicBarrier — многофазная обработка
 *   3 потока обрабатывают данные в 3 фазах. После каждой фазы ждут
 *   друг друга на барьере. Barrier action печатает "Phase X complete".
 *   Подумай: почему CyclicBarrier, а не CountDownLatch?
 *
 * Задание 3: Semaphore — ограниченный пул соединений
 *   10 потоков хотят доступ к ресурсу, но одновременно могут работать только 3.
 *   Каждый "использует ресурс" 500ms. Покажи, что одновременно не больше 3.
 *
 * Задание 4: Exchanger — обмен буферами между producer и consumer
 *   Producer заполняет буфер, обменивает на пустой от consumer'а.
 *   Consumer обрабатывает буфер, возвращает пустой. Сделай 3 обмена.
 */

// ===== Задание 1: CountDownLatch =====

fun task1_countDownLatch() {
    val startSignal = CountDownLatch(1)
    val finishSignal = CountDownLatch(5)

    // TODO: Создай 5 потоков-бегунов:
    //   каждый ждёт стартового сигнала, затем "бежит" (sleep 100-500ms), затем сигнализирует о финише

    val runners = (1..5).map {
        Thread {
            startSignal.await()
            Thread.sleep((random() * 1000).toLong())
            println("Thread $it finished")
            finishSignal.countDown()
        }
    }

    runners.forEach { it.start() }
    startSignal.countDown()
    finishSignal.await()

    println("All threads finished")
}

// ===== Задание 2: CyclicBarrier =====

fun task2_cyclicBarrier() {
    var phase = AtomicInteger(0)
    val barrier = CyclicBarrier(3) {
        println("=== Phase ${phase.incrementAndGet()} complete ===")

    }

    val runners = (1..3).map { threadNumber ->
        Thread {
            repeat(3) {
                Thread.sleep((random() * 1000).toLong())
                println("Thread $threadNumber finished barrier")
                barrier.await()
            }
        }
    }.onEach { it.start() }

    runners.forEach { it.join() }

}

// ===== Задание 3: Semaphore =====

fun task3_semaphore() {
    val semaphore = Semaphore(3) // только 3 одновременно

    val threads = (1..10).map {
        Thread {
            semaphore.acquire()
            try {
                println("Thread $it acquired semaphore")
                Thread.sleep(500)
                println("Thread $it released semaphore")
            } finally {
                semaphore.release()
            }
        }
    }

    val totalTime = measureTime {
        threads.forEach { it.start() }
        threads.forEach { it.join() }
    }

    println("Total time: $totalTime")
}

// ===== Задание 4: Exchanger =====

fun task4_exchanger() {
    val exchanger = Exchanger<List<Int>>()

    val producer = Thread {
        var buf: List<Int>
        repeat(3) {
            buf = (1..5).toList()
            val emptyBuf = exchanger.exchange(buf)
            buf = exchanger.exchange(emptyBuf)
            println("Producer received back: $buf")
        }
    }

    val consumer = Thread {
        repeat(3) {
            val unprocessedList = exchanger.exchange(emptyList())
            println("Consumer received unprocessed data: $unprocessedList")
            val processedList = unprocessedList.map { it * 123 }
            exchanger.exchange(processedList)
        }
    }

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
}

fun main() {
    println("=== Task 1: CountDownLatch ===")
    task1_countDownLatch()

    println("\n=== Task 2: CyclicBarrier ===")
    task2_cyclicBarrier()

    println("\n=== Task 3: Semaphore ===")
    task3_semaphore()

    println("\n=== Task 4: Exchanger ===")
    task4_exchanger()
}
