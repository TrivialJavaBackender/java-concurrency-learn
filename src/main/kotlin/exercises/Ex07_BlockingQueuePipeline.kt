package exercises

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 7: Pipeline на BlockingQueue
 *
 * Задание: Реализуй многостадийный pipeline обработки данных:
 *
 *   [Generator] → Queue1 → [Transformer] → Queue2 → [Aggregator]
 *
 * - Generator: Генерирует числа 1..100, кладёт в queue1
 * - Transformer (3 потока): Берёт число из queue1, возводит в квадрат, кладёт в queue2
 * - Aggregator: Берёт из queue2 и суммирует
 *
 * Используй ArrayBlockingQueue(10) для queue1 и LinkedBlockingQueue для queue2.
 * Используй poison pill (специальное значение -1) для сигнала завершения.
 *
 * Ожидаемый результат: sum(i^2 for i in 1..100) = 338350
 */

val POISON_PILL = -1

fun generator(q: BlockingQueue<Int>) {
    (1..100).forEach { q.put(it) }
    q.put(POISON_PILL)
}

fun transformer(from: BlockingQueue<Int>, to: BlockingQueue<Int>) {
    var fromValue = from.take()
    while (fromValue != POISON_PILL) {
        to.put(fromValue * fromValue)
        fromValue = from.take()
    }
    from.put(POISON_PILL)
    to.put(POISON_PILL)
}

fun aggregator(q: BlockingQueue<Int>, sum: AtomicInteger) {
    var pills = 0
    while (pills < 3) {
        val fromValue = q.take()
        if (fromValue == POISON_PILL) {
            pills++
        } else {
            sum.addAndGet(fromValue)
        }
    }
}

fun main() {
    val queue1 = ArrayBlockingQueue<Int>(10)  // bounded — generator блокируется если переполнена
    val queue2 = LinkedBlockingQueue<Int>()    // unbounded

    val sum = AtomicInteger()

    val genThread = Thread { generator(queue1) }
    val transformers = (1..3).map { Thread { transformer(queue1, queue2) } }
    val aggregator = Thread { aggregator(queue2, sum) }

    val threads = transformers + genThread + aggregator

    threads.forEach { it.start() }
    threads.forEach { it.join() }

    println("Actual: ${sum.get()}")

    println("Expected: 338350")
}
