package exercises

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * УПРАЖНЕНИЕ 16: Виды thread pools — различия, опасности, выбор
 *
 * Задание 1: Наблюдение за поведением всех pool-типов
 *   Запусти одни и те же 20 задач (каждая sleep 500ms) на каждом пуле:
 *   - newFixedThreadPool(4)
 *   - newCachedThreadPool()
 *   - newSingleThreadExecutor()
 *   - newWorkStealingPool()
 *   Для каждого: замерь общее время и залогируй имена потоков.
 *   Вопросы: почему CachedPool быстрее Fixed при burst-нагрузке?
 *            почему SingleThread гарантирует порядок, а WorkStealing — нет?
 *
 * Задание 2: Опасности каждого пула
 *   a) newCachedThreadPool + 10000 задач по 10s — что происходит с памятью/потоками?
 *      Наблюдай через Thread.activeCount(). Почему это опасно?
 *   b) newFixedThreadPool(4) + медленные задачи — submit 10000 запросов.
 *      Что происходит с очередью? Как это приводит к OOM?
 *      Используй ThreadPoolExecutor.getQueue().size() для мониторинга.
 *   c) Исправь каждый вариант: CachedPool → ограничь через Semaphore;
 *      FixedPool → замени unbounded queue на ArrayBlockingQueue.
 *
 * Задание 3: ThreadPoolExecutor — ручная настройка
 *   Создай пул: core=2, max=6, keepAlive=30s, queue=ArrayBlockingQueue(10).
 *   Submit 20 задач и логируй для каждой: что произошло?
 *   - задачи 1-2: идут в core threads
 *   - задачи 3-12: идут в очередь (queue.size растёт)
 *   - задачи 13-18: создаются новые потоки (core..max)
 *   - задачи 19-20: RejectionHandler срабатывает
 *   Подбери правильные задержки и размеры чтобы увидеть все 4 стадии.
 *
 * Задание 4: Rejection Policies — сравни все четыре
 *   Используй маленький пул (core=1, max=1, queue=ArrayBlockingQueue(1)).
 *   Submit 5 задач и наблюдай поведение каждой политики:
 *   - AbortPolicy (по умолчанию): бросает RejectedExecutionException
 *   - CallerRunsPolicy: задача выполняется в вызывающем потоке (замедляет producer)
 *   - DiscardPolicy: молча выбрасывает задачу
 *   - DiscardOldestPolicy: выбрасывает самую старую из очереди, добавляет новую
 *   Напечатай в каком потоке выполнилась каждая задача.
 *
 * Задание 5: invokeAll vs invokeAny vs ExecutorCompletionService
 *   5 задач с разным временем выполнения (100ms, 500ms, 200ms, 800ms, 50ms).
 *   - invokeAll: ждёт все, верни список результатов в порядке submit
 *   - invokeAny: верни результат самой быстрой, остальные отмени
 *   - CompletionService: обрабатывай результаты по мере готовности (FIFO by completion)
 *   Замерь общее время каждого подхода. Когда что использовать?
 *
 * Задание 6: Custom ThreadFactory + Graceful Shutdown
 *   Реализуй NamedThreadFactory: имя "$prefix-worker-N", daemon=false,
 *   uncaughtExceptionHandler (логирует ошибку и имя потока).
 *   Graceful shutdown: shutdown() → awaitTermination(5s) → если не завершился: shutdownNow().
 *   Напечатай сколько задач не успело запуститься.
 */

// ===== Задание 1: Поведение всех pool-типов =====

fun task1_allPoolTypes() {
    val taskCount = 20
    val taskDuration = 500L

    fun runTasks(name: String, executor: ExecutorService) {
        val start = System.currentTimeMillis()
        val futures = (1..taskCount).map { i ->
            executor.submit(Callable {
                Thread.sleep(taskDuration)
                Thread.currentThread().name
            })
        }
        val threadNames = futures.map { it.get() }.toSet()
        val elapsed = System.currentTimeMillis() - start
        println("[$name] time=${elapsed}ms, unique threads=${threadNames.size}")
        executor.shutdown()
    }

    // TODO: Запусти runTasks для каждого из 4 стандартных типов ExecutorService
    // Сравни время выполнения и количество используемых потоков
    // Для SingleThread: проверь порядок выполнения задач
    // Для WorkStealing: объясни почему порядок не гарантирован
}

// ===== Задание 2: Опасности пулов =====

