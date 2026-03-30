package exercises

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.LongAdder

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
 */

// ===== Задание 1: Treiber Stack =====

class LockFreeStack<T> {
    private class Node<T>(val value: T, val next: Node<T>?)

    private val top = AtomicReference<Node<T>?>(null)

    fun push(value: T) {
        // TODO: CAS loop:
        //   val oldTop = top.get()
        //   val newTop = Node(value, oldTop)
        //   повторяй пока top.compareAndSet(oldTop, newTop) не вернёт true
    }

    fun pop(): T? {
        // TODO: CAS loop:
        //   val oldTop = top.get() ?: return null
        //   val newTop = oldTop.next
        //   повторяй пока top.compareAndSet(oldTop, newTop) не вернёт true
        //   return oldTop.value
        return null
    }
}

// ===== Задание 2: Manual CAS Counter =====

class CASCounter {
    private val value = AtomicInteger(0)

    fun increment() {
        // TODO: Реализуй через цикл с compareAndSet
        // while(true) { val cur = value.get(); if (value.compareAndSet(cur, cur+1)) break }
    }

    fun get(): Int = value.get()
}

// ===== Задание 3: AtomicLong vs LongAdder benchmark =====

fun benchmarkAtomicVsAdder() {
    val threads = 10
    val iterations = 1_000_000L

    // TODO: Замерь время для AtomicLong.incrementAndGet() и LongAdder.increment()
    // Используй System.nanoTime()
    // Напечатай результаты
    println("TODO: implement benchmark")
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
