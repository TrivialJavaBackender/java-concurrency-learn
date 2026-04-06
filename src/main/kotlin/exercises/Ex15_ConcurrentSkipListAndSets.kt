package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.time.measureTime

/**
 * УПРАЖНЕНИЕ 15: ConcurrentSkipListMap, Sets, newKeySet
 *
 * Задание 1: ConcurrentSkipListMap — sorted leaderboard
 *            5 потоков одновременно обновляют счёт 10 игроков.
 *            После завершения — выведи Top 5 (по убыванию счёта).
 *            Используй tailMap() для получения всех игроков с score > N.
 *            Подумай: как хранить уникальные ключи если score может совпадать?
 *
 * Задание 2: ConcurrentSkipListSet — sorted unique events
 *            3 потока добавляют timestamped события.
 *            Запроси все события за последние 100ms через tailSet().
 *
 * Задание 3: ConcurrentHashMap.newKeySet() vs CopyOnWriteArraySet
 *            Benchmark: 10 потоков добавляют элементы конкурентно.
 *            Сравни время записи и время итерации для обоих вариантов.
 *            Объясни когда что использовать.
 */

// ===== Задание 1: Sorted Leaderboard =====

class Leaderboard {
    data class PlayerScore(val score: Int, val player: String) : Comparable<PlayerScore> {
        override fun compareTo(other: PlayerScore): Int {
            val scoreCompare = score.compareTo(other.score)
            return if (scoreCompare != 0) scoreCompare else player.compareTo(other.player)
        }
    }

    private val scores = ConcurrentSkipListSet<PlayerScore>()
    private val playerScores = ConcurrentHashMap<String, Int>()
    private val lock = ReentrantReadWriteLock()

    fun updateScore(player: String, score: Int) {
        lock.write {
            playerScores[player]?.let { playerScore -> scores.remove(PlayerScore(playerScore, player)) }
            playerScores[player] = score
            scores.add(PlayerScore(score, player))
        }
    }

    fun topN(n: Int): List<Pair<String, Int>> {
        lock.read {
            return scores.reversed().take(n).map { v ->
                v.player to v.score
            }
        }
    }

    fun playersAboveScore(minScore: Int): Map<Int, String> {
        lock.read {
            return scores.tailSet(PlayerScore(minScore, "")).map { it.score to it.player }.toMap()
        }
    }
}

fun task1_leaderboard() {
    val board = Leaderboard()

    val workers = (1..5).map {
        Thread {
            repeat(10) {
                board.updateScore("player-${(Math.random() * 10).toInt() + 1}", (Math.random() * 1000).toInt())
            }
        }
    }.onEach { it.start() }

    workers.forEach { it.join() }

    println("Top 5 players: ${board.topN(5)}")
    println("Players with >500 score: ${board.playersAboveScore(500)}")


    println("Leaderboard demo")
}

// ===== Задание 2: Sorted Events =====

data class TimestampedEvent(
    val timestamp: Long,
    val message: String
) : Comparable<TimestampedEvent> {
    override fun compareTo(other: TimestampedEvent) = this.timestamp.compareTo(other.timestamp)
}

fun task2_sortedEvents() {
    val events = ConcurrentSkipListSet<TimestampedEvent>()

    val start = System.currentTimeMillis()
    val workers = (1..3).map {
        Thread {
            repeat(10) {
                val delay = (Math.random() * 20 + 20).toLong()
                val timestamp = System.currentTimeMillis() - start
                events += TimestampedEvent(timestamp, "${Thread.currentThread().name}-$timestamp")
                Thread.sleep(delay)
            }
        }
    }.onEach { it.start() }
    workers.forEach { it.join() }

    val latestsEvents = events.tailSet(TimestampedEvent(System.currentTimeMillis() - 100 - start, ""))
    println("Latest events: $latestsEvents. Size: ${latestsEvents.size}")
}

// ===== Задание 3: newKeySet vs CopyOnWriteArraySet =====

fun task3_setBenchmark() {
    val iterations = 100_000
    val threads = 10

    val chmSet = ConcurrentHashMap.newKeySet<String>()
    val cowSet = CopyOnWriteArraySet<String>()

    val chmWorkers = (1..threads).map {
        Thread {
            (1..iterations).forEach { chmSet.add("$it") }
        }
    }
    val chmTime = measureTime {
        chmWorkers.forEach { it.start() }
        chmWorkers.forEach { it.join() }
    }


    val cowWorkers = (1..threads).map {
        Thread {
            (1..iterations).forEach { cowSet.add("$it") }
        }
    }
    val cowTime = measureTime {
        cowWorkers.forEach { it.start() }
        cowWorkers.forEach { it.join() }
    }

    println("CHM Time: $chmTime, COW Time: $cowTime")


    val chmIterate = measureTime({ chmSet.forEach { print("") } })
    val cowIterate = measureTime({ cowSet.forEach { print("") } })

    println("CHM Iterate: $chmIterate, COW Iterate: $cowIterate")

}

fun main() {
    println("=== Task 1: Leaderboard ===")
    task1_leaderboard()

    println("\n=== Task 2: Sorted Events ===")
    task2_sortedEvents()

    println("\n=== Task 3: Set Benchmark ===")
    task3_setBenchmark()
}
