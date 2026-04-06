package exercises

import java.util.concurrent.*
import kotlin.time.Duration
import kotlin.time.measureTime

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

    println("SynchronousQueue demo")

    val producer = Thread {
        var elapsedTime = Duration.ZERO
        repeat(5) {
            val value: String
            elapsedTime += measureTime {
                println("Sending an element to queue: $it")
                value = "Value-$it"
                queue.put(value)
            }
            println("Sent data to queue: $value. Elapsed time: $elapsedTime")
        }
    }

    val consumer = Thread {
        var elapsedTime = Duration.ZERO
        repeat(5) {
            val value: String
            elapsedTime += measureTime {
                println("Waiting for an element from the queue: $it")
                value = queue.take()
                println("Received data from queue: $value")
                Thread.sleep(500)
            }
            println("Processed data from queue: $value. Elapsed time: $elapsedTime")
        }
    }

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
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


    val tasks = listOf(10, 1, 5, 2, 7)

    val producer = Thread {
        tasks.forEach {
            queue.put(PriorityTask("Task #$it", priority = it))
        }
    }

    val consumer = Thread {
        Thread.sleep(500)
        println("РАБотник проснулся. Опять за работу!")
        repeat(5) {
            val value = queue.take()
            println("Получил работу: $value. РАБотаю...")
            Thread.sleep(500)
            println("Cлавно поработал над задачей $value")
        }
    }

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
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

    val delays = listOf("A" to 3L, "B" to 1L, "C" to 2L)

    val producer = Thread {
        delays.forEach {
            queue.put(ScheduledItem("Task-${it.first}", it.second * 1000L))
        }
    }

    val consumer = Thread {
        Thread.sleep(500)
        println("РАБотник проснулся. Опять за работу!")
        repeat(3) {
            val value = queue.take()
            println("Получил работу: ${value.name} РАБотаю...")
            println("Cлавно поработал над задачей ${value.name}")
        }
    }

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
}

// ===== Задание 4: LinkedTransferQueue =====

fun task4_transferQueue() {
    val queue = LinkedTransferQueue<String>()


    val producer = Thread {
        var elapsedTime = Duration.ZERO
        println("Король проснулся.")
        repeat(5) {
            val value: String
            elapsedTime += measureTime {
                println("Король: Отправляю работу батракам: $it")
                value = "Работа-$it"
                queue.transfer(value)
            }
            println("Король: Отправил работу батраку: $value. Прошли времени с начала работы: $elapsedTime")
        }

        repeat(5) {
            val value: String
            elapsedTime += measureTime {
                println("Король: Отправляю еще больше работы батракам: $it")
                value = "Работа-$it"
                while (!queue.tryTransfer(value)) {
                    println("Никто не принял работы. Глупые батраки!")
                    Thread.sleep(500)
                }

            }
            println("Король: Отправил работу батраку: $value. Прошло времени с начала работы: $elapsedTime")
        }
    }

    val consumer = Thread {
        var elapsedTime = Duration.ZERO
        repeat(5) {
            val value: String
            elapsedTime += measureTime {
                println("Батрак: Жду работы!")
                value = queue.take()
                println("Батрак: Получил работу: $value")
                Thread.sleep(500)
            }
            println("Батрак: Сделал работу $value. Прошло времени с начала работы: $elapsedTime")
        }

        println("Батрак: Работа сделана! Пора и поспать")
        Thread.sleep(2000)
        println("Батрак: Проснулсяя!")

        repeat(5) {
            val value: String
            elapsedTime += measureTime {
                println("Батрак: Жду работы!")
                value = queue.take()
                println("Батрак: Получил работу: $value")
                Thread.sleep(500)
            }
            println("Батрак: Сделал работу $value. Прошло времени с начала работы: $elapsedTime")
        }
    }

    producer.start()
    consumer.start()

    producer.join()
    consumer.join()
}

// ===== Задание 5: Fair ArrayBlockingQueue =====

fun task5_fairQueue() {

    val окноПолученияЗарплаты = ArrayBlockingQueue<String>(1, true)

    val батракСчетовод = Thread({
        val name = Thread.currentThread().name
        repeat(5) {
            println("$name: Ну, Кто там следующий?")
            val батракЗаЗарплатой = окноПолученияЗарплаты.take()
            println("$name: Выдаю зарплату батраку ${батракЗаЗарплатой}")
            Thread.sleep(500)
        }

        println("$name: Все, всем зарплату раздал")

    }, "Батрак счетовод")

    val батракиРаботяги = (1..5).map {
        Thread {
            val name = "Батрак-$it"
            println("$name: Я пришел за зарплатой! Наконец-то зарплата")
            окноПолученияЗарплаты.put(name)
            println("$name: Наконец-то мне выдадут зарплату!")
        }
    }.onEach { it.start(); Thread.sleep(50) }

    Thread.sleep(500)

    батракСчетовод.start()
    батракСчетовод.join()
    батракиРаботяги.onEach { it.join() }
}

fun task5_unfairQueue() {

    val окноПолученияЗарплаты = ArrayBlockingQueue<String>(1, false)

    val батракСчетовод = Thread({
        val name = Thread.currentThread().name
        repeat(5) {
            println("$name: Ну, Кто там следующий?")
            val батракЗаЗарплатой = окноПолученияЗарплаты.take()
            println("$name: Выдаю зарплату батраку ${батракЗаЗарплатой}")
            Thread.sleep(100)
        }

        println("$name: Все, всем зарплату раздал")

    }, "Батрак счетовод")

    val батракиРаботяги = (1..5).map {
        Thread {
            val name = "Батрак-$it"
            println("$name: Я пришел за зарплатой! Наконец-то зарплата")
            окноПолученияЗарплаты.put(name)
            println("$name: Наконец-то мне выдадут зарплату!")
        }
    }.onEach { it.start(); Thread.sleep(10) }

    Thread.sleep(500)

    батракСчетовод.start()
    батракСчетовод.join()
    батракиРаботяги.onEach { it.join() }
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

    println("\n=== Task 5: Unfair ArrayBlockingQueue ===")
    task5_unfairQueue()
}
