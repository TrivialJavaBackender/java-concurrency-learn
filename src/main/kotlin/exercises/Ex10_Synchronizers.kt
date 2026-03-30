package exercises

import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Semaphore
import java.util.concurrent.Exchanger

/**
 * УПРАЖНЕНИЕ 10: Synchronizers
 *
 * Задание 1: CountDownLatch — "стартовый пистолет"
 *   5 потоков-бегунов ждут сигнала старта (latch.await()).
 *   Главный поток делает countdown. Все бегуны стартуют одновременно.
 *   Второй latch — ждём пока все финишируют.
 *
 * Задание 2: CyclicBarrier — многофазная обработка
 *   3 потока обрабатывают данные в 3 фазах. После каждой фазы ждут
 *   друг друга на барьере. Barrier action печатает "Phase X complete".
 *
 * Задание 3: Semaphore — ограниченный пул соединений
 *   10 потоков хотят доступ к ресурсу, но одновременно могут работать только 3.
 *   Каждый "использует ресурс" 500ms.
 *
 * Задание 4: Exchanger — обмен буферами между producer и consumer
 */

// ===== Задание 1: CountDownLatch =====

fun task1_countDownLatch() {
    val startSignal = CountDownLatch(1)
    val finishSignal = CountDownLatch(5)

    // TODO: Создай 5 потоков:
    //   startSignal.await()  // ждёт старта
    //   println("Runner-$i started!")
    //   Thread.sleep(random 100-500ms)
    //   println("Runner-$i finished!")
    //   finishSignal.countDown()
    //
    // println("Ready... Set...")
    // startSignal.countDown()  // GO!
    // finishSignal.await()  // ждём всех
    // println("All runners finished!")
}

// ===== Задание 2: CyclicBarrier =====

fun task2_cyclicBarrier() {
    var phase = 1
    val barrier = CyclicBarrier(3) {
        println("=== Phase $phase complete ===")
        phase++
    }

    // TODO: 3 потока, каждый выполняет 3 фазы:
    //   for (p in 1..3) {
    //     println("Worker-$i processing phase $p")
    //     Thread.sleep(random)
    //     barrier.await()
    //   }
}

// ===== Задание 3: Semaphore =====

fun task3_semaphore() {
    val semaphore = Semaphore(3) // только 3 одновременно

    // TODO: 10 потоков:
    //   semaphore.acquire()
    //   println("Thread-$i acquired (available: ${semaphore.availablePermits()})")
    //   Thread.sleep(500)
    //   semaphore.release()
    //   println("Thread-$i released")
}

// ===== Задание 4: Exchanger =====

fun task4_exchanger() {
    val exchanger = Exchanger<List<Int>>()

    // TODO: Producer заполняет буфер [1,2,3,4,5], обменивает на пустой
    // Consumer получает буфер, обрабатывает, возвращает пустой
    // Сделай 3 обмена
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
