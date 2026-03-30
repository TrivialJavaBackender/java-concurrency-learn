package exercises

import java.lang.management.ManagementFactory

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
            // TODO: synchronized(lock2) { println("Thread1: holds both locks") }
        }
    }, "Thread-1")

    val thread2 = Thread({
        synchronized(lock2) {
            println("Thread2: holds lock2")
            Thread.sleep(100)
            // TODO: synchronized(lock1) { println("Thread2: holds both locks") }
        }
    }, "Thread-2")

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

    // TODO: Оба потока захватывают блокировки В ОДНОМ ПОРЯДКЕ: lock1 → lock2
    // Это предотвращает circular wait
}

fun task2b_fixWithTryLock() {
    // TODO: Используй ReentrantLock + tryLock(1, TimeUnit.SECONDS)
    // Если не удалось захватить — отпусти все блокировки и попробуй снова
}

// ===== Задание 3: 3-thread circular deadlock =====

fun task3_circularDeadlock() {
    // TODO: 3 объекта A, B, C
    // Thread1: lock A → lock B
    // Thread2: lock B → lock C
    // Thread3: lock C → lock A
    // Обнаружь deadlock через ThreadMXBean
}

fun main() {
    println("=== Task 1: Deadlock Detection ===")
    task1_createDeadlock()

    // Раскомментируй после реализации:
    // println("\n=== Task 2a: Fix with Lock Ordering ===")
    // task2a_fixWithLockOrdering()
    //
    // println("\n=== Task 2b: Fix with TryLock ===")
    // task2b_fixWithTryLock()
    //
    // println("\n=== Task 3: Circular Deadlock ===")
    // task3_circularDeadlock()
}
