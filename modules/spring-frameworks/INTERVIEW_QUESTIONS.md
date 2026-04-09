# Interview Questions — Spring Frameworks

## Spring Core / DI / IoC

**Q1. В чём разница между IoC и DI?**

IoC (Inversion of Control) — принцип: управление жизненным циклом объектов передаётся контейнеру. DI (Dependency Injection) — конкретная реализация IoC: зависимости передаются объекту извне (через конструктор, setter или поле). DI — это один из способов реализовать IoC.

**Q2. Почему constructor injection предпочтительнее field injection?**

- `final` поля — иммутабельность, защита от NPE
- Явные зависимости в сигнатуре — видно без чтения тела класса
- Тестируется без Spring: `new Service(mockA, mockB)`
- Spring обнаруживает circular dependencies при старте, не в рантайме
- Field injection требует рефлексии, обходит инкапсуляцию

**Q3. Что такое Bean Scope? Какие бывают?**

Scope определяет, сколько экземпляров бина создаёт контейнер:
- `singleton` (дефолт) — один на ApplicationContext
- `prototype` — новый при каждом запросе из контейнера
- `request` — один на HTTP-запрос (только Web)
- `session` — один на HTTP-сессию
- `application` — один на ServletContext

Проблема: prototype в singleton — singleton всегда получает один и тот же prototype. Решение: `ObjectFactory<T>` или `Provider<T>`.

**Q4. Как работает AOP в Spring? Что такое self-invocation проблема?**

Spring AOP реализован через прокси (CGLIB или JDK Proxy). При вызове метода извне — прокси перехватывает вызов и применяет advice (@Transactional, @Cacheable и т.д.). Self-invocation — вызов метода из того же класса (`this.method()`) обходит прокси → `@Transactional`, `@Cacheable` не работают. Решение: вынести метод в другой бин или получить ссылку на прокси через `AopContext.currentProxy()`.

**Q5. Как Spring обнаруживает и разрешает Circular Dependencies?**

При constructor injection — бросает `BeanCurrentlyInCreationException` при старте (до рантайма). При field/setter injection — может разрешить через ранние ссылки (early references). Решения: рефакторинг (чаще всего — нарушение SRP), `@Lazy`, setter injection, выделение общего третьего бина.

---

## Spring Boot & Auto-Configuration

**Q6. Как работает Auto-Configuration в Spring Boot?**

`@EnableAutoConfiguration` сканирует `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` во всех jar в classpath. Каждый класс авто-конфигурации аннотирован `@Conditional*` — применяется только при выполнении условий (например, `@ConditionalOnClass(DataSource.class)` + `@ConditionalOnMissingBean`). Это позволяет пользователю переопределить любой бин, зарегистрировав свой.

**Q7. Что такое Spring Boot Starter? Как устроен изнутри?**

Starter — Maven/Gradle артефакт, содержащий только `pom.xml` с транзитивными зависимостями. Логика авто-конфигурации находится в отдельных `*-autoconfigure` артефактах. Например, `spring-boot-starter-web` транзитивно подтягивает spring-webmvc, embedded Tomcat, Jackson и активирует `WebMvcAutoConfiguration`.

**Q8. Как отладить авто-конфигурацию?**

Запустить с `--debug` или `debug=true` в application.properties. Spring выведет `CONDITIONS EVALUATION REPORT` с перечнем: какие авто-конфигурации применились (Positive matches), какие нет (Negative matches) и почему.

---

## Spring Data JPA & Hibernate

**Q9. Что такое N+1 проблема и как её решить?**

При загрузке списка сущностей с LAZY связью: 1 запрос за список + N запросов за каждую связь. Решения:
1. `JOIN FETCH` в JPQL — один запрос со JOIN
2. `@EntityGraph` — аналогично, но декларативно
3. `@BatchSize(size = N)` — загружает пачками IN (...) вместо по одному
4. DTO projection через `new ClassName(...)` в JPQL — самый эффективный, только нужные поля

