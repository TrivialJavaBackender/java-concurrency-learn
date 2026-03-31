package exercises

import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * УПРАЖНЕНИЕ 12: Virtual Threads (Java 21+)
 *
 * Задание 1: Создай 100_000 виртуальных потоков, каждый делает Thread.sleep(1000).
 *            Замерь общее время. Объясни почему это работает, а platform threads — нет.
 *
 * Задание 2: Реализуй HTTP-подобный "сервер":
 *            - Принимает "запросы" (список строк)
 *            - Каждый запрос обрабатывается в виртуальном потоке
 *            - Обработка включает "блокирующий I/O" (Thread.sleep)
 *            - Используй Executors.newVirtualThreadPerTaskExecutor()
 *
 * Задание 3: Покажи проблему с pinning — synchronized блокирует carrier thread.
 *            Замерь время 100 виртуальных потоков с synchronized vs ReentrantLock.
 *            Объясни разницу.
 *
 * ВАЖНО: Требует Java 21+.
 */

fun task1_massiveVirtualThreads() {
    // TODO: Создай 100_000 виртуальных потоков, каждый спит 1000ms
    // Дождись всех. Замерь время. Объясни почему это работает, а platform threads — нет.
    println("TODO")
}

fun task2_virtualThreadServer() {
    // TODO: Обработай 1000 "запросов" конкурентно в виртуальных потоках
    // Каждый запрос: sleep 100ms, вернуть результат
    // Собери все результаты, напечатай количество обработанных
    println("TODO")
}

fun task3_pinningProblem() {
    // TODO: Запусти 100 виртуальных потоков с блокирующим sleep внутри synchronized блока
    // Замерь время, затем повтори с другим механизмом синхронизации
    // Объясни разницу в поведении
    println("TODO")
}

fun main() {
    println("=== Virtual Threads (Java 21+) ===")
    task1_massiveVirtualThreads()
    task2_virtualThreadServer()
    task3_pinningProblem()
}
