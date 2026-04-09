# Spring: DI, IoC и GoF паттерны

## Что решает Spring

До Spring (ранние 2000-е) типичный Java-код:

```java
// Проблема: жёсткая связность
class OrderService {
    private PaymentService payment = new PaymentService();       // конкретика
    private EmailService email = new EmailNotificationService(); // сложно заменить
    private OrderRepository repo = new MySQLOrderRepository();   // сложно тестировать
}
```

**Проблемы:**
1. **Жёсткая связность (tight coupling)** — нельзя заменить реализацию без правки кода
2. **Тяжело тестировать** — нельзя подставить mock без правки класса
3. **Управление жизненным циклом** — кто создаёт, кто уничтожает?
4. **Кросс-срезающие заботы (cross-cutting concerns)** — логирование, транзакции, безопасность дублируются везде
5. **Конфигурация** — хардкод настроек в коде

---

## IoC (Inversion of Control)

**Инверсия управления** — управление созданием объектов и их жизненным циклом передаётся контейнеру (фреймворку), а не разработчику.

```
Традиционно: код вызывает фреймворк
IoC:          фреймворк управляет кодом
```

**Hollywood Principle:** "Don't call us, we'll call you" — ты не создаёшь объекты, контейнер создаёт и внедряет их.

---

## DI (Dependency Injection)

DI — конкретная реализация IoC. Зависимости передаются объекту снаружи (не создаются внутри).

### Три способа внедрения

```java
// 1. Constructor Injection (рекомендуется)
@Service
public class OrderService {
    private final PaymentService payment;
    private final OrderRepository repo;

    public OrderService(PaymentService payment, OrderRepository repo) {
        this.payment = payment;   // final — иммутабельно
        this.repo = repo;
    }
}
// Spring автоматически внедряет если один конструктор (без @Autowired)

// 2. Setter Injection (опциональные зависимости)
@Service
public class NotificationService {
    private EmailService email;

    @Autowired(required = false)
    public void setEmailService(EmailService email) {
        this.email = email;
    }
}

// 3. Field Injection (не рекомендуется: нельзя final, сложно тестировать)
@Service
public class UserService {
    @Autowired
    private UserRepository repo; // плохо
}
```

**Почему constructor injection лучше:**
- `final` поля — иммутабельны, нет NPE
- Явные зависимости в сигнатуре конструктора
- Легко тестировать: `new OrderService(mockPayment, mockRepo)`
- Нет риска circular dependency в рантайме (Spring обнаруживает при старте)

---

## Spring Container (ApplicationContext)

```java
// Spring Boot: ApplicationContext создаётся автоматически
@SpringBootApplication
public class App { public static void main(String[] args) { SpringApplication.run(App.class, args); } }

// Регистрация бинов:
@Component    // общий
@Service      // сервисный слой (semantic alias)
@Repository   // доступ к данным (+ exception translation)
@Controller   // MVC контроллер
@RestController = @Controller + @ResponseBody

@Configuration + @Bean:
@Configuration
public class Config {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
```

### Bean Scopes

| Scope | Описание | Применение |
|-------|----------|-----------|
| `singleton` | Один экземпляр на контейнер (дефолт) | Stateless сервисы |
| `prototype` | Новый экземпляр каждый раз | Stateful объекты |
| `request` | Один на HTTP-запрос | Web: данные запроса |
| `session` | Один на HTTP-сессию | Web: данные сессии |
| `application` | Один на ServletContext | Singleton для Web |

```java
@Component
@Scope("prototype")
public class ReportGenerator { ... }

// Внедрить prototype в singleton — проблема!
// Решение: ObjectFactory или Provider:
@Service
public class ReportService {
    private final ObjectFactory<ReportGenerator> factory;

    public void generateReport() {
        ReportGenerator generator = factory.getObject(); // новый каждый раз
    }
}
```

---

## GoF паттерны в Spring

### Factory (Фабрика)

`BeanFactory` / `ApplicationContext` — основной GoF Factory в Spring.

```java
// Spring сам является фабрикой
ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
OrderService service = ctx.getBean(OrderService.class);

// FactoryBean — для сложного создания бинов
@Component
public class ConnectionPoolFactoryBean implements FactoryBean<DataSource> {
    @Override
    public DataSource getObject() throws Exception {
        return createHikariPool(); // сложная инициализация
    }
    @Override
    public Class<?> getObjectType() { return DataSource.class; }
}
```

### Singleton

Scope `singleton` — один экземпляр на ApplicationContext.

```java
// Управляемый Spring singleton — потокобезопасный при stateless реализации
@Service // singleton scope by default
public class PriceCalculator {
    // Нет состояния → нет race conditions
    public BigDecimal calculate(Order order) { ... }
}
```

### Proxy (Прокси)

Основа AOP в Spring. Spring создаёт прокси поверх бина для кросс-срезающих забот.

