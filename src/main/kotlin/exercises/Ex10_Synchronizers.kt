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
    //   каждый ждёт startSignal, затем "бежит" (sleep 100-500ms), затем finishSignal.countDown()
    // Дай сигнал старта и дождись финиша всех
}

// ===== Задание 2: CyclicBarrier =====

fun task2_cyclicBarrier() {
    var phase = 1
    val barrier = CyclicBarrier(3) {
        println("=== Phase $phase complete ===")
        phase++
    }

    // TODO: Создай 3 потока, каждый выполняет 3 фазы.
    // В каждой фазе: обработка (sleep) + barrier.await()
    // Дождись завершения всех потоков
}

// ===== Задание 3: Semaphore =====

fun task3_semaphore() {
    val semaphore = Semaphore(3) // только 3 одновременно

    // TODO: Создай 10 потоков, каждый:
    //   acquire() → использует ресурс 500ms → release()
    // Покажи в логах сколько активных потоков одновременно (availablePermits)
    // Дождись всех потоков
}

// ===== Задание 4: Exchanger =====

fun task4_exchanger() {
    val exchanger = Exchanger<List<Int>>()

    // TODO: Producer в цикле 3 раза: заполняет буфер [1..5], вызывает exchanger.exchange()
    // Consumer в цикле 3 раза: получает полный буфер через exchange(), обрабатывает
    // Запусти оба в потоках, дождись завершения
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
