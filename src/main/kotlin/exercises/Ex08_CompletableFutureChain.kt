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
    return fetchUserId(username)
        .thenCompose  { userId -> fetchUserProfile(userId) }
        .thenCompose { userProfile -> fetchOrders(userProfile) }
        .thenApply { orders -> orders.sum() }
        .exceptionally {
            println("Exception while processing: ${it.message}")
            0.0
        }

}

// ===== Задание 2: Parallel with allOf =====

fun task2_parallel(): CompletableFuture<Map<String, String>> {

    val f1 = CompletableFuture.supplyAsync { Thread.sleep(300); "f1" to "300" }
    val f2 = CompletableFuture.supplyAsync { Thread.sleep(200); "f2" to "200" }
    val f3 = CompletableFuture.supplyAsync { Thread.sleep(100); "f3" to "100" }
    val futures = listOf(f1, f2, f3)
    return CompletableFuture.allOf(f1, f2, f3)
        .thenApply { futures.map { it.get() }.toMap() }
}

// ===== Задание 3: Race with anyOf =====


fun task3_race(): CompletableFuture<String> {
    val f1 = CompletableFuture.supplyAsync { Thread.sleep(300); "300" }
    val f2 = CompletableFuture.supplyAsync { Thread.sleep(200); "200" }
    val f3 = CompletableFuture.supplyAsync { Thread.sleep(100); "100" }
    return CompletableFuture.anyOf(f1, f2, f3).thenApply { v -> v as String }
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