```java
// Транзакции — @Transactional через прокси
@Service
public class OrderService {
    @Transactional
    public void placeOrder(Order order) {
        repo.save(order);         // Spring оборачивает вызов транзакцией
        payment.charge(order);    // если исключение → rollback
    }
}
// Spring создаёт CGLIB или JDK Proxy поверх OrderService

// Кэширование — @Cacheable
@Service
public class ProductService {
    @Cacheable("products")
    public Product findById(Long id) {
        return repo.findById(id).orElseThrow(); // вызывается только если нет в кэше
    }

    @CacheEvict("products")
    public void update(Product product) { repo.save(product); }
}

// Безопасность — @PreAuthorize тоже через прокси
```

**AOP терминология:**
- **Aspect** — класс с кросс-срезающей логикой (`@Aspect`)
- **Advice** — код, выполняющийся в нужный момент (`@Before`, `@After`, `@Around`)
- **Pointcut** — выражение, какие методы перехватывать
- **Join Point** — точка перехвата (вызов метода)

```java
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* by.pavel.service.*.*(..))") // pointcut
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // вызов реального метода
        log.info("{} took {}ms", joinPoint.getSignature(), System.currentTimeMillis() - start);
        return result;
    }
}
```

**Ограничения Spring Proxy:** прокси работает только для вызовов через бин. Self-invocation (вызов `@Transactional` метода из того же класса) — транзакция НЕ создаётся!

### Template Method

`JdbcTemplate`, `RestTemplate`, `KafkaTemplate`, `RedisTemplate` — AbstractTemplate скрывает бойлерплейт, оставляет точки расширения.

```java
// JdbcTemplate убирает: получение соединения, PreparedStatement, обработку ResultSet, закрытие
List<User> users = jdbcTemplate.query(
    "SELECT * FROM users WHERE active = ?",
    (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name")), // mapper — точка расширения
    true
);

// RestTemplate
ResponseEntity<Order> response = restTemplate.getForEntity(
    "http://order-service/api/orders/{id}", Order.class, orderId
);
```

### Observer (Наблюдатель)

Spring Events — ApplicationEventPublisher / @EventListener:

```java
// Событие
public record OrderPlaced(Order order) {}

// Публикатор
@Service
public class OrderService {
    private final ApplicationEventPublisher publisher;

    public void placeOrder(Order order) {
        repo.save(order);
        publisher.publishEvent(new OrderPlaced(order)); // fire-and-forget
    }
}

// Подписчик
@Component
public class NotificationService {
    @EventListener
    public void onOrderPlaced(OrderPlaced event) {
        emailService.sendConfirmation(event.order());
    }

    @EventListener
    @Async  // асинхронно
    public void onOrderPlacedAsync(OrderPlaced event) {
        smsService.notify(event.order());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT) // только если транзакция успешна
    public void onOrderPlacedTransactional(OrderPlaced event) {
        kafkaProducer.send("orders", event.order());
    }
}
```

### Strategy (Стратегия)

Несколько реализаций одного интерфейса, выбор стратегии.

```java
interface PaymentStrategy { void pay(Order order); }

@Component("stripe")  class StripePayment implements PaymentStrategy { ... }
@Component("paypal")  class PaypalPayment implements PaymentStrategy { ... }

// Выбор стратегии:
@Service
public class PaymentService {
    private final Map<String, PaymentStrategy> strategies;

    public PaymentService(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies; // Spring инжектирует Map<name, bean>
    }

    public void pay(String method, Order order) {
        strategies.getOrDefault(method, strategies.get("stripe")).pay(order);
    }
}
```

### Decorator (Декоратор)

Добавление поведения без изменения класса.

```java
// Кэширующий декоратор поверх репозитория
@Primary
@Component
public class CachingUserRepository implements UserRepository {
    private final UserRepository delegate;
    private final Cache cache;

    @Override
    public Optional<User> findById(Long id) {
        return cache.get(id, () -> delegate.findById(id));
    }

    @Override
    public void save(User user) {
        delegate.save(user);
        cache.evict(user.id());
    }
}
```

---

## Circular Dependencies

```java
@Service class A { A(B b) {...} }
@Service class B { B(A a) {...} }
// → BeanCurrentlyInCreationException при старте
```

**Решения:**
1. Рефакторинг — убрать circular dependency (обычно нарушение SRP)
2. `@Lazy` — отложить создание одного из бинов
3. Setter injection вместо constructor (не рекомендуется)
4. Общий третий сервис, которому оба делегируют

---

## Spring Boot Auto-Configuration

```java
// Spring Boot ищет @AutoConfiguration классы через META-INF/spring/...
// Условная конфигурация:
@AutoConfiguration
@ConditionalOnClass(DataSource.class)            // если в classpath есть DataSource
@ConditionalOnMissingBean(DataSource.class)      // если пользователь не определил свой
@ConditionalOnProperty("spring.datasource.url")  // если свойство задано
public class DataSourceAutoConfiguration {
    @Bean
    public DataSource dataSource(DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }
}
```
