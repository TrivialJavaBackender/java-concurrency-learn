package exercises

/**
 * УПРАЖНЕНИЕ 2: Producer-Consumer на wait/notify
 *
 * Задание: Реализуй bounded buffer (ограниченный буфер) размером 5.
 * - 3 producer'а генерируют числа от 1 до 20 каждый
 * - 2 consumer'а потребляют числа и суммируют их
 * - Используй ТОЛЬКО wait/notify (не BlockingQueue)
 * - Producer блокируется когда буфер полон
 * - Consumer блокируется когда буфер пуст
 * - Когда все producer'ы закончили, consumer'ы должны завершиться
 *
 * Ожидаемая сумма: 3 * (1+2+...+20) = 3 * 210 = 630
 */

class BoundedBuffer(private val capacity: Int) {
    private val buffer = mutableListOf<Int>()
    private var finished = false

    @Synchronized
    fun put(value: Int) {
        // TODO
    }

    @Synchronized
    fun take(): Int? {
        // TODO
        return null
    }

    @Synchronized
    fun markFinished() {
        // TODO
    }
}

fun main() {
    val buffer = BoundedBuffer(5)
    val producerCount = 3
    val consumerCount = 2

    // TODO: Запусти 3 producer'а (каждый кладёт числа 1..20)
    // TODO: Запусти 2 consumer'а (каждый суммирует числа до сигнала завершения)
    // TODO: После завершения producer'ов — сигнализируй о конце
    // TODO: Дождись всех потоков и напечатай общую сумму (должна быть 630)

    println("Expected sum: 630")
}
