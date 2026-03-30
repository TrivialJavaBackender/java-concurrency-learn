package exercises

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * УПРАЖНЕНИЕ 18: ScheduledExecutorService + ForkJoinPool — глубокое погружение
 *
 * Задание 1: scheduleAtFixedRate vs scheduleWithFixedDelay
 *            Визуализируй разницу: задача занимает случайное время (100-600ms),
 *            period/delay = 500ms. Покажи когда запускается каждая итерация.
 *
 * Задание 2: Реализуй rate limiter на ScheduledExecutorService.
 *            Максимум N операций в секунду. Задачи сверх лимита откладываются.
 *
 * Задание 3: ForkJoinPool — параллельный поиск в отсортированном массиве.
 *            Разбей массив на подзадачи, каждая ищет в своём диапазоне.
 *
 * Задание 4: ForkJoinPool — параллельный map-reduce.
 *            Подсчитай среднее значение большого массива через fork/join.
 *
 * Задание 5: Покажи проблему с commonPool() и блокирующими операциями.
 */

// ===== Задание 1: Rate vs Delay =====

fun task1_rateVsDelay() {
    val scheduler = Executors.newScheduledThreadPool(2)
    val startTime = System.currentTimeMillis()
    val iteration = AtomicInteger(0)

    fun elapsed() = System.currentTimeMillis() - startTime

    // TODO: scheduleAtFixedRate — запуск каждые 500ms (по часам)
    //   val rateTask = scheduler.scheduleAtFixedRate({
    //       val i = iteration.incrementAndGet()
    //       println("[Rate] Iteration $i at ${elapsed()}ms")
    //       Thread.sleep((100..600).random().toLong())  // задача дольше периода?
    //   }, 0, 500, TimeUnit.MILLISECONDS)

    // TODO: scheduleWithFixedDelay — 500ms ПОСЛЕ завершения предыдущей
    //   val delayIteration = AtomicInteger(0)
    //   val delayTask = scheduler.scheduleWithFixedDelay({
    //       val i = delayIteration.incrementAndGet()
    //       println("[Delay] Iteration $i at ${elapsed()}ms")
    //       Thread.sleep((100..600).random().toLong())
    //   }, 0, 500, TimeUnit.MILLISECONDS)

    // Thread.sleep(5000)
    // rateTask.cancel(false)
    // delayTask.cancel(false)
    // scheduler.shutdown()

    println("Rate vs Delay demo")
}

// ===== Задание 2: Rate Limiter =====

class SimpleRateLimiter(permitsPerSecond: Int) {
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val semaphore = Semaphore(permitsPerSecond)

    init {
        // TODO: Каждую секунду пополняй permits
        //   scheduler.scheduleAtFixedRate({
        //       val toRelease = permitsPerSecond - semaphore.availablePermits()
        //       if (toRelease > 0) semaphore.release(toRelease)
        //   }, 1, 1, TimeUnit.SECONDS)
    }

    fun acquire() {
        // TODO: semaphore.acquire() — блокируется если лимит исчерпан
    }

    fun shutdown() {
        scheduler.shutdown()
    }
}

fun task2_rateLimiter() {
    // TODO: Создай rate limiter на 5 ops/sec
    //   Запусти 20 задач, каждая делает rateLimiter.acquire() перед работой
    //   Покажи что не больше 5 задач выполняются в секунду

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
        // TODO:
        // if (hi - lo <= THRESHOLD) {
        //     // Линейный поиск в диапазоне
        //     for (i in lo until hi) {
        //         if (array[i] == target) return i
        //     }
        //     return -1
        // }
        // val mid = (lo + hi) / 2
        // val left = ParallelSearch(array, target, lo, mid)
        // val right = ParallelSearch(array, target, mid, hi)
        // left.fork()
        // val rightResult = right.compute()
        // val leftResult = left.join()
        // return if (leftResult != -1) leftResult else rightResult

        return -1 // placeholder
    }
}

fun task3_parallelSearch() {
    val size = 1_000_000
    val array = IntArray(size) { it * 2 }  // чётные числа
    val target = 777_776

    // TODO:
    // val pool = ForkJoinPool()
    // val index = pool.invoke(ParallelSearch(array, target, 0, array.size))
    // println("Found $target at index $index")
    // println("Verify: array[$index] = ${array[index]}")

    println("Parallel search demo")
}

// ===== Задание 4: Map-Reduce Average =====

class AverageTask(
    private val array: DoubleArray,
    private val lo: Int,
    private val hi: Int
) : RecursiveTask<Pair<Double, Int>>() { // (sum, count)

    companion object {
        const val THRESHOLD = 5000
    }

    override fun compute(): Pair<Double, Int> {
        // TODO:
        // if (hi - lo <= THRESHOLD) {
        //     var sum = 0.0
        //     for (i in lo until hi) sum += array[i]
        //     return Pair(sum, hi - lo)
        // }
        // val mid = (lo + hi) / 2
        // val left = AverageTask(array, lo, mid)
        // val right = AverageTask(array, mid, hi)
        // left.fork()
        // val (rSum, rCount) = right.compute()
        // val (lSum, lCount) = left.join()
        // return Pair(lSum + rSum, lCount + rCount)

        return Pair(0.0, 0) // placeholder
    }
}

fun task4_mapReduceAverage() {
    val size = 1_000_000
    val array = DoubleArray(size) { Math.random() * 100 }

    // TODO:
    // val pool = ForkJoinPool()
    // val (sum, count) = pool.invoke(AverageTask(array, 0, array.size))
    // val average = sum / count
    // println("ForkJoin average: $average")
    // println("Direct average: ${array.average()}")

    println("Map-reduce average demo")
}

// ===== Задание 5: commonPool blocking problem =====

fun task5_commonPoolProblem() {
    // TODO: Покажи проблему:
    //   val parallelism = ForkJoinPool.commonPool().parallelism
    //   println("Common pool parallelism: $parallelism")

    //   // Запусти parallelism блокирующих задач в commonPool
    //   val futures = (1..parallelism * 2).map {
    //       CompletableFuture.supplyAsync {
    //           println("Task-$it on ${Thread.currentThread().name}")
    //           Thread.sleep(2000)  // Блокирует carrier!
    //           "Result-$it"
    //       }
    //   }
    //   // Все задачи завершатся, но первые parallelism заблокируют пул
    //   // Остальные будут ждать. Общее время ≈ 4 секунды вместо 2.

    //   // Решение: свой executor
    //   val myPool = Executors.newFixedThreadPool(20)
    //   CompletableFuture.supplyAsync({ blockingWork() }, myPool)

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
