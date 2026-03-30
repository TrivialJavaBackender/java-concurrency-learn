package exercises

import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * УПРАЖНЕНИЕ 8: CompletableFuture — цепочки и композиция
 *
 * Задание 1: Построй цепочку:
 *   fetchUserId("pavel") → fetchUserProfile(id) → fetchOrders(profile) → calculateTotal(orders)
 *   Каждый шаг — async операция с задержкой 200ms.
 *   Обработай ошибки с exceptionally().
 *
 * Задание 2: Параллельный запрос к 3 "сервисам" через allOf().
 *   Дождись всех результатов, объедини в один ответ.
 *
 * Задание 3: Гонка — anyOf() для выбора самого быстрого результата
 *   из 3 "зеркал" с разным временем ответа.
 */

// Имитация async API
fun fetchUserId(name: String): CompletableFuture<Int> {
    return CompletableFuture.supplyAsync {
        Thread.sleep(200)
        if (name == "error") throw RuntimeException("User not found: $name")
        name.hashCode() % 1000
    }
}

fun fetchUserProfile(userId: Int): CompletableFuture<Map<String, String>> {
    return CompletableFuture.supplyAsync {
        Thread.sleep(200)
        mapOf("id" to userId.toString(), "name" to "User-$userId", "tier" to "premium")
    }
}

fun fetchOrders(profile: Map<String, String>): CompletableFuture<List<Double>> {
    return CompletableFuture.supplyAsync {
        Thread.sleep(200)
        listOf(29.99, 49.99, 9.99, 199.99)
    }
}

// ===== Задание 1: Chain =====

fun task1_chain(username: String): CompletableFuture<Double> {
    // TODO: Построй цепочку из fetchUserId → fetchUserProfile → fetchOrders → sum
    // Используй thenCompose для async шагов, thenApply для синхронного преобразования
    // Обработай ошибки через exceptionally (вернуть 0.0)
    return CompletableFuture.completedFuture(0.0)
}

// ===== Задание 2: Parallel with allOf =====

fun task2_parallel(): CompletableFuture<Map<String, String>> {
    // TODO: Запусти 3 CompletableFuture параллельно (задержки 300ms, 200ms, 100ms)
    // Используй allOf() чтобы дождаться всех, затем собери результаты через join()
    return CompletableFuture.completedFuture(emptyMap())
}

// ===== Задание 3: Race with anyOf =====

fun task3_race(): CompletableFuture<String> {
    // TODO: 3 "зеркала" с разной задержкой (100ms, 300ms, 500ms)
    // Используй anyOf() — верни результат первого завершившегося
    // Подумай: какой тип возвращает anyOf() и почему?
    return CompletableFuture.completedFuture("")
}

fun main() {
    println("=== Task 1: Chain ===")
    val total = task1_chain("pavel").get(5, TimeUnit.SECONDS)
    println("Total: $total")

    println("\n=== Task 1: Chain with error ===")
    val errorTotal = task1_chain("error").get(5, TimeUnit.SECONDS)
    println("Error total: $errorTotal") // 0.0

    println("\n=== Task 2: Parallel ===")
    val results = task2_parallel().get(5, TimeUnit.SECONDS)
    println("Results: $results")

    println("\n=== Task 3: Race ===")
    val fastest = task3_race().get(5, TimeUnit.SECONDS)
    println("Fastest: $fastest")
}
