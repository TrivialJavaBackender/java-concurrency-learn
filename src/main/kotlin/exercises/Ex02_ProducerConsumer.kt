package exercises

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.LongAdder

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
    private var lock = Object()

    fun put(value: Int) {
        synchronized(lock) {
            while (buffer.size >= capacity) {
                lock.wait()
            }
            buffer.add(value)
            lock.notifyAll()
        }
    }

    fun take(): Int? {
        synchronized(lock) {
            while (buffer.isEmpty() && !finished) {
                lock.wait()
            }

            if (buffer.isEmpty()) {
                return null
            }

            val res = buffer.removeAt(0)
            lock.notifyAll()

            return res
        }
    }

    fun markFinished() {
        synchronized(lock) {
            finished = true
            lock.notifyAll()
        }
    }
}

fun main() {
    val buffer = BoundedBuffer(5)
    val producerCount = 3
    val consumerCount = 2

    val sum = AtomicInteger()

    val producers = (1..producerCount).map {
        Thread {
            (1..20).forEach { buffer.put(it) }
        }
    }.onEach { it.start() }

    val consumers = (1..consumerCount).map {
        Thread {
            do {
                val value = buffer.take()?.let { sum.addAndGet(it) }
            } while (value != null)
        }
    }.onEach { it.start() }

    producers.forEach { it.join() }
    buffer.markFinished()
    consumers.forEach { it.join() }

    println("Sum: ${sum.get()}")
    println("Expected sum: 630")
}
