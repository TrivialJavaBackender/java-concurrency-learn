# Testing — JUnit & Mockito

## Пирамида тестирования

```
         /\
        /  \
       / E2E\        ← мало, медленно, дорого
      /------\
     /  Integ \      ← умеренно
    /----------\
   /    Unit    \    ← много, быстро, дёшево
  /______________\
```

**Unit тесты** — тестируют одну единицу в изоляции (класс, метод). Моки вместо зависимостей. Быстрые, детерминированные.

**Integration тесты** — тестируют взаимодействие нескольких компонентов (сервис + реальная БД, сервис + Kafka). Медленнее, но ловят ошибки на стыках.

**E2E (End-to-End)** — тестируют всю систему через API. Самые медленные и хрупкие. Минимальное количество.

**Антипаттерн "Ice Cream Cone"** — перевёрнутая пирамида: много E2E, мало unit. Тесты медленные, ненадёжные.

---

## JUnit 5 — основные концепции

### Аннотации жизненного цикла

```java
@BeforeAll   // один раз перед всеми тестами в классе (static)
@BeforeEach  // перед каждым тестом
@AfterEach   // после каждого теста
@AfterAll    // один раз после всех тестов (static)
```

### Основные аннотации

```java
@Test           // тестовый метод
@Disabled       // пропустить тест (с причиной)
@DisplayName("Читаемое имя теста")
@Nested         // вложенный класс для группировки связанных тестов
@Tag("slow")    // метка для фильтрации при запуске

@ParameterizedTest
@ValueSource(ints = {1, 2, 3})
void test(int value) { ... }
```

### Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(expected, actual);
assertNotEquals(unexpected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(value);
assertNotNull(value);

// Проверка исключения:
assertThrows(IllegalArgumentException.class, () -> service.transfer(-100));

// Несколько проверок — все выполняются, даже если одна упала:
assertAll(
    () -> assertEquals("Alice", user.getName()),
    () -> assertEquals(25, user.getAge())
);

// Сообщение при провале (lambda — не вычисляется если тест прошёл):
assertEquals(expected, actual, () -> "Failed for input: " + input);
```

### Параметризованные тесты

```java
@ParameterizedTest
@CsvSource({
    "1, 2, 3",
    "10, 20, 30"
})
void addTest(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}

@ParameterizedTest
@MethodSource("provideReservationCases")
void reservationTest(LocalDate date, TimeSlot slot, boolean expected) { ... }

static Stream<Arguments> provideReservationCases() {
    return Stream.of(
        Arguments.of(LocalDate.now(), new TimeSlot(10, 12), true),
        Arguments.of(LocalDate.now(), new TimeSlot(11, 13), false) // пересечение
    );
}
```

---

## Mockito — основные концепции

### Создание моков

```java
// Через аннотацию (нужен MockitoExtension):
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    ReservationRepository repository;

    @InjectMocks
    ReservationService service; // зависимости инжектятся автоматически
}

// Или явно:
ReservationRepository repository = mock(ReservationRepository.class);
```

### Stubbing (настройка поведения)

```java
// when().thenReturn() — базовый случай
when(repository.findByDate(date)).thenReturn(List.of(reservation1));

// thenThrow — выбросить исключение
when(repository.save(any())).thenThrow(new RuntimeException("DB error"));

// thenAnswer — сложная логика
when(repository.findById(anyInt())).thenAnswer(invocation -> {
    int id = invocation.getArgument(0);
    return id > 0 ? Optional.of(new Table(id)) : Optional.empty();
});

// doReturn/doThrow — для void-методов и spy:
doNothing().when(repository).delete(any());
doThrow(new RuntimeException()).when(repository).delete(null);
```

### Argument Matchers

```java
any()          // любой объект (не null)
any(Class.class) // объект конкретного типа
anyInt(), anyString(), anyList() // примитивы и коллекции
eq(value)      // точное совпадение (обязателен если другие аргументы — матчеры)
isNull()
isNotNull()
argThat(predicate) // кастомное условие

