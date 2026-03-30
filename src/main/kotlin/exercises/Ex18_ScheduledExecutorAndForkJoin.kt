package exercises

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * УПРАЖНЕНИЕ 18: ScheduledExecutorService + ForkJoinPool — глубокое погружение
 *
 * Задание 1: scheduleAtFixedRate vs scheduleWithFixedDelay
 *            Задача занимает случайное время (100-600ms), period/delay = 500ms.
 *            Покажи с timestamp когда запускается каждая итерация.
 *            Ключевой вопрос: что происходит если задача длиннее периода?
 *
 * Задание 2: Реализуй SimpleRateLimiter на основе Semaphore + ScheduledExecutor.
 *            Каждую секунду пополняет permits до permitsPerSecond.
 *            20 задач, не больше 5 ops/sec.
 *
 * Задание 3: ForkJoinPool — параллельный поиск элемента в массиве.
 *            При размере <= THRESHOLD — линейный поиск.
 *            Иначе — fork левую, compute правую, выбрать найденный.
 *
 * Задание 4: ForkJoinPool — map-reduce для подсчёта среднего.
 *            RecursiveTask возвращает Pair(sum, count).
 *            Сравни с array.average().
 *
 * Задание 5: Покажи проблему с commonPool() и блокирующими операциями.
 *            Запусти parallelism*2 задач с Thread.sleep() в commonPool.
 *            Почему это плохо? Как исправить?
 */

// ===== Задание 1: Rate vs Delay =====

fun task1_rateVsDelay() {
    val scheduler = Executors.newScheduledThreadPool(2)
    val startTime = System.currentTimeMillis()
    fun elapsed() = System.currentTimeMillis() - startTime

    // TODO: scheduleAtFixedRate — запуск каждые 500ms (фиксированная точка по часам)
    // Каждая итерация: логируй elapsed(), затем sleep рандомное 100-600ms
    // Что происходит если sleep > 500ms?

    // TODO: scheduleWithFixedDelay — 500ms ПОСЛЕ завершения предыдущей
    // Та же логика, другое поведение

    // Запусти 5 секунд, cancel оба, shutdown

    println("Rate vs Delay demo")
}

// ===== Задание 2: Rate Limiter =====

class SimpleRateLimiter(permitsPerSecond: Int) {
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val semaphore = Semaphore(permitsPerSecond)

    init {
        // TODO: Каждую секунду пополняй semaphore до permitsPerSecond
        // Подсказка: сколько нужно release()? (permitsPerSecond - semaphore.availablePermits())
    }

    fun acquire() {
        // TODO: Блокирует если лимит исчерпан
    }

    fun shutdown() {
        scheduler.shutdown()
    }
}

fun task2_rateLimiter() {
    // TODO: Создай rate limiter на 5 ops/sec
    // 20 задач — каждая acquire() + логируй время + "работает"
    // Убедись что в каждую секунду выполняется не больше 5 задач

    println("Rate limiter demo")
}

// ===== Задание 3: Parallel Search =====

class ParallelSearch(
    private val array: IntArray,
    private val target: Int,
    private val lo: Int,
    private val hi: Int
) : RecursiveTask<Int>() {

    companion object {
        const val THRESHOLD = 1000
    }

    override fun compute(): Int {
        // TODO: При hi-lo <= THRESHOLD — линейный поиск в диапазоне, вернуть индекс или -1
        // Иначе: fork левую половину, compute правую, join левую
        // Вернуть первый найденный (не -1)
        return -1
    }
}

fun task3_parallelSearch() {
    val size = 1_000_000
    val array = IntArray(size) { it * 2 }  // чётные числа 0, 2, 4, ...
    val target = 777_776

    // TODO: Используй ForkJoinPool для поиска
    // Напечатай индекс и verify: array[index] == target

    println("Parallel search demo")
}

// ===== Задание 4: Map-Reduce Average =====

class AverageTask(
    private val array: DoubleArray,
    private val lo: Int,
    private val hi: Int
) : RecursiveTask<Pair<Double, Int>>() {

    companion object {
        const val THRESHOLD = 5000
    }

    override fun compute(): Pair<Double, Int> {
        // TODO: При hi-lo <= THRESHOLD — суммируй линейно, верни Pair(sum, count)
        // Иначе: fork/compute/join, объедини пары через сложение
        return Pair(0.0, 0)
    }
}

fun task4_mapReduceAverage() {
    val size = 1_000_000
    val array = DoubleArray(size) { Math.random() * 100 }

    // TODO: ForkJoinPool.invoke(AverageTask(...)), вычисли среднее
    // Сравни с array.average() — должны совпасть

    println("Map-reduce average demo")
}

// ===== Задание 5: commonPool blocking problem =====

fun task5_commonPoolProblem() {
    // TODO: Узнай parallelism у ForkJoinPool.commonPool()
    // Запусти parallelism*2 задач через CompletableFuture.supplyAsync (использует commonPool)
    // Каждая задача: Thread.sleep(2000)
    // Замерь общее время — почему оно ~4s а не ~2s?
    // Исправь: создай собственный Executors.newFixedThreadPool(20) и передай его в supplyAsync

    println("commonPool blocking demo")
}

fun main() {
    println("=== Task 1: Rate vs Delay ===")
    task1_rateVsDelay()

    println("\n=== Task 2: Rate Limiter ===")
    task2_rateLimiter()

    println("\n=== Task 3: Parallel Search ===")
    task3_parallelSearch()

    println("\n=== Task 4: Map-Reduce Average ===")
    task4_mapReduceAverage()

    println("\n=== Task 5: commonPool Blocking ===")
    task5_commonPoolProblem()
}
