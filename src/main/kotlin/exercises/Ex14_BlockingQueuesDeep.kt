package exercises

import java.util.concurrent.*

/**
 * УПРАЖНЕНИЕ 14: BlockingQueue — все разновидности
 *
 * Задание 1: SynchronousQueue — прямая передача (handoff)
 *            Producer и Consumer без буфера: put() блокируется до take().
 *            Покажи в логах, что producer ждёт consumer'а перед каждой передачей.
 *
 * Задание 2: PriorityBlockingQueue — обработка задач по приоритету
 *            Положи задачи с приоритетами в случайном порядке.
 *            Worker должен обработать их строго в порядке приоритета.
 *
 * Задание 3: DelayQueue — отложенное выполнение
 *            Добавь 3 задачи с задержками 3s, 1s, 2s.
 *            Worker берёт через take() — они должны выйти в порядке B(1s), C(2s), A(3s).
 *            ScheduledItem уже реализован — реализуй логику запуска.
 *
 * Задание 4: LinkedTransferQueue — transfer() vs put()
 *            Покажи разницу: put() возвращается сразу, transfer() ждёт consumer'а.
 *
 * Задание 5: ArrayBlockingQueue fair vs unfair
 *            fair=true гарантирует FIFO для ожидающих потоков.
 *            Покажи разницу поведения с fair=true и fair=false.
 */

// ===== Задание 1: SynchronousQueue =====

fun task1_synchronousQueue() {
    val queue = SynchronousQueue<String>()

    // TODO: Producer: для каждого из 5 элементов — логируй "putting", put(), логируй "taken"
    // TODO: Consumer с задержкой 500ms: логируй "got $item"
    // Запусти оба в потоках, дождись завершения

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

    // TODO: Добавь 5 задач с приоритетами 10, 1, 5, 2, 7 в указанном порядке
    // Worker: пока очередь не пустая, take() и обработай
    // Убедись что порядок: 1, 2, 5, 7, 10
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

    // TODO: Добавь Task-A(3s), Task-B(1s), Task-C(2s)
    // Worker: 3 раза take() и логируй время выполнения
    // Ожидаемый порядок: B, C, A
}

// ===== Задание 4: LinkedTransferQueue =====

fun task4_transferQueue() {
    val queue = LinkedTransferQueue<String>()

    // TODO: Запусти consumer с задержкой 2 секунды
    // Producer: сначала put() — должен вернуться сразу
    //           затем transfer() — должен заблокироваться до consumer
    //           затем tryTransfer() — вернёт false если нет ожидающего consumer
    // Логируй каждый шаг с временными метками
}

// ===== Задание 5: Fair ArrayBlockingQueue =====

fun task5_fairQueue() {
    // TODO: Создай ArrayBlockingQueue(1, true) с одним элементом (capacity=1, заполнена)
    // Запусти 5 потоков в определённом порядке, каждый делает put()
    // Медленно освобождай через take() из main
    // С fair=true потоки получают доступ в FIFO порядке — покажи это в логах
    // Повтори с fair=false — порядок не гарантирован
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