// Все аргументы должны быть матчерами или значениями:
// НЕЛЬЗЯ: when(repo.find(1, any())) — смешивать нельзя
// НУЖНО:  when(repo.find(eq(1), any()))
```

### Verify — проверка вызовов

```java
verify(repository).save(reservation); // вызван ровно 1 раз с этим аргументом
verify(repository, times(2)).save(any());
verify(repository, never()).delete(any());
verify(repository, atLeast(1)).findByDate(any());
verify(repository, atMost(3)).findByDate(any());

// Проверить, что больше никаких взаимодействий не было:
verifyNoMoreInteractions(repository);

// Порядок вызовов:
InOrder inOrder = inOrder(repository, eventBus);
inOrder.verify(repository).save(reservation);
inOrder.verify(eventBus).publish(any());
```

### Capture — захват аргументов

```java
ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
verify(repository).save(captor.capture());
Reservation saved = captor.getValue();
assertEquals(tableId, saved.getTableId());
```

### Spy — частичный мок

```java
ReservationService spyService = spy(new ReservationService(repository));

// Реальный метод, кроме getDefaultSlot:
doReturn(new TimeSlot(10, 22)).when(spyService).getDefaultSlot();
```

---

## Практические паттерны

### Структура теста — Given/When/Then (AAA)

```java
@Test
void makeReservation_whenTableAvailable_savesReservation() {
    // Given (Arrange)
    LocalDate date = LocalDate.of(2026, 4, 8);
    when(repository.findByDate(date)).thenReturn(Collections.emptyList());

    // When (Act)
    service.makeReservation(date, new TimeSlot(10, 12), tableId, 2);

    // Then (Assert)
    ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
    verify(repository).save(captor.capture());
    assertEquals(tableId, captor.getValue().getTableId());
}
```

### Тестирование исключений

```java
@Test
void transfer_whenInsufficientBalance_throwsException() {
    when(accountRepository.findById(fromId)).thenReturn(Optional.of(new Account(fromId, BigDecimal.ONE)));
    when(accountRepository.findById(toId)).thenReturn(Optional.of(new Account(toId, BigDecimal.ZERO)));

    IllegalStateException ex = assertThrows(
        IllegalStateException.class,
        () -> bankService.transfer(idempotencyKey, fromId, toId, BigDecimal.TEN)
    );
    assertTrue(ex.getMessage().contains("Insufficient balance"));
}
```

### Тестирование конкурентности

```java
@Test
void makeReservation_concurrent_noDoubleBooking() throws InterruptedException {
    int threads = 10;
    CountDownLatch latch = new CountDownLatch(threads);
    AtomicInteger successCount = new AtomicInteger(0);

    ExecutorService executor = Executors.newFixedThreadPool(threads);
    for (int i = 0; i < threads; i++) {
        executor.submit(() -> {
            try {
                service.makeReservation(date, slot, tableId, 2);
                successCount.incrementAndGet();
            } catch (NoAvailableTimeSlotException e) {
                // ожидаемо для конкурирующих
            } finally {
                latch.countDown();
            }
        });
    }

    latch.await(5, TimeUnit.SECONDS);
    assertEquals(1, successCount.get()); // только один успех
}
```

### Integration тест с @SpringBootTest

```java
@SpringBootTest
@Transactional
class ReservationServiceIntegrationTest {

    @Autowired
    ReservationService service;

    @Autowired
    ReservationRepository repository;

    @Test
    void makeReservation_persists() {
        service.makeReservation(date, slot, tableId, 2);
        List<Reservation> found = repository.findByDate(date);
        assertEquals(1, found.size());
    }
    // @Transactional откатит изменения после каждого теста
}
```

---

## Что мокать, а что нет

**Мокать:**
- Внешние зависимости: repository, HTTP-клиенты, email-сервисы
- Медленные операции: БД, файловая система
- Недетерминированные вещи: `Clock`, `Random`, UUID-генераторы

**Не мокать:**
- Value objects и простые классы без side-эффектов
- Сам тестируемый объект (тестируешь мок — не тестируешь ничего)
- БД в интеграционных тестах — используй реальную (H2 / TestContainers)

**TestContainers** — запуск реального PostgreSQL в Docker для интеграционных тестов:
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

@DynamicPropertySource
static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
}
```
