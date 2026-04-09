# Apache Kafka

## Ключевые концепции

```
Producer → [Topic: orders] → Consumer Group
               │
          ┌────┴────┐
     Partition 0  Partition 1  Partition 2
     [msg1]       [msg2]       [msg3]
     [msg4]       [msg5]
     [msg6]
```

**Topic** — логический канал сообщений. Разбит на **partitions**.  
**Partition** — упорядоченный, иммутабельный лог. Каждое сообщение получает монотонно растущий **offset**.  
**Consumer Group** — группа потребителей, каждую партицию читает ровно один consumer группы.  
**Broker** — сервер Kafka. Обычно кластер из 3+ брокеров.  
**Replication Factor** — сколько копий каждой партиции хранится. RF=3 → выдержит падение 2 брокеров.

---

## Гарантия порядка сообщений

**Порядок гарантирован внутри одной партиции.** Между партициями — нет.

```
Producer отправляет: order:A → partition 0
Producer отправляет: order:B → partition 0
Consumer читает partition 0: order:A, затем order:B ✓

Producer отправляет: order:A → partition 0
Producer отправляет: order:B → partition 1
Порядок между A и B НЕ гарантирован ✗
```

**Как обеспечить порядок для связанных сообщений:**
```java
// Указать ключ — все сообщения с одним ключом → одна и та же партиция
producer.send(new ProducerRecord<>("orders", userId, orderEvent));
// partition = hash(key) % numPartitions
```

Все события одного пользователя (один `userId`) → всегда одна партиция → гарантированный порядок.

**Проблема:** если партиций больше, чем активных пользователей → некоторые партиции пустые.  
**Проблема:** при добавлении партиций перераспределение ключей → один пользователь может попасть в другую партицию.

---

## Гарантии доставки

### At Most Once (максимум один раз)

Сообщение может потеряться, но никогда не дублируется.

```
Producer: send → Broker
Consumer: commit offset СРАЗУ, потом обрабатывает
→ Если упал после commit, до обработки → сообщение потеряно
```

**Настройка producer:** `acks=0` (не ждёт подтверждения от брокера).  
**Когда:** метрики, non-critical логи, где потеря допустима.

### At Least Once (минимум один раз) — дефолт

Сообщение доставлено минимум один раз. Дублирование возможно.

```
Producer: send → Broker подтвердил (acks=1 или all)
Consumer: обработать → commit offset
→ Если упал после обработки, до commit → при рестарте читает снова → дубль
```

**Настройка producer:** `acks=1` или `acks=all`, `retries > 0`.  
**Требование:** consumer должен быть **идемпотентным** (обработка одного сообщения дважды = тот же результат).  
**Идемпотентность:** хранить обработанные ID и игнорировать дубли, или операция от природы идемпотентна (INSERT ON CONFLICT DO NOTHING, SET value = X).

### Exactly Once (ровно один раз)

Сложнее всего — ни потерь, ни дублей.

**Idempotent Producer** (гарантия от producer-side дублей):
```java
props.put("enable.idempotence", true);
// Каждый producer получает PID, каждое сообщение — sequence number
// Broker дедуплицирует: если seq_num уже видел → игнорирует
```
Защищает от дублей из-за retry producer'а, но не защищает от дублей на стороне consumer.

**Transactional Producer + Consumer** (end-to-end exactly once):
```java
// Producer
props.put("transactional.id", "my-transactional-producer");
producer.initTransactions();
producer.beginTransaction();
producer.send(record1);
producer.send(record2);
producer.commitTransaction(); // атомарно
// или producer.abortTransaction();
```

```java
// Consumer: читать только закоммиченные сообщения
props.put("isolation.level", "read_committed");
```

**Kafka Streams exactly-once:** consume + transform + produce атомарно. Настраивается через `processing.guarantee=exactly_once_v2`.

**Ограничения exactly-once:**
- Только внутри Kafka (producer → broker → consumer → producer другого топика)
- При записи в внешние системы (БД, API) — нужна идемпотентность на стороне этих систем

---

## Consumer Group — балансировка

```
Topic: 3 partitions, Consumer Group: 3 consumers
C1 → P0
C2 → P1
C3 → P2   ← идеально, 1:1

Consumer Group: 2 consumers
C1 → P0, P1   ← C1 читает две партиции
C2 → P2

Consumer Group: 4 consumers
C1 → P0
C2 → P1
C3 → P2
C4 → (idle)   ← лишний consumer ничего не читает
```

**Rebalance** — при добавлении/удалении consumer перераспределяются партиции. Во время rebalance потребление останавливается. Решение: Cooperative Sticky Assignor (минимально перемещает партиции).

---

## Retention и Log Compaction

**Retention by time:** `retention.ms=604800000` (7 дней — дефолт). Старые сообщения удаляются.

**Retention by size:** `retention.bytes` — удалять если размер партиции превышает лимит.

**Log Compaction** — для топиков типа "last value per key". Kafka хранит только последнее сообщение с каждым ключом.

```
До compaction:  [key=A,v=1] [key=B,v=1] [key=A,v=2] [key=B,v=2] [key=A,v=3]
После:          [key=B,v=2] [key=A,v=3]
```

**Применение:** снимки состояния (changelog topics в Kafka Streams), таблицы Reference Data.  
Tombstone: сообщение с `null`-значением удаляет ключ после compaction.

---

## Producer настройки

```java
// Надёжность
props.put("acks", "all");          // ждать подтверждения от всех ISR (in-sync replicas)
props.put("retries", 3);
props.put("retry.backoff.ms", 100);

// Батчинг (throughput vs latency)
props.put("batch.size", 16384);    // размер батча (16KB)
props.put("linger.ms", 5);         // ждать 5ms для накопления батча

// Идемпотентность
props.put("enable.idempotence", true); // автоматически ставит acks=all, retries=Integer.MAX
```

**acks=0:** fire-and-forget, максимальный throughput, возможна потеря.  
**acks=1:** лидер записал, реплики могут не успеть (при падении лидера — потеря).  
**acks=all (или -1):** все ISR записали → максимальная надёжность, выше latency.

---

## Kafka vs другие очереди

| | Kafka | RabbitMQ | Amazon SQS |
|---|---|---|---|
| Модель | Pull (consumer тянет) | Push (брокер толкает) | Pull |
| Retention | Дни/недели, replay | Удаляет после ACK | До 14 дней |
| Порядок | Внутри партиции | Очередь FIFO (1 consumer) | Без гарантий |
| Пропускная способность | Очень высокий | Средний | Высокий (managed) |
| Replay | Да (перемотать offset) | Нет | Нет |
| Паттерн | Event streaming, log | Task queue, routing | Decoupling сервисов |
