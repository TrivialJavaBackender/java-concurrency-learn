# Аутентификация и безопасность

## JWT (JSON Web Token)

### Структура

JWT = три части, разделённые точками: `header.payload.signature`

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIzIiwicm9sZXMiOlsiVVNFUiJdLCJleHAiOjE3MDA...}.<signature>
```

**Header** (base64url):
```json
{ "alg": "HS256", "typ": "JWT" }
```

**Payload** (base64url) — claims:
```json
{
  "sub": "user123",         // subject
  "iss": "auth-service",    // issuer
  "aud": "api-service",     // audience
  "exp": 1700000000,        // expiration (unix timestamp)
  "iat": 1699999000,        // issued at
  "roles": ["USER", "ADMIN"]
}
```

**Signature:**
```
HMAC-SHA256(base64url(header) + "." + base64url(payload), secretKey)
// или RSA/ECDSA для asymmetric (RS256/ES256)
```

### Валидация JWT (сервер)

1. Проверить структуру (3 части)
2. Декодировать header → алгоритм
3. Проверить подпись (HMAC или RSA)
4. Проверить `exp` — не просрочен
5. Проверить `iss`, `aud` — ожидаемый источник/получатель
6. Проверить `nbf` (not before) если есть

```java
// Spring: JwtDecoder + @EnableMethodSecurity
Jwt jwt = jwtDecoder.decode(token);
jwt.getClaimAsString("sub");
jwt.getClaimAsStringList("roles");
```

### Stateless vs Stateful аутентификация

| | Session (stateful) | JWT (stateless) |
|---|---|---|
| Хранение | Session ID в cookie, данные на сервере | Все данные в токене |
| Масштабирование | Нужен sticky session или distributed session store (Redis) | Нет серверного состояния |
| Отзыв | Удалить сессию → мгновенно | Нельзя отозвать до exp → нужен blocklist |
| Размер | Маленький cookie | JWT может быть большим |
| Применение | Монолит, Web App | Микросервисы, SPA, mobile |

### Отзыв JWT (Revocation)

JWT stateless — нет механизма отзыва без сервера. Решения:
1. **Короткий TTL** (5-15 минут) + refresh token с длинным TTL
2. **Blocklist в Redis** — хранить jti (JWT ID) отозванных токенов до exp
3. **Refresh token rotation** — при каждом обновлении выдавать новый refresh, старый инвалидировать

---

## OAuth 2.0

**OAuth 2.0** — фреймворк авторизации. Позволяет третьим сторонам получить доступ к ресурсам пользователя без раскрытия пароля.

### Роли

| Роль | Описание | Пример |
|------|----------|--------|
| Resource Owner | Пользователь | Вы |
| Client | Приложение, запрашивающее доступ | Мобильное приложение |
| Authorization Server | Выдаёт токены | Google, Keycloak, Auth0 |
| Resource Server | Защищённый API | Google Calendar API |

### Grant Types (потоки)

#### Authorization Code (с PKCE — рекомендуется для SPA/mobile)

```
1. Client → Authorization Server:
   GET /authorize?response_type=code&client_id=...&redirect_uri=...&scope=...&code_challenge=...

2. Пользователь логинится и соглашается

3. Authorization Server → Client (redirect):
   https://myapp.com/callback?code=AUTH_CODE

4. Client → Authorization Server (backend):
   POST /token
   { grant_type: authorization_code, code: AUTH_CODE, code_verifier: ... }

5. Authorization Server → Client:
   { access_token, refresh_token, expires_in }

6. Client → Resource Server:
   GET /api/data
   Authorization: Bearer ACCESS_TOKEN
```

**PKCE (Proof Key for Code Exchange):** `code_verifier` (random) → `code_challenge = SHA256(code_verifier)`. Защищает от перехвата кода.

#### Client Credentials (machine-to-machine)

```
Service A → Authorization Server:
POST /token
{ grant_type: client_credentials, client_id, client_secret, scope }

→ { access_token }
```

Нет пользователя. Используется для сервис-к-сервис.

#### Refresh Token

```
Client → Authorization Server:
POST /token
{ grant_type: refresh_token, refresh_token: REFRESH }

→ { access_token, [new refresh_token] }
```

### OpenID Connect (OIDC)

OIDC — слой идентификации поверх OAuth 2.0. Добавляет **ID Token** (JWT с информацией о пользователе).

```
scope=openid → получаем id_token + access_token
scope=openid profile email → + имя, email в id_token

// id_token claims:
{ sub, iss, aud, exp, iat, name, email, picture }
```

OAuth 2.0 — авторизация (что можно делать).  
OIDC — аутентификация (кто ты).

---

## Spring Security

### Архитектура: Filter Chain

```
HTTP Request
     ↓
DelegatingFilterProxy (Servlet Filter)
     ↓
FilterChainProxy
     ↓
SecurityFilterChain (цепочка фильтров):
  UsernamePasswordAuthenticationFilter
  BearerTokenAuthenticationFilter (JWT)
  ExceptionTranslationFilter
  AuthorizationFilter
     ↓
