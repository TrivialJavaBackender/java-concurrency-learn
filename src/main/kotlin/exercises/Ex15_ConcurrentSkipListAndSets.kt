package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CopyOnWriteArraySet

/**
 * УПРАЖНЕНИЕ 15: ConcurrentSkipListMap, Sets, newKeySet
 *
 * Задание 1: ConcurrentSkipListMap — sorted leaderboard
 *            Несколько потоков одновременно обновляют счёт игроков.
 *            Используй subMap/headMap/tailMap для range queries.
 *
 * Задание 2: ConcurrentSkipListSet — sorted unique events
 *            Потоки добавляют timestamped события.
 *            Запроси все события за последние N миллисекунд.
 *
 * Задание 3: ConcurrentHashMap.newKeySet() vs CopyOnWriteArraySet
 *            Benchmark: 10 потоков добавляют и читают.
 *            Покажи когда какой выбрать.
 */

// ===== Задание 1: Sorted Leaderboard =====

class Leaderboard {
    // Ключ: -score (для desc сортировки), значение: player name
    // Или используй ConcurrentSkipListMap с Comparator.reverseOrder()
    private val scores = ConcurrentSkipListMap<Int, String>()
    private val playerScores = ConcurrentHashMap<String, Int>()

    fun updateScore(player: String, score: Int) {
        // TODO:
        //   val oldScore = playerScores.put(player, score)
        //   if (oldScore != null) scores.remove(oldScore)  // удали старый
        //   scores.put(score, player)
        //
        // ⚠️ Это упрощённая версия — что если два игрока с одинаковым счётом?
        // Для продакшена нужен составной ключ или другая структура
    }

    fun topN(n: Int): List<Pair<String, Int>> {
        // TODO: Верни N лучших (наибольший score первым)
        //   return scores.descendingMap().entries.take(n).map { it.value to it.key }
        return emptyList()
    }

    fun playersAboveScore(minScore: Int): Map<Int, String> {
        // TODO: Используй tailMap(minScore) для range query
        return emptyMap()
    }
}

fun task1_leaderboard() {
    val board = Leaderboard()

    // TODO: Запусти 5 потоков, каждый обновляет 20 случайных игроков с случайным счётом
    // Дождись всех
    // Напечатай Top 5

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

    // TODO: 3 потока добавляют события с текущим timestamp + случайное смещение
    //   events.add(TimestampedEvent(System.currentTimeMillis(), "Event from Thread-$i"))
    //   Thread.sleep(random 10-50ms)

    // TODO: Запроси события за последние 100ms:
    //   val cutoff = TimestampedEvent(System.currentTimeMillis() - 100, "")
    //   val recent = events.tailSet(cutoff)
    //   println("Recent events (last 100ms): ${recent.size}")

    println("Sorted events demo")
}

// ===== Задание 3: newKeySet vs CopyOnWriteArraySet =====

fun task3_setBenchmark() {
    val iterations = 10_000
    val threads = 10

    // ConcurrentHashMap.newKeySet() — для частых записей
    val chmSet = ConcurrentHashMap.newKeySet<String>()

    // CopyOnWriteArraySet — для редких записей, частых чтений
    val cowSet = CopyOnWriteArraySet<String>()

    // TODO: Benchmark записи
    //   Замерь время добавления iterations элементов из threads потоков для каждого Set

    // TODO: Benchmark чтения
    //   Замерь время итерации (forEach) при конкурентных записях для каждого Set

    // TODO: Напечатай результаты
    //   Ожидание: newKeySet быстрее для записи, COWAL быстрее для чтения (при маленьком размере)

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
