package exercises

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.LongAdder
import kotlin.time.measureTime

/**
 * УПРАЖНЕНИЕ 5: Atomic операции и CAS
 *
 * Задание 1: Реализуй lock-free стек (Treiber Stack) на AtomicReference.
 *            push() и pop() должны работать без synchronized/Lock.
 *
 * Задание 2: Реализуй lock-free счётчик с compareAndSet вручную
 *            (без использования incrementAndGet).
 *
 * Задание 3: Сравни производительность AtomicLong vs LongAdder
 *            при 10 потоках, 1_000_000 инкрементов каждый.
 *            Используй System.nanoTime() для замера.
 */

// ===== Задание 1: Treiber Stack =====

class LockFreeStack<T> {
    private class Node<T>(val value: T, val next: Node<T>?)

    private val top = AtomicReference<Node<T>?>(null)

    fun push(value: T) {
        var newHead: Node<T>?
        var oldHead: Node<T>?
        do {
            oldHead = top.get()
            newHead = Node(value, oldHead)
        } while (!top.compareAndSet(oldHead, newHead))
        // TODO: Реализуй через CAS-цикл (без synchronized)
    }

    fun pop(): T? {
        // TODO: Реализуй через CAS-цикл (без synchronized)
        var newHead: Node<T>?
        var oldHead: Node<T>?

        do {
            oldHead = top.get()
            if (oldHead == null) return null
            newHead = oldHead.next
        } while (!top.compareAndSet(oldHead, newHead))
        return oldHead.value
    }
}

// ===== Задание 2: Manual CAS Counter =====

class CASCounter {
    private val value = AtomicInteger(0)

    fun increment() {
        do {
            val oldValue = value.get()
        } while (!value.compareAndSet(oldValue, oldValue + 1))
        // TODO: Реализуй через цикл с compareAndSet (не используй incrementAndGet)
    }

    fun get(): Int = value.get()
}

// ===== Задание 3: AtomicLong vs LongAdder benchmark =====

fun benchmarkAtomicVsAdder() {
    val threads = 10
    val iterations = 1_000_000

    // TODO: Замерь и сравни время AtomicLong и LongAdder при конкурентных инкрементах
    // Подумай: почему LongAdder быстрее при высокой конкуренции?

    val atomicLong = AtomicLong(0L)
    val longAdder = LongAdder()

    val atomicLongTime = measureTime {
        val atomicLongThread = (1..threads).map {
            Thread { repeat(iterations) { atomicLong.incrementAndGet() } }
        }
        atomicLongThread.forEach { it.start() }
        atomicLongThread.forEach { it.join() }
    }

    val longAdderTime = measureTime {
        val longAdderThreads = (1..threads).map {
            Thread { repeat(iterations) { longAdder.increment() } }
        }
        longAdderThreads.forEach { it.start() }
        longAdderThreads.forEach { it.join() }
    }

    println("AtomicLong Time ${atomicLongTime}ms")
    println("LongAdder Time ${longAdderTime}ms")
}

fun main() {
    // Test Treiber Stack
    val stack = LockFreeStack<Int>()
    val pushThreads = (1..5).map { threadNum ->
        Thread {
            for (i in 1..1000) stack.push(threadNum * 1000 + i)
        }
    }
    pushThreads.forEach { it.start() }
    pushThreads.forEach { it.join() }

    var popCount = 0
    while (stack.pop() != null) popCount++
    println("Stack: pushed 5000, popped $popCount") // Должно быть 5000

    // Test CAS Counter
    val counter = CASCounter()
    val counterThreads = (1..10).map {
        Thread { repeat(100_000) { counter.increment() } }
    }
    counterThreads.forEach { it.start() }
    counterThreads.forEach { it.join() }
    println("CAS Counter: ${counter.get()} (expected 1000000)")

    // Benchmark
    benchmarkAtomicVsAdder()
}