DispatcherServlet → Controller
```

Каждый запрос проходит через цепочку фильтров. Если аутентификация/авторизация не прошла — фильтр бросает исключение или возвращает 401/403.

### SecurityContext

```java
// Текущий пользователь доступен через:
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<GrantedAuthority> authorities = auth.getAuthorities();

// В контроллере:
@GetMapping("/profile")
public String profile(@AuthenticationPrincipal UserDetails user) {
    return user.getUsername();
}
```

`SecurityContextHolder` хранит в ThreadLocal → для виртуальных потоков или async нужно настраивать propagation.

### Конфигурация (Spring Security 6+)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)  // для stateless REST API
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            )
            .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
        // или с публичным ключом RSA:
        // NimbusJwtDecoder.withPublicKey(rsaPublicKey).build()
    }
}
```

### Method Security

```java
@EnableMethodSecurity
// ...

@GetMapping("/orders/{id}")
@PreAuthorize("hasRole('USER') and #id == authentication.principal.id")
public Order getOrder(@PathVariable Long id) { ... }

@DeleteMapping("/orders/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void deleteOrder(@PathVariable Long id) { ... }

@PostFilter("filterObject.ownerId == authentication.principal.id")
public List<Order> getOrders() { ... } // фильтрует результат
```

### UserDetails и UserDetailsService

```java
// Кастомная загрузка пользователя
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepo.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
            user.email(),
            user.passwordHash(),
            user.roles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList()
        );
    }
}
```

### Password Encoding

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // cost factor 12
}

// Регистрация:
String hash = encoder.encode(rawPassword);  // bcrypt сам генерирует соль

// Проверка:
encoder.matches(rawPassword, storedHash);  // константное время (защита от timing attack)
```

### CSRF

**CSRF (Cross-Site Request Forgery):** злоумышленник вынуждает браузер пользователя отправить запрос от его имени (с cookie).

**Защита:**
- CSRF Token (синхронизатор): сервер выдаёт уникальный токен на форму, проверяет при POST
- `SameSite=Strict/Lax` cookie — браузер не шлёт cookie на cross-site запросы
- Для REST API + JWT — CSRF не нужен (JWT в Authorization header, не в cookie)

### Распространённые уязвимости (OWASP Top 10)

| Уязвимость | Защита |
|-----------|--------|
| SQL Injection | Prepared Statements, параметризованные запросы |
| XSS (Cross-Site Scripting) | HTML encoding, Content-Security-Policy |
| CSRF | CSRF token, SameSite cookie, проверка Origin |
| Broken Auth | Bcrypt, rate limiting, MFA |
| Sensitive Data Exposure | HTTPS, no-store, encrypt at rest |
| IDOR | Проверять владение ресурсом (`@PreAuthorize("#id == principal.id")`) |

---

## Distributed Tracing

### Проблема

В микросервисах один запрос проходит через N сервисов. Если что-то сломалось — в каком именно?

```
Browser → API Gateway → OrderService → InventoryService → PaymentService → NotificationService
                           ↓ 500ms         ↓ 1.2s          ↓ error
```

### Решение: Trace ID + Span ID

**Trace** — весь путь запроса через все сервисы. Один Trace ID.  
**Span** — единица работы в рамках одного сервиса (или между). Каждый span имеет parent span.

```
Trace ID: abc123
├── Span: api-gateway (0ms → 1500ms)
│   ├── Span: order-service (10ms → 1400ms)
│   │   ├── Span: db-query (10ms → 50ms)
│   │   ├── Span: inventory-service HTTP call (60ms → 1200ms)  ← МЕДЛЕННО
│   │   │   └── Span: inventory-service (60ms → 1200ms)
│   │   └── Span: payment-service HTTP call (1210ms → 1390ms)
```

### Инструменты

**OpenTelemetry** — стандарт (vendor-neutral). Поддерживается всеми крупными вендорами.

**Micrometer Tracing** (Spring Boot 3+) — поверх OpenTelemetry:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
</dependency>
```

```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% запросов (в prod обычно 0.1-0.01)
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
```

**Backends:** Zipkin, Jaeger, Tempo (Grafana), AWS X-Ray, Datadog APM

### Propagation

Trace ID передаётся через HTTP headers (W3C TraceContext стандарт):

```http
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
             version  trace-id (128-bit)                span-id        flags
```

Spring Boot автоматически:
- Добавляет `traceparent` header к исходящим HTTP/gRPC запросам
- Читает `traceparent` из входящих запросов
- Добавляет `traceId` и `spanId` в MDC → появляются в логах

```
2024-01-15 10:23:45 [trace=4bf92f3577b34da6a3ce929d0e0e4736, span=00f067aa0ba902b7] INFO OrderService - Processing order
```

### Correlation ID

Отличие от Trace ID:
- **Trace ID** — технический, из трейсинга (OpenTelemetry)
- **Correlation ID** — бизнесовый, для логов. Может совпадать с Trace ID или быть отдельным.

```java
// В Spring фильтр устанавливает MDC:
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null) correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        response.setHeader("X-Correlation-ID", correlationId);
        try { filterChain.doFilter(request, response); }
        finally { MDC.clear(); }
    }
}
```
