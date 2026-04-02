package exercises

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

/**
 * УПРАЖНЕНИЕ 11: Deadlock — создание, обнаружение, предотвращение
 *
 * Задание 1: Создай классический deadlock двух потоков (lock ordering problem).
 *            Используй ThreadMXBean для обнаружения deadlock'а.
 *
 * Задание 2: Исправь deadlock из задания 1 с помощью:
 *            a) Упорядочивания блокировок (lock ordering)
 *            b) tryLock с таймаутом
 *
 * Задание 3: Создай deadlock из 3 потоков (circular wait) и обнаружь его.
 */

// ===== Задание 1: Create & Detect Deadlock =====

fun task1_createDeadlock() {
    val lock1 = Object()
    val lock2 = Object()

    val thread1 = Thread({
        synchronized(lock1) {
            println("Thread1: holds lock1")
            Thread.sleep(100)
            synchronized(lock2) {
                println("Thread1: holds lock2")
            }
        }
    }, "Thread-1")

    val thread2 = Thread({
        synchronized(lock2) {
            println("Thread2: holds lock2")
            Thread.sleep(100)
            synchronized(lock1) {
                println("Thread2: holds lock1")
            }
        }
    }, "Thread-2")

    thread1.isDaemon = true
    thread2.isDaemon = true

    thread1.start()
    thread2.start()

    // Detect deadlock
    Thread.sleep(1000)
    val mxBean = ManagementFactory.getThreadMXBean()
    val deadlockedThreads = mxBean.findDeadlockedThreads()
    if (deadlockedThreads != null) {
        println("DEADLOCK DETECTED!")
        for (id in deadlockedThreads) {
            val info = mxBean.getThreadInfo(id)
            println("  ${info.threadName}: blocked on ${info.lockName}, held by ${info.lockOwnerName}")
        }
    }

    // Не ждём завершения — потоки в deadlock'е навсегда
}

// ===== Задание 2: Fix Deadlock =====

fun task2a_fixWithLockOrdering() {
    val lock1 = Object()
    val lock2 = Object()

    val thread1 = Thread({
        synchronized(lock1) {
            println("Thread1: holds lock1")
            Thread.sleep(100)
            synchronized(lock2) {
                println("Thread1: holds lock2")
            }
        }
    }, "Thread-1")

    val thread2 = Thread({
        synchronized(lock1) {
            println("Thread2: holds lock1")
            Thread.sleep(100)
            synchronized(lock2) {
                println("Thread2: holds lock2")
            }
        }
    }, "Thread-2")

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()
}

fun randomTimeout(): Long = (Math.random() * 300 + 150).toLong()

fun task2b_fixWithTryLock() {

    val lock1 = ReentrantLock()
    val lock2 = ReentrantLock()

    val thread1 = Thread({
        val finished = AtomicBoolean(false)
        while (!finished.get()) {
            if (lock1.tryLock(randomTimeout(), TimeUnit.MILLISECONDS)) {
                try {
                    println("Thread1: holds lock1")
                    Thread.sleep(100)
                    if (lock2.tryLock(randomTimeout(), TimeUnit.MILLISECONDS)) {
                        try {
                            println("Thread1: holds lock2")
                            finished.set(true)
                        } finally {
                            println("Thread1: unlock lock2")
                            lock2.unlock()
                        }
                    }
                } finally {
                    println("Thread1: unlock lock1")
                    lock1.unlock()
                }
            }
        }

    }, "Thread-1")

    val thread2 = Thread({
        val finished = AtomicBoolean(false)
        while (!finished.get()) {
            if (lock2.tryLock(randomTimeout(), TimeUnit.MILLISECONDS)) {
                try {
                    println("Thread2: holds lock2")
                    Thread.sleep(100)
                    if (lock1.tryLock(randomTimeout(), TimeUnit.MILLISECONDS)) {
                        try {
                            println("Thread2: holds lock1")
                            finished.set(true)
                        } finally {
                            println("Thread2: unlock lock1")
                            lock1.unlock()
                        }
                    }
                } finally {
                    println("Thread2: unlock lock2")
                    lock2.unlock()
                }
            }
        }

    }, "Thread-2")

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()
}

// ===== Задание 3: 3-thread circular deadlock =====

fun task3_circularDeadlock() {
    // TODO: 3 объекта A, B, C
    // Thread1: lock A → lock B
    // Thread2: lock B → lock C
    // Thread3: lock C → lock A
    // Обнаружь deadlock через ThreadMXBean

    val lockA = Object()
    val lockB = Object()
    val lockC = Object()

    val thread1 = Thread({
        synchronized(lockA) {
            println("ThreadA: holds lockA")
            Thread.sleep(100)
            synchronized(lockB) {
                println("ThreadA: holds lockB")
            }
        }
    }, "Thread-A")

    val thread2 = Thread({
        synchronized(lockB) {
            println("ThreadB: holds lockB")
            Thread.sleep(100)
            synchronized(lockC) {
                println("ThreadB: holds lockС")
            }
        }
    }, "Thread-B")

    val thread3 = Thread({
        synchronized(lockC) {
            println("ThreadC: holds lockC")
            Thread.sleep(100)
            synchronized(lockA) {
                println("ThreadC: holds lockA")
            }
        }
    }, "Thread-C")


    thread1.isDaemon = true
    thread2.isDaemon = true
    thread3.isDaemon = true

    thread1.start()
    thread2.start()
    thread3.start()

    // Detect deadlock
    Thread.sleep(1000)
    val mxBean = ManagementFactory.getThreadMXBean()
    val deadlockedThreads = mxBean.findDeadlockedThreads().map { mxBean.getThreadInfo(it) }
        .filter { it.threadName in setOf("Thread-A", "Thread-B", "Thread-C") }
    if (deadlockedThreads.isNotEmpty()) {
        println("DEADLOCK DETECTED!")
        for (info in deadlockedThreads) {
            println("  ${info.threadName}: blocked on ${info.lockName}, held by ${info.lockOwnerName}")
        }
    }
}

fun main() {
    println("=== Task 1: Deadlock Detection ===")
    task1_createDeadlock()

//     Раскомментируй после реализации:
    println("\n=== Task 2a: Fix with Lock Ordering ===")
    task2a_fixWithLockOrdering()

    println("\n=== Task 2b: Fix with TryLock ===")
    task2b_fixWithTryLock()

    println("\n=== Task 3: Circular Deadlock ===")
    task3_circularDeadlock()
}
