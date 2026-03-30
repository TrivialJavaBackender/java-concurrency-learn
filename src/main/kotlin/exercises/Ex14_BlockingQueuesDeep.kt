package exercises

import java.util.concurrent.*

/**
 * УПРАЖНЕНИЕ 14: BlockingQueue — все разновидности
 *
 * Задание 1: SynchronousQueue — прямая передача (handoff)
 *            Producer и Consumer — без буфера. put() блокируется до take().
 *            Покажи, что producer ждёт consumer'а.
 *
 * Задание 2: PriorityBlockingQueue — обработка задач по приоритету
 *            Генератор создаёт задачи с приоритетом 1-10.
 *            Worker обрабатывает в порядке приоритета.
 *
 * Задание 3: DelayQueue — отложенное выполнение
 *            Реализуй простой task scheduler: задачи выполняются через указанную задержку.
 *
 * Задание 4: LinkedTransferQueue — transfer() vs put()
 *            Покажи разницу: transfer() ждёт consumer'а, put() — нет.
 *
 * Задание 5: ArrayBlockingQueue fair vs unfair
 *            Покажи, что fair mode обеспечивает FIFO порядок для ожидающих потоков.
 */

// ===== Задание 1: SynchronousQueue =====

fun task1_synchronousQueue() {
    val queue = SynchronousQueue<String>()

    // TODO: Producer:
    //   for (i in 1..5) {
    //     println("Producer: putting item-$i")
    //     queue.put("item-$i")  // блокируется пока consumer не заберёт
    //     println("Producer: item-$i taken by consumer")
    //   }

    // TODO: Consumer (с задержкой, чтобы показать блокировку):
    //   for (i in 1..5) {
    //     Thread.sleep(500)
    //     val item = queue.take()
    //     println("Consumer: got $item")
    //   }

    println("SynchronousQueue demo")
}

// ===== Задание 2: PriorityBlockingQueue =====

data class PriorityTask(
    val name: String,
    val priority: Int  // чем меньше, тем важнее
) : Comparable<PriorityTask> {
    override fun compareTo(other: PriorityTask) = this.priority - other.priority
}

fun task2_priorityQueue() {
    val queue = PriorityBlockingQueue<PriorityTask>()

    // TODO: Положи задачи в случайном порядке:
    //   queue.put(PriorityTask("Low priority task", 10))
    //   queue.put(PriorityTask("CRITICAL task", 1))
    //   queue.put(PriorityTask("Medium task", 5))
    //   queue.put(PriorityTask("High priority task", 2))
    //   queue.put(PriorityTask("Normal task", 7))

    // TODO: Worker забирает и обрабатывает — должно быть в порядке приоритета
    //   while (queue.isNotEmpty()) {
    //     val task = queue.take()
    //     println("Processing: ${task.name} (priority=${task.priority})")
    //   }
}

// ===== Задание 3: DelayQueue =====

class ScheduledItem(
    val name: String,
    private val delayMs: Long,
    private val createdAt: Long = System.currentTimeMillis()
) : Delayed {

    override fun getDelay(unit: TimeUnit): Long {
        val remaining = (createdAt + delayMs) - System.currentTimeMillis()
        return unit.convert(remaining, TimeUnit.MILLISECONDS)
    }

    override fun compareTo(other: Delayed): Int {
        return getDelay(TimeUnit.MILLISECONDS).compareTo(other.getDelay(TimeUnit.MILLISECONDS))
    }
}

fun task3_delayQueue() {
    val queue = DelayQueue<ScheduledItem>()

    // TODO: Добавь элементы с разной задержкой:
    //   queue.put(ScheduledItem("Task-A", 3000))  // через 3 секунды
    //   queue.put(ScheduledItem("Task-B", 1000))  // через 1 секунду
    //   queue.put(ScheduledItem("Task-C", 2000))  // через 2 секунды

    // TODO: Worker:
    //   repeat(3) {
    //     val item = queue.take()  // блокируется пока delay не истечёт
    //     println("${System.currentTimeMillis()}: Executing ${item.name}")
    //   }
    // Ожидаемый порядок: B (1s), C (2s), A (3s)
}

// ===== Задание 4: LinkedTransferQueue =====

fun task4_transferQueue() {
    val queue = LinkedTransferQueue<String>()

    // TODO: Consumer с задержкой:
    //   Thread.sleep(2000)
    //   println("Consumer: taking...")
    //   println("Consumer: got ${queue.take()}")

    // TODO: Producer — покажи разницу:
    //   println("put() — не ждёт consumer'а")
    //   queue.put("item-via-put")
    //   println("put() — вернулся сразу")

    //   println("transfer() — ждёт consumer'а...")
    //   queue.transfer("item-via-transfer")  // БЛОКИРУЕТСЯ пока consumer не заберёт!
    //   println("transfer() — consumer забрал!")

    //   println("tryTransfer() — не ждёт")
    //   val success = queue.tryTransfer("item-try")
    //   println("tryTransfer success: $success")  // false если нет ожидающего consumer'а
}

// ===== Задание 5: Fair ArrayBlockingQueue =====

fun task5_fairQueue() {
    // TODO: Создай ArrayBlockingQueue(1, true) — fair
    //   Заполни очередь (capacity=1, один элемент)
    //   Запусти 5 потоков в ОПРЕДЕЛЁННОМ порядке, каждый делает queue.put()
    //   Медленно освобождай очередь через take()
    //   С fair=true потоки должны получить доступ в FIFO порядке
    //   С fair=false порядок не гарантирован

    // Покажи разницу запустив оба варианта
}

fun main() {
    println("=== Task 1: SynchronousQueue ===")
    task1_synchronousQueue()

    println("\n=== Task 2: PriorityBlockingQueue ===")
    task2_priorityQueue()

    println("\n=== Task 3: DelayQueue ===")
    task3_delayQueue()

    println("\n=== Task 4: LinkedTransferQueue ===")
    task4_transferQueue()

    println("\n=== Task 5: Fair ArrayBlockingQueue ===")
    task5_fairQueue()
}
