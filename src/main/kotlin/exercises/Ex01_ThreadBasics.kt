package exercises

/**
 * УПРАЖНЕНИЕ 1: Основы потоков
 *
 * Задание 1: Создай 5 потоков, каждый из которых печатает своё имя и номер 10 раз.
 *            Убедись, что вывод перемешивается (потоки работают параллельно).
 *
 * Задание 2: Реализуй метод safeIncrement(), который инкрементирует общий счётчик
 *            из 10 потоков по 100_000 раз каждый. Результат должен быть ровно 1_000_000.
 *            Используй synchronized.
 *
 * Задание 3: Реализуй "пинг-понг" — два потока поочерёдно печатают "ping" и "pong"
 *            используя wait/notify. Всего 10 обменов.
 */

// ===== Задание 1 =====

fun task1_parallelPrint() {
    val threads = (1..5).map {
        Thread {
            repeat(10) {
                println("Thread ${Thread.currentThread().name} - $it")
            }
        }
    }.onEach { it.start() }

    threads.forEach { it.join() }

}

// ===== Задание 2 =====

class SafeCounter {
    var count = 0
        private set

    fun increment() {
        synchronized(this) {
            count++
        }
    }
}

fun task2_safeIncrement(): Int {
    val counter = SafeCounter()

    val threads = (1..10).map {
        Thread {
            repeat(100_000) {
                counter.increment()
            }
        }
    }.onEach { it.start() }

    threads.forEach { it.join() }

    return counter.count
}

// ===== Задание 3 =====

class PingPong {
    private val lock = Object()
    private var isPing = true

    fun ping() {
        synchronized(lock) {
            while (!isPing) {
                lock.wait()
            }
            isPing = false
            println("ping")
            lock.notifyAll()
        }
    }

    fun pong() {
        synchronized(lock) {
            while (isPing) {
                lock.wait()
            }
            isPing = true
            println("pong")
            lock.notifyAll()
        }
    }
}

fun task3_pingPong() {
    val pp = PingPong()

    val t1 = Thread { repeat(10) { pp.ping() } }
    val t2 = Thread { repeat(10) { pp.pong() } }

    t1.start()
    t2.start()

    t1.join()
    t2.join()
}

fun main() {
    println("=== Task 1: Parallel Print ===")
    task1_parallelPrint()

    println("\n=== Task 2: Safe Increment ===")
    val result = task2_safeIncrement()
    println("Counter = $result (expected 1000000)")

    println("\n=== Task 3: Ping Pong ===")
    task3_pingPong()
}
