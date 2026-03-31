package exercises

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingQueue

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

fun main() {
    val queue1 = ArrayBlockingQueue<Int>(10)  // bounded — generator блокируется если переполнена
    val queue2 = LinkedBlockingQueue<Int>()    // unbounded

    // TODO: Generator thread — генерирует числа 1..100, затем сигнализирует о завершении

    // TODO: 3 Transformer threads — берут числа из queue1, возводят в квадрат, кладут в queue2
    //       корректно обрабатывают сигнал завершения и передают его дальше

    // TODO: Aggregator thread — суммирует результаты из queue2 до получения сигнала завершения

    // TODO: Дождись всех потоков

    println("Expected: 338350")
}
