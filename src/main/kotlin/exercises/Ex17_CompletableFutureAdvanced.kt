package exercises

import java.util.concurrent.*

/**
 * УПРАЖНЕНИЕ 17: CompletableFuture — продвинутые паттерны
 *
 * Задание 1: Параллельный вызов нескольких API + сбор результатов в список.
 *            fetchUser для 5 userId параллельно → List<String>.
 *            Используй allOf() и join() после него.
 *
 * Задание 2: thenCombine — объединение двух независимых CF.
 *            fetchUser(id) и fetchAddress(id) запускаются параллельно,
 *            результат: "$user lives at $address".
 *
 * Задание 3: handle vs exceptionally vs whenComplete — обработка ошибок.
 *            Протести каждый на успешном и упавшем CF.
 *            Главный вопрос: какой из них НЕ меняет результат?
 *
 * Задание 4: Timeout + fallback (Java 9+).
 *            orTimeout() — бросает TimeoutException.
 *            completeOnTimeout() — возвращает дефолтное значение.
 *
 * Задание 5: Retry с exponential backoff.
 *            MockApi.unreliableService() успевает только на 3-й попытке.
 *            Реализуй retryWithBackoff(action, maxRetries, initialDelayMs).
 *
 * Задание 6: applyToEither — "гонка" двух сервисов.
 *            Два mirror с разной задержкой — победит быстрый.
 */

// Имитация API
object MockApi {
    fun fetchUser(id: Int): CompletableFuture<String> = CompletableFuture.supplyAsync {
        Thread.sleep((100..500).random().toLong())
        if (id == 999) throw RuntimeException("User $id not found")
        "User-$id"
    }

    fun fetchAddress(userId: Int): CompletableFuture<String> = CompletableFuture.supplyAsync {
        Thread.sleep((100..300).random().toLong())
        "Address of User-$userId"
    }

    fun fetchPayment(userId: Int): CompletableFuture<Double> = CompletableFuture.supplyAsync {
        Thread.sleep((100..400).random().toLong())
        userId * 99.99
    }

    private var callCount = 0
    fun unreliableService(): CompletableFuture<String> = CompletableFuture.supplyAsync {
        callCount++
        Thread.sleep(100)
        if (callCount % 3 != 0) throw RuntimeException("Attempt $callCount failed")
        "Success on attempt $callCount"
    }
}

// ===== Задание 1: Parallel API calls → List =====

fun task1_parallelToList(): CompletableFuture<List<String>> {
    val userIds = listOf(1, 2, 3, 4, 5)
    // TODO: fetchUser для каждого id параллельно, собери все результаты в список
    return CompletableFuture.completedFuture(emptyList())
}

// ===== Задание 2: thenCombine =====

fun task2_combine(userId: Int): CompletableFuture<String> {
    // TODO: Запусти fetchUser и fetchAddress ПАРАЛЛЕЛЬНО, объедини через thenCombine
    return CompletableFuture.completedFuture("")
}

// ===== Задание 3: Error handling =====

fun task3_errorHandling() {
    val failing = CompletableFuture.supplyAsync<String> { throw RuntimeException("Boom!") }
    val succeeding = CompletableFuture.supplyAsync { "OK" }

    // TODO: exceptionally — протести на failing и succeeding
    // TODO: handle — протести на failing и succeeding
    // TODO: whenComplete — покажи что исключение всё равно пробрасывается

    println("Error handling demo")
}

// ===== Задание 4: Timeout + Fallback =====

fun task4_timeout() {
    // TODO: Создай медленный CF (sleep 5s)
    // orTimeout(1s) + exceptionally → напечатай "Timeout: ..."
    // completeOnTimeout("Default", 1s) → напечатай "Default value"

    println("Timeout demo")
}

// ===== Задание 5: Retry with Exponential Backoff =====

fun <T> retryWithBackoff(
    action: () -> CompletableFuture<T>,
    maxRetries: Int,
    initialDelayMs: Long = 100
): CompletableFuture<T> {
    // TODO: При ошибке — подождать initialDelayMs и повторить с maxRetries-1 и задержкой*2
    // При maxRetries=0 — вернуть failed future
    // Используй exceptionallyCompose (Java 12+) или вложенные thenCompose
    return action()
}

fun task5_retry() {
    // TODO: val result = retryWithBackoff({ MockApi.unreliableService() }, maxRetries = 5)
    // println("Result: ${result.join()}")
    println("Retry demo")
}

// ===== Задание 6: applyToEither =====

fun task6_race() {
    // TODO: Два CF с разной задержкой (random 100-1000ms каждый)
    // applyToEither — победитель применяет функцию к результату
    // Напечатай кто выиграл

    println("Race demo")
}

fun main() {
    println("=== Task 1: Parallel API → List ===")
    println("Results: ${task1_parallelToList().join()}")

    println("\n=== Task 2: thenCombine ===")
    println(task2_combine(42).join())

    println("\n=== Task 3: Error Handling ===")
    task3_errorHandling()

    println("\n=== Task 4: Timeout ===")
    task4_timeout()

    println("\n=== Task 5: Retry ===")
    task5_retry()

    println("\n=== Task 6: Race ===")
    task6_race()
}
