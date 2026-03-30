package exercises

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * УПРАЖНЕНИЕ 4: Конфигурация приложения на ReadWriteLock
 *
 * Задание: Реализуй потокобезопасное хранилище конфигурации.
 * - Множество потоков могут читать одновременно
 * - Только один поток может писать, блокируя всех читателей
 * - Реализуй reload() — атомарная перезагрузка всей конфигурации
 *
 * Запусти 10 читателей (читают случайные ключи каждые 10ms)
 * и 2 писателя (обновляют конфигурацию каждые 100ms).
 * Покажи, что чтения не блокируют друг друга.
 */

class AppConfig {
    private val lock = ReentrantReadWriteLock()
    private val config = mutableMapOf<String, String>()

    fun get(key: String): String? {
        // TODO: Используй lock.read { ... } (Kotlin extension)
        return null
    }

    fun set(key: String, value: String) {
        // TODO: Используй lock.write { ... }
    }

    fun reload(newConfig: Map<String, String>) {
        // TODO: lock.write { config.clear(); config.putAll(newConfig) }
    }

    fun getAll(): Map<String, String> {
        // TODO: lock.read { return HashMap(config) } — возвращай копию!
        return emptyMap()
    }
}

fun main() {
    val config = AppConfig()
    config.reload(mapOf("db.url" to "localhost:5432", "db.pool" to "10", "app.name" to "MyApp"))

    // TODO: Запусти 10 читателей и 2 писателя
    // Читатели: каждые 10ms читают случайный ключ, печатают "[Reader-N] key=value"
    // Писатели: каждые 100ms обновляют случайный ключ, печатают "[Writer-N] set key=value"
    // Работают 2 секунды, затем останавливаются (используй volatile flag)

    println("Config demo complete")
}
