package by.pavel.logging;

// TODO: Реализуй OrderService со структурированным логированием
// @Service
// private static final Logger log = LoggerFactory.getLogger(OrderService.class);
//
// public Order createOrder(String userId, OrderRequest request) {
//     // 1. MDC.put(userId, orderId, amount)
//     // 2. log.info("Creating order") — без конкатенации
//     // 3. Валидация → WARN если некорректный запрос
//     // 4. Обработка → DEBUG для деталей
//     // 5. ERROR с e (не e.getMessage()) при неожиданной ошибке
//     // 6. finally: MDC.remove(...)
// }
