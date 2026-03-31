package exercises

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask

/**
 * УПРАЖНЕНИЕ 9: ForkJoinPool и RecursiveTask
 *
 * Задание 1: Реализуй параллельный Merge Sort через RecursiveTask.
 *            - Если массив <= THRESHOLD (16 элементов), сортируй обычным sort()
 *            - Иначе разбей на 2 половины: fork() одну, compute() другую, join() первую
 *            - merge() уже реализован — используй его
 *
 * Задание 2: Реализуй параллельный поиск максимума в массиве через RecursiveTask.
 *            Аналогичная структура: при малом размере — линейный поиск,
 *            иначе — рекурсивное разбиение.
 *
 * Покажи ускорение на массиве из 1_000_000 элементов.
 */

class MergeSortTask(
    private val array: IntArray,
    private val left: Int,
    private val right: Int
) : RecursiveTask<IntArray>() {

    companion object {
        const val THRESHOLD = 16
    }

    override fun compute(): IntArray {
        // TODO: Реализуй рекурсивный merge sort через fork/join
        // При right - left <= THRESHOLD — сортируй напрямую
        // Иначе — разбей на 2 половины, объедини результаты через merge()
        return array.copyOfRange(left, right).also { it.sort() } // placeholder
    }

    private fun merge(a: IntArray, b: IntArray): IntArray {
        val result = IntArray(a.size + b.size)
        var i = 0; var j = 0; var k = 0
        while (i < a.size && j < b.size) {
            if (a[i] <= b[j]) result[k++] = a[i++]
            else result[k++] = b[j++]
        }
        while (i < a.size) result[k++] = a[i++]
        while (j < b.size) result[k++] = b[j++]
        return result
    }
}

class MaxFinderTask(
    private val array: IntArray,
    private val left: Int,
    private val right: Int
) : RecursiveTask<Int>() {

    companion object {
        const val THRESHOLD = 1000
    }

    override fun compute(): Int {
        // TODO: Реализуй параллельный поиск максимума через fork/join
        // При right - left <= THRESHOLD — линейный поиск в диапазоне
        // Иначе — разбей и объедини результаты
        return array.copyOfRange(left, right).max()
    }
}

fun main() {
    val pool = ForkJoinPool()
    val size = 1_000_000
    val array = IntArray(size) { (Math.random() * size).toInt() }

    // Merge Sort
    val start = System.nanoTime()
    val sorted = pool.invoke(MergeSortTask(array, 0, array.size))
    val forkJoinTime = (System.nanoTime() - start) / 1_000_000

    val start2 = System.nanoTime()
    val sorted2 = array.clone().also { it.sort() }
    val singleTime = (System.nanoTime() - start2) / 1_000_000

    println("ForkJoin sort: ${forkJoinTime}ms")
    println("Single-thread sort: ${singleTime}ms")
    println("Correctly sorted: ${sorted.contentEquals(sorted2)}")

    // Max finder
    val max = pool.invoke(MaxFinderTask(array, 0, array.size))
    println("Max: $max (expected: ${array.max()})")

    pool.shutdown()
}