fun task2_dangers() {
    println("--- 2a: CachedPool thread explosion ---")
    // TODO: Создай newCachedThreadPool(), submit 200 задач по 5 секунд каждая
    // Мониторь количество активных потоков каждую секунду
    // Остановись через 3 секунды (не жди завершения)
    // Что происходит с количеством потоков? Почему это опасно в production?

    println("--- 2b: FixedPool queue growth ---")
    // TODO: Создай newFixedThreadPool(2), submit 100 задач по 1 секунде
    // Мониторь размер внутренней очереди каждые 100ms
    // Остановись через 2 секунды
    // Что случится с памятью если задач будут миллионы?

    println("--- 2c: Fix с ограниченной очередью ---")
    // TODO: Создай пул с ограниченной очередью и политикой отклонения
    // Submit те же 100 задач — что изменилось?
}

// ===== Задание 3: ThreadPoolExecutor — 4 стадии =====

fun task3_threadPoolExecutor() {
    // TODO: Создай ThreadPoolExecutor(core=2, max=6, keepAlive=30s, queue=ArrayBlockingQueue(10))
    // Submit 20 задач (sleep 2000ms каждая) и логируй состояние пула после каждого submit
    // Должны быть видны все 4 стадии: core → queue → max → rejection

    println("ThreadPoolExecutor stages demo")
}

// ===== Задание 4: Rejection Policies =====

fun task4_rejectionPolicies() {
    val policies = listOf(
        "AbortPolicy" to ThreadPoolExecutor.AbortPolicy(),
        "CallerRunsPolicy" to ThreadPoolExecutor.CallerRunsPolicy(),
        "DiscardPolicy" to ThreadPoolExecutor.DiscardPolicy(),
        "DiscardOldestPolicy" to ThreadPoolExecutor.DiscardOldestPolicy(),
    )

    for ((name, policy) in policies) {
        println("\n--- $name ---")
        // TODO: Создай ThreadPoolExecutor(1, 1, 0s, ArrayBlockingQueue(1), policy)
        // Submit 5 задач (sleep 1000ms каждая), для каждой:
        //   - обработай RejectedExecutionException (для AbortPolicy)
        //   - логируй в каком потоке выполнилась задача
        // shutdown и awaitTermination
    }
}

// ===== Задание 5: invokeAll vs invokeAny vs CompletionService =====

fun task5_invocationStrategies() {
    val executor = Executors.newFixedThreadPool(5)
    val delays = listOf(100L, 500L, 200L, 800L, 50L)
    val tasks = delays.mapIndexed { i, delay ->
        Callable {
            Thread.sleep(delay)
            "Result-${i + 1} (${delay}ms)"
        }
    }

    // TODO: invokeAll — ждёт все, результаты в порядке submit
    // Замерь время: должно быть ~800ms (ждёт самую медленную)

    // TODO: invokeAny — возвращает первый успешный (~50ms), остальные отменяет

    // TODO: ExecutorCompletionService — submit все, take() результаты по мере готовности
    // Порядок: Result-5 (50ms), Result-1 (100ms), Result-3 (200ms), ...

    executor.shutdown()
}

// ===== Задание 6: Custom ThreadFactory + Graceful Shutdown =====

class NamedThreadFactory(private val prefix: String) : ThreadFactory {
    private val counter = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread {
        // TODO: задай имя потоку, daemon=false, настрой обработчик непойманных исключений
        return Thread(r) // placeholder
    }
}

fun task6_shutdownAndFactory() {
    // TODO: Создай FixedThreadPool(3) с NamedThreadFactory("my-pool")
    // Submit 10 задач (sleep 1000ms каждая)
    // Через 2 секунды начни graceful shutdown
    // Напечатай сколько задач не успело запуститься

    println("Graceful shutdown demo")
}

fun main() {
    println("=== Task 1: All Pool Types ===")
    task1_allPoolTypes()

    println("\n=== Task 2: Dangers ===")
    task2_dangers()

    println("\n=== Task 3: ThreadPoolExecutor Stages ===")
    task3_threadPoolExecutor()

    println("\n=== Task 4: Rejection Policies ===")
    task4_rejectionPolicies()

    println("\n=== Task 5: Invocation Strategies ===")
    task5_invocationStrategies()

    println("\n=== Task 6: ThreadFactory + Shutdown ===")
    task6_shutdownAndFactory()
}
