package exercises

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 16: ThreadPoolExecutor — глубокое погружение
 *
 * Задание 1: Создай ThreadPoolExecutor с bounded queue и наблюдай:
 *            - core threads создаются при submit()
 *            - задачи идут в очередь после заполнения core
 *            - max threads создаются после заполнения очереди
 *            - rejection policy срабатывает после заполнения max + queue
 *
 * Задание 2: Реализуй custom ThreadFactory с именованием и логированием.
 *
 * Задание 3: Реализуй custom RejectedExecutionHandler который логирует
 *            и сохраняет отклонённые задачи для повторной отправки.
 *
 * Задание 4: Покажи правильный graceful shutdown.
 *
 * Задание 5: invokeAll vs invokeAny vs ExecutorCompletionService
 */

// ===== Задание 1: Наблюдение за поведением ThreadPoolExecutor =====

fun task1_observeThreadPool() {
    val activeCount = AtomicInteger(0)

    // TODO: Создай ThreadPoolExecutor:
    //   corePoolSize = 2
    //   maxPoolSize = 4
    //   keepAliveTime = 5 seconds
    //   queue = ArrayBlockingQueue(3)
    //   rejectionPolicy = CallerRunsPolicy

    // TODO: Submit 10 задач, каждая:
    //   println("[${Thread.currentThread().name}] Task-$i started (active: ${activeCount.incrementAndGet()})")
    //   Thread.sleep(2000)
    //   println("[${Thread.currentThread().name}] Task-$i finished (active: ${activeCount.decrementAndGet()})")

    // Наблюдай:
    //   - Задачи 1-2: создаются core потоки
    //   - Задачи 3-5: идут в очередь (3 элемента)
    //   - Задачи 6-7: создаются дополнительные потоки (до max=4)
    //   - Задача 8+: CallerRunsPolicy — выполняется в main потоке

    println("ThreadPool observation demo")
}

// ===== Задание 2: Custom ThreadFactory =====

class NamedThreadFactory(private val prefix: String) : ThreadFactory {
    private val counter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        // TODO: Создай поток с именем "$prefix-worker-${counter.getAndIncrement()}"
        //   Установи daemon = false
        //   Установи uncaughtExceptionHandler
        //   return thread
        return Thread(r) // placeholder
    }
}

fun task2_customThreadFactory() {
    // TODO: Создай executor с NamedThreadFactory("my-pool")
    //   Submit 5 задач, каждая печатает Thread.currentThread().name
    //   Убедись что имена: my-pool-worker-0, my-pool-worker-1, ...

    println("Custom ThreadFactory demo")
}

// ===== Задание 3: Custom RejectionHandler =====

class RetryRejectionHandler : RejectedExecutionHandler {
    val rejected = ConcurrentLinkedQueue<Runnable>()

    override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
        // TODO: Сохрани задачу в rejected queue
        //   println("REJECTED: task saved for retry (total rejected: ${rejected.size})")
    }

    fun retryAll(executor: ExecutorService) {
        // TODO: Повторно отправь все отклонённые задачи
        //   while (rejected.isNotEmpty()) {
        //     executor.submit(rejected.poll())
        //   }
    }
}

fun task3_customRejectionHandler() {
    val handler = RetryRejectionHandler()

    // TODO: Создай executor с маленьким core=1, max=1, queue=1
    //   Submit 5 задач (каждая sleep 1000ms)
    //   3 задачи будут отклонены
    //   Подожди завершения текущих
    //   handler.retryAll(executor) — отправь отклонённые повторно

    println("Custom rejection handler demo")
}

// ===== Задание 4: Graceful Shutdown =====

fun task4_gracefulShutdown() {
    val executor = Executors.newFixedThreadPool(3)

    // TODO: Submit 10 долгих задач (каждая sleep 2000ms)
    //   println("Initiating shutdown...")
    //   executor.shutdown()  // Перестать принимать новые
    //   println("Awaiting termination...")
    //   if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
    //     println("Timeout! Forcing shutdown...")
    //     val notExecuted = executor.shutdownNow()  // Прервать текущие
    //     println("Tasks never started: ${notExecuted.size}")
    //     if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
    //       println("Pool did not terminate!")
    //     }
    //   }
    //   println("Shutdown complete")

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

    // TODO: invokeAll — ждёт ВСЕ, возвращает List<Future>
    //   println("=== invokeAll ===")
    //   val allResults = executor.invokeAll(tasks)
    //   allResults.forEach { println("  ${it.get()}") }
    //   // Все futures уже isDone()

    // TODO: invokeAny — возвращает ПЕРВЫЙ успешный
    //   println("=== invokeAny ===")
    //   val first = executor.invokeAny(tasks)
    //   println("  First: $first")

    // TODO: ExecutorCompletionService — результаты ПО МЕРЕ ГОТОВНОСТИ
    //   println("=== CompletionService ===")
    //   val ecs = ExecutorCompletionService<String>(executor)
    //   tasks.forEach { ecs.submit(it) }
    //   repeat(tasks.size) {
    //     val future = ecs.take()  // первый завершившийся
    //     println("  Completed: ${future.get()}")
    //   }

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
