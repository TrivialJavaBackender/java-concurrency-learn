package exercises

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 16: ThreadPoolExecutor — глубокое погружение
 *
 * Задание 1: Создай ThreadPoolExecutor и наблюдай за его поведением:
 *            - corePoolSize=2, maxPoolSize=4, queue=ArrayBlockingQueue(3)
 *            - Что происходит с задачами 1-2, 3-5, 6-7, 8+?
 *            - Используй CallerRunsPolicy и наблюдай когда она срабатывает
 *
 * Задание 2: Реализуй NamedThreadFactory — потоки с именем "$prefix-worker-N",
 *            daemon=false, uncaughtExceptionHandler.
 *
 * Задание 3: Реализуй RetryRejectionHandler — сохраняет отклонённые задачи,
 *            retryAll() повторно отправляет их когда пул освободится.
 *
 * Задание 4: Graceful shutdown — корректное завершение пула:
 *            shutdown() → awaitTermination(5s) → если timeout: shutdownNow()
 *
 * Задание 5: invokeAll vs invokeAny vs ExecutorCompletionService.
 *            Покажи разницу поведения на 5 задачах с разным временем выполнения.
 *            Подумай: когда использовать каждый вариант?
 */

// ===== Задание 1: ThreadPoolExecutor наблюдение =====

fun task1_observeThreadPool() {
    val activeCount = AtomicInteger(0)

    // TODO: Создай ThreadPoolExecutor (core=2, max=4, queue=ArrayBlockingQueue(3), CallerRunsPolicy)
    // Submit 10 задач (каждая sleep 2000ms), логируй имя потока и активное количество
    // Наблюдай: какие задачи идут в core, какие в очередь, какие создают новые потоки, какие в caller

    println("ThreadPool observation demo")
}

// ===== Задание 2: Custom ThreadFactory =====

class NamedThreadFactory(private val prefix: String) : ThreadFactory {
    private val counter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        // TODO: Создай поток с именем "$prefix-worker-N"
        // Установи daemon=false и uncaughtExceptionHandler (логирует ошибку)
        return Thread(r) // placeholder
    }
}

fun task2_customThreadFactory() {
    // TODO: Создай FixedThreadPool(3) с NamedThreadFactory("my-pool")
    // Submit 5 задач, каждая печатает Thread.currentThread().name
    // Ожидаемые имена: my-pool-worker-0, my-pool-worker-1, my-pool-worker-2

    println("Custom ThreadFactory demo")
}

// ===== Задание 3: Custom RejectionHandler =====

class RetryRejectionHandler : RejectedExecutionHandler {
    val rejected = ConcurrentLinkedQueue<Runnable>()

    override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
        // TODO: Сохрани задачу в rejected, залогируй факт отказа
    }

    fun retryAll(executor: ExecutorService) {
        // TODO: Повторно отправь все задачи из rejected в executor
    }
}

fun task3_customRejectionHandler() {
    val handler = RetryRejectionHandler()

    // TODO: Создай пул (core=1, max=1, queue=ArrayBlockingQueue(1)) с handler
    // Submit 5 задач (sleep 1000ms) — 3 будут отклонены
    // После завершения первых задач — retryAll()
    // Дождись всех

    println("Custom rejection handler demo")
}

// ===== Задание 4: Graceful Shutdown =====

fun task4_gracefulShutdown() {
    val executor = Executors.newFixedThreadPool(3)

    // TODO: Submit 10 задач (sleep 2000ms каждая)
    // shutdown() — больше не принимает новые
    // awaitTermination(5s) — если не завершились: shutdownNow()
    // Лог: сколько задач не успело запуститься

    executor.shutdown()
}

// ===== Задание 5: invokeAll vs invokeAny vs CompletionService =====

fun task5_invocationStrategies() {
    val executor = Executors.newFixedThreadPool(3)
    val tasks = (1..5).map { i ->
        Callable {
            val delay = (Math.random() * 2000).toLong()
            Thread.sleep(delay)
            "Result-$i (took ${delay}ms)"
        }
    }

    // TODO: invokeAll — ждёт ВСЕ, напечатай результаты
    // TODO: invokeAny — возвращает ПЕРВЫЙ успешный, напечатай его
    // TODO: ExecutorCompletionService — submit задачи, take() результаты по мере готовности
    // Подумай: в чём разница? Когда что использовать?

    executor.shutdown()
}

fun main() {
    println("=== Task 1: Observe ThreadPool ===")
    task1_observeThreadPool()

    println("\n=== Task 2: Custom ThreadFactory ===")
    task2_customThreadFactory()

    println("\n=== Task 3: Custom Rejection Handler ===")
    task3_customRejectionHandler()

    println("\n=== Task 4: Graceful Shutdown ===")
    task4_gracefulShutdown()

    println("\n=== Task 5: Invocation Strategies ===")
    task5_invocationStrategies()
}
