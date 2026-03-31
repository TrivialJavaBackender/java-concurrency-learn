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
    // TODO: Создай 5 потоков, каждый печатает "Thread-{name}: iteration {i}"
    // Подсказка: Thread { ... }.also { it.name = "Worker-$n" }.start()
}

// ===== Задание 2 =====

class SafeCounter {
    var count = 0
        private set

    fun increment() {
        // TODO: Сделай потокобезопасным с помощью synchronized
        count++
    }
}

fun task2_safeIncrement(): Int {
    val counter = SafeCounter()
    // TODO: Запусти 10 потоков, каждый вызывает counter.increment() 100_000 раз
    // TODO: Дождись завершения всех потоков (join)
    // TODO: Верни counter.count — должно быть 1_000_000
    return counter.count
}

// ===== Задание 3 =====

class PingPong {
    private val lock = Object()
    private var isPing = true

    fun ping() {
        // TODO: 10 раз: дождись своей очереди (isPing == true), напечатай "ping", передай ход
    }

    fun pong() {
        // TODO: 10 раз: дождись своей очереди (isPing == false), напечатай "pong", передай ход
    }
}

fun task3_pingPong() {
    val pp = PingPong()
    // TODO: Запусти ping() и pong() в двух потоках
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
