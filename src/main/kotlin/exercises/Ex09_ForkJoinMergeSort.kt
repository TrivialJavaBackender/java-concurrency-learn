package exercises

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import kotlin.math.max

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
        if (right - left <= THRESHOLD) {
            array.sort(left, right)
            return array
        } else {
            val mid = left + (right - left) / 2
            val leftTask = MergeSortTask(array, left, mid).fork()
            val rightTask = MergeSortTask(array, mid, right)

            val rightPart = rightTask.compute()
            val leftPart = leftTask.join()
            val merged = merge(leftPart.copyOfRange(left, mid), rightPart.copyOfRange(mid, right))
            merged.copyInto(array, left)
            return array
        }
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

        if (right - left <= THRESHOLD) {
            var max = Int.MIN_VALUE
            for (i in left..<right) {
                max = max(max, array[i])
            }
            return max
        } else {
            val mid = left + (right - left) / 2
            val leftTask = MaxFinderTask(array, left,mid).fork()
            val rightTask = MaxFinderTask(array, mid, right)
            val rightMax = rightTask.compute()
            val leftMax = leftTask.join()
            return Math.max(leftMax, rightMax)
        }
    }
}

fun main() {
    val pool = ForkJoinPool()
    val size = 10_000_000
    val array = IntArray(size) { (Math.random() * size).toInt() }
    val array2 = array.clone()

    // Merge Sort
    val start = System.nanoTime()
    val sorted = pool.invoke(MergeSortTask(array, 0, array.size))
    val forkJoinTime = (System.nanoTime() - start) / 1_000_000

    val start2 = System.nanoTime()
    val sorted2 = array2.also { it.sort() }
    val singleTime = (System.nanoTime() - start2) / 1_000_000

    println("ForkJoin sort: ${forkJoinTime}ms")
    println("Single-thread sort: ${singleTime}ms")
    println("Correctly sorted: ${sorted.contentEquals(sorted2)}")

    // Max finder
    val max = pool.invoke(MaxFinderTask(array, 0, array.size))
    println("Max: $max (expected: ${array.max()})")

    pool.shutdown()
}
