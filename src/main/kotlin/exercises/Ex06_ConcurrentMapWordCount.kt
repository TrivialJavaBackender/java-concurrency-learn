package exercises

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * УПРАЖНЕНИЕ 6: ConcurrentHashMap и CopyOnWriteArrayList
 *
 * Задание 1: Параллельный подсчёт слов.
 *            Раздели текст на 4 части, каждый поток считает слова в своей части.
 *            Используй ConcurrentHashMap.merge() для агрегации результатов.
 *
 * Задание 2: Покажи разницу между ConcurrentHashMap.putIfAbsent() и обычным put().
 *            Запусти гонку — 10 потоков одновременно пытаются положить значение по одному ключу.
 *            Только один должен "выиграть".
 *
 * Задание 3: Реализуй event bus на CopyOnWriteArrayList.
 *            Подписчики регистрируются, publisher отправляет события.
 *            Подписчики могут добавляться/удаляться во время итерации (snapshot iterator).
 */

// ===== Задание 1: Word Count =====

fun parallelWordCount(text: String): Map<String, Int> {
    val result = ConcurrentHashMap<String, Int>()
    val words = text.lowercase().split("\\s+".toRegex())

    // TODO: Раздели words на 4 чанка, обработай каждый в отдельном потоке
    // Используй merge() для безопасного накопления результатов
    // Дождись всех потоков

    return result
}

// ===== Задание 2: putIfAbsent race =====

fun putIfAbsentRace() {
    val map = ConcurrentHashMap<String, String>()

    // TODO: 10 потоков одновременно пытаются вставить значение по ключу "winner"
    // Используй putIfAbsent — только один поток должен победить
    // Напечатай результат: кто "выиграл"
}

// ===== Задание 3: Event Bus =====

class SimpleEventBus {
    private val listeners = CopyOnWriteArrayList<(String) -> Unit>()

    fun subscribe(listener: (String) -> Unit) {
        // TODO
    }

    fun unsubscribe(listener: (String) -> Unit) {
        // TODO
    }

    fun publish(event: String) {
        // TODO: Почему CopyOnWriteArrayList безопасен при конкурентной итерации?
    }
}

fun main() {
    // Word count
    val text = "the quick brown fox jumps over the lazy dog the fox the dog"
    val counts = parallelWordCount(text)
    println("Word counts: $counts")
    // Expected: {the=4, fox=2, dog=2, quick=1, brown=1, jumps=1, over=1, lazy=1}

    // putIfAbsent race
    putIfAbsentRace()

    // Event bus
    val bus = SimpleEventBus()
    val listener1: (String) -> Unit = { println("[Listener 1] $it") }
    val listener2: (String) -> Unit = { println("[Listener 2] $it") }

    bus.subscribe(listener1)
    bus.subscribe(listener2)
    bus.publish("Hello!")

    bus.unsubscribe(listener1)
    bus.publish("After unsubscribe")
}
