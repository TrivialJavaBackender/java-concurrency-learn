package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CopyOnWriteArraySet

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
    private val scores = ConcurrentSkipListMap<Int, String>()
    private val playerScores = ConcurrentHashMap<String, Int>()

    fun updateScore(player: String, score: Int) {
        // TODO: Обнови счёт игрока атомарно
    }

    fun topN(n: Int): List<Pair<String, Int>> {
        // TODO: Верни N лучших (наибольший score первым)
        return emptyList()
    }

    fun playersAboveScore(minScore: Int): Map<Int, String> {
        // TODO: Верни игроков с score >= minScore
        return emptyMap()
    }
}

fun task1_leaderboard() {
    val board = Leaderboard()

    // TODO: 5 потоков, каждый обновляет счёт 10 случайных игроков (игроки: "player-1".."player-10")
    // Дождись всех, напечатай Top 5 и игроков с score > 500

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

    // TODO: 3 потока, каждый добавляет 10 событий с текущим timestamp
    // Дождись всех потоков
    // Запроси события за последние 100ms
    // Напечатай количество недавних событий

    println("Sorted events demo")
}

// ===== Задание 3: newKeySet vs CopyOnWriteArraySet =====

fun task3_setBenchmark() {
    val iterations = 10_000
    val threads = 10

    val chmSet = ConcurrentHashMap.newKeySet<String>()
    val cowSet = CopyOnWriteArraySet<String>()

    // TODO: Замерь время параллельного добавления для обоих Set
    // TODO: Замерь время итерации (forEach) при параллельных записях для обоих
    // TODO: Напечатай и сравни результаты
    // Объясни: когда выбрать newKeySet, а когда CopyOnWriteArraySet?

    println("Set benchmark demo")
}

fun main() {
    println("=== Task 1: Leaderboard ===")
    task1_leaderboard()

    println("\n=== Task 2: Sorted Events ===")
    task2_sortedEvents()

    println("\n=== Task 3: Set Benchmark ===")
    task3_setBenchmark()
}
