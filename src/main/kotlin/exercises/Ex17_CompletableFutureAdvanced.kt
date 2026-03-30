package exercises

import java.util.concurrent.*

/**
 * УПРАЖНЕНИЕ 17: CompletableFuture — продвинутые паттерны
 *
 * Задание 1: Параллельный вызов нескольких API + сбор результатов в список
 *            (типичная задача на собесе)
 *
 * Задание 2: thenCombine — объединение результатов двух независимых CF
 *
 * Задание 3: handle vs exceptionally vs whenComplete — обработка ошибок
 *            Покажи поведение каждого при успехе и ошибке.
 *
 * Задание 4: Timeout + fallback (orTimeout / completeOnTimeout, Java 9+)
 *
 * Задание 5: Реализуй retry с exponential backoff через CompletableFuture
 *
 * Задание 6: applyToEither — "гонка" двух сервисов, первый ответ побеждает
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

    // TODO: Запусти fetchUser для каждого id параллельно
    //   val futures = userIds.map { MockApi.fetchUser(it) }
    //   return CompletableFuture.allOf(*futures.toTypedArray())
    //       .thenApply { futures.map { it.join() } }

    return CompletableFuture.completedFuture(emptyList())
}

// ===== Задание 2: thenCombine — два независимых результата =====

fun task2_combine(userId: Int): CompletableFuture<String> {
    // TODO: Запусти fetchUser и fetchAddress ПАРАЛЛЕЛЬНО
    //   Объедини результаты через thenCombine:
    //   MockApi.fetchUser(userId)
    //       .thenCombine(MockApi.fetchAddress(userId)) { user, address ->
    //           "$user lives at $address"
    //       }

    return CompletableFuture.completedFuture("")
}

// ===== Задание 3: Обработка ошибок — три способа =====

fun task3_errorHandling() {
    val failing = CompletableFuture.supplyAsync<String> {
        throw RuntimeException("Boom!")
    }
    val succeeding = CompletableFuture.supplyAsync { "OK" }

    // TODO: exceptionally — перехватить ошибку, вернуть fallback
    //   val r1 = failing.exceptionally { ex -> "Fallback: ${ex.message}" }
    //   println("exceptionally (fail): ${r1.join()}")   // "Fallback: Boom!"
    //   val r1s = succeeding.exceptionally { "Fallback" }
    //   println("exceptionally (success): ${r1s.join()}")  // "OK"

    // TODO: handle — получить оба (result OR exception)
    //   val r2 = failing.handle { result, ex ->
    //       if (ex != null) "Handled: ${ex.message}" else result
    //   }
    //   println("handle (fail): ${r2.join()}")  // "Handled: Boom!"

    // TODO: whenComplete — побочный эффект, НЕ МЕНЯЕТ результат
    //   val r3 = failing.whenComplete { result, ex ->
    //       if (ex != null) println("  whenComplete logged error: ${ex.message}")
    //   }
    //   try { r3.join() } catch (e: Exception) {
    //       println("whenComplete (fail): exception still propagated!")
    //   }

    println("Error handling demo")
}

// ===== Задание 4: Timeout + Fallback =====

fun task4_timeout() {
    val slow = CompletableFuture.supplyAsync<String> {
        Thread.sleep(5000)  // очень медленный
        "Slow result"
    }

    // TODO: orTimeout — бросает TimeoutException
    //   val r1 = slow.copy()  // Java 9: copy()
    //       .orTimeout(1, TimeUnit.SECONDS)
    //       .exceptionally { "Timeout! ${it.message}" }
    //   println("orTimeout: ${r1.join()}")

    // TODO: completeOnTimeout — fallback без исключения
    //   val r2 = CompletableFuture.supplyAsync<String> { Thread.sleep(5000); "Slow" }
    //       .completeOnTimeout("Default value", 1, TimeUnit.SECONDS)
    //   println("completeOnTimeout: ${r2.join()}")

    // Для Java 8 — ручной timeout:
    //   val cf = CompletableFuture<String>()
    //   val scheduler = Executors.newScheduledThreadPool(1)
    //   scheduler.schedule({ cf.complete("Timeout fallback") }, 1, TimeUnit.SECONDS)
    //   CompletableFuture.supplyAsync { Thread.sleep(5000); "Slow" }
    //       .thenAccept { cf.complete(it) }
    //   println("Manual timeout: ${cf.join()}")

    println("Timeout demo")
}

// ===== Задание 5: Retry with Exponential Backoff =====

fun <T> retryWithBackoff(
    action: () -> CompletableFuture<T>,
    maxRetries: Int,
    initialDelayMs: Long = 100
): CompletableFuture<T> {
    // TODO: Рекурсивная реализация:
    //   return action().exceptionallyCompose { ex ->  // Java 12+
    //       if (maxRetries <= 0) CompletableFuture.failedFuture(ex)
    //       else {
    //           println("  Retry in ${initialDelayMs}ms... (${maxRetries} left)")
    //           val delayed = CompletableFuture<T>()
    //           Executors.newSingleThreadScheduledExecutor().schedule({
    //               retryWithBackoff(action, maxRetries - 1, initialDelayMs * 2)
    //                   .thenAccept { delayed.complete(it) }
    //                   .exceptionally { delayed.completeExceptionally(it); null }
    //           }, initialDelayMs, TimeUnit.MILLISECONDS)
    //           delayed
    //       }
    //   }

    return action() // placeholder
}

fun task5_retry() {
    // TODO: val result = retryWithBackoff({ MockApi.unreliableService() }, maxRetries = 5)
    //   println("Result: ${result.join()}")
    println("Retry demo")
}

// ===== Задание 6: applyToEither — гонка сервисов =====

fun task6_race() {
    // TODO: Два "зеркала" API с разной задержкой
    //   val mirror1 = CompletableFuture.supplyAsync {
    //       Thread.sleep((100..1000).random().toLong())
    //       "Result from Mirror-1"
    //   }
    //   val mirror2 = CompletableFuture.supplyAsync {
    //       Thread.sleep((100..1000).random().toLong())
    //       "Result from Mirror-2"
    //   }

    //   val fastest = mirror1.applyToEither(mirror2) { result ->
    //       "Winner: $result"
    //   }
    //   println(fastest.join())

    // Бонус: acceptEither — то же, но void
    // Бонус: runAfterEither — то же, но Runnable

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
