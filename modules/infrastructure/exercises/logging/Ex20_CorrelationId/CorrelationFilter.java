package by.pavel.logging;

// TODO: Реализуй Correlation ID filter
// Extends: OncePerRequestFilter
// Аннотации: @Component
//
// Логика:
// 1. Прочитать X-Request-ID заголовок
// 2. Если нет — UUID.randomUUID().toString()
// 3. MDC.put("requestId", id)
// 4. response.setHeader("X-Request-ID", id)
// 5. chain.doFilter(request, response)
// 6. finally: MDC.clear()
