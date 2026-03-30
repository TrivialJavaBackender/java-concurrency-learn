package exercises

/**
 * УПРАЖНЕНИЕ 12: Virtual Threads (Java 21+)
 *
 * Задание 1: Создай 100_000 виртуальных потоков, каждый делает Thread.sleep(1000).
 *            Замерь время. Сравни с тем же количеством platform threads (спойлер: не делай это).
 *
 * Задание 2: Реализуй HTTP-подобный "сервер":
 *            - Принимает "запросы" из очереди
 *            - Каждый запрос обрабатывается в виртуальном потоке
 *            - Обработка включает "блокирующий I/O" (Thread.sleep)
 *
 * Задание 3: Покажи проблему с pinning — виртуальный поток внутри synchronized
 *            не отпускает carrier thread. Используй ReentrantLock вместо этого.
 *
 * ВАЖНО: Требует Java 21+. Если используешь более раннюю версию, пропусти это упражнение.
 */

fun task1_massiveVirtualThreads() {
    // TODO:
    // val start = System.nanoTime()
    // val threads = (1..100_000).map {
    //     Thread.ofVirtual().start { Thread.sleep(1000) }
    // }
    // threads.forEach { it.join() }
    // println("100K virtual threads with sleep(1s): ${(System.nanoTime()-start)/1_000_000}ms")
    println("TODO: Requires Java 21+")
}

fun task2_virtualThreadServer() {
    // TODO:
    // val executor = Executors.newVirtualThreadPerTaskExecutor()
    // val requests = (1..1000).map { "request-$it" }
    //
    // val futures = requests.map { req ->
    //     executor.submit<String> {
    //         Thread.sleep(100) // simulate I/O
    //         "response for $req"
    //     }
    // }
    //
    // val responses = futures.map { it.get() }
    // println("Processed ${responses.size} requests")
    // executor.shutdown()
    println("TODO: Requires Java 21+")
}

fun task3_pinningProblem() {
    // TODO: Покажи что synchronized блокирует carrier thread:
    //
    // val lock = Object()
    // val threads = (1..100).map {
    //     Thread.ofVirtual().start {
    //         synchronized(lock) {   // BAD: pins virtual thread to carrier
    //             Thread.sleep(100)
    //         }
    //     }
    // }
    //
    // Исправь на ReentrantLock:
    // val rlock = ReentrantLock()
    // Thread.ofVirtual().start {
    //     rlock.withLock {  // GOOD: virtual thread can unmount
    //         Thread.sleep(100)
    //     }
    // }
    println("TODO: Requires Java 21+")
}

fun main() {
    println("=== Virtual Threads (Java 21+) ===")
    task1_massiveVirtualThreads()
    task2_virtualThreadServer()
    task3_pinningProblem()
}
