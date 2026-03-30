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

    // TODO: Generator thread — put 1..100 в queue1, затем 3 poison pills

    // TODO: 3 Transformer threads — take из queue1, если poison pill -> передай в queue2 и завершись
    //       иначе положи number*number в queue2

    // TODO: Aggregator thread — take из queue2, если poison pill -> уменьши счётчик transformer'ов
    //       когда все 3 transformer'а завершились -> напечатай сумму

    // TODO: Дождись всех потоков

    println("Expected: 338350")
}