**Q10. Опиши три уровня кэширования в Hibernate.**

- **L1 (First Level)** — всегда включён, область: одна Session. Гарантирует идентичность объектов внутри транзакции. При повторном `findById` — SQL не выполняется.
- **L2 (Second Level)** — опциональный, область: SessionFactory (всё приложение). Реализации: EHCache, Caffeine, Infinispan. Настраивается через `@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`. Инвалидируется при save/delete сущности.
- **Query Cache** — кэширует список ID результатов JPQL/HQL запросов. Требует L2. Инвалидируется при любом изменении таблицы.

**Q11. Что делает @Transactional и что такое Propagation?**

`@Transactional` через AOP-прокси оборачивает метод: начинает транзакцию до вызова, коммитит после, откатывает при `RuntimeException`. Propagation определяет поведение при вызове из уже существующей транзакции:
- `REQUIRED` (дефолт) — использует текущую или создаёт новую
- `REQUIRES_NEW` — всегда создаёт новую, текущую приостанавливает
- `NESTED` — создаёт savepoint, откат только до savepoint
- `SUPPORTS` — использует если есть, без транзакции если нет

**Q12. Чем отличается Optimistic от Pessimistic Locking в Spring Data?**

**Optimistic** (`@Version`): Hibernate добавляет `WHERE version = ?` к UPDATE. Если версия изменилась — `OptimisticLockException`. Нет блокировки в БД, подходит для высокой конкурентности при редких конфликтах.

**Pessimistic** (`@Lock(PESSIMISTIC_WRITE)`): генерирует `SELECT ... FOR UPDATE`. Блокирует строку в БД до commit. Подходит при высокой вероятности конфликта (финансовые операции).

---

## Spring Security

**Q13. Как устроена цепочка фильтров в Spring Security?**

`DelegatingFilterProxy` (Servlet Filter) делегирует в `FilterChainProxy`, который выбирает подходящую `SecurityFilterChain`. Запрос проходит через фильтры по порядку: аутентификация (UsernamePasswordAuthenticationFilter, BearerTokenAuthenticationFilter) → `ExceptionTranslationFilter` → `AuthorizationFilter`. Любой фильтр может прервать цепочку.

**Q14. Что такое SecurityContext и почему важно его propagation?**

`SecurityContext` хранится в `SecurityContextHolder` через **ThreadLocal** — привязан к текущему потоку. При `@Async` — новый поток не имеет доступа к контексту. Решение: `DelegatingSecurityContextAsyncTaskExecutor` или `DelegatingSecurityContextExecutor` для propagation контекста в другие потоки.

**Q15. Когда нужен CSRF и когда нет?**

CSRF нужен для stateful-приложений с cookie-аутентификацией (браузер автоматически отправляет cookie). Для REST API с JWT в `Authorization: Bearer` заголовке — CSRF **не нужен**: браузер не добавляет заголовки автоматически. Поэтому `.csrf(AbstractHttpConfigurer::disable)` корректен для stateless REST API.

---

## Spring Cloud

**Q16. Зачем нужен @RefreshScope и как он работает?**

`@RefreshScope` помечает бин для пересоздания при обновлении конфигурации. При вызове `/actuator/refresh` Spring уничтожает бин и создаёт новый с обновлёнными значениями `@Value` из Config Server. Без `@RefreshScope` — бин создаётся один раз, значения не обновляются.

**Q17. Как Circuit Breaker защищает от каскадных сбоев?**

CB отслеживает последние N вызовов. При превышении порога ошибок (failureRateThreshold) переходит в OPEN — все вызовы немедленно возвращают fallback (нет запросов к упавшему сервису). После паузы (waitDurationInOpenState) — HALF_OPEN: пропускает несколько пробных запросов. Если успешны → CLOSED, иначе → снова OPEN. Это предотвращает перегрузку нестабильного сервиса и быстро возвращает ответ клиенту.
