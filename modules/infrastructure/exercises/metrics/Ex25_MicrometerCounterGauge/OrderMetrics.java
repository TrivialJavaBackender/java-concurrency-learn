package by.pavel.metrics;

// TODO: Реализуй OrderMetrics сервис
// @Service
//
// В конструкторе (MeterRegistry registry):
//   Counter ordersCreated = Counter.builder("orders_created_total")
//       .description("...")
//       .tag("status", "success")  // но нужно сделать динамичным!
//       .register(registry);
//
//   AtomicInteger activeOrders = new AtomicInteger(0);
//   registry.gauge("orders_active", activeOrders);
//
// Методы:
//   recordOrderCreated(String status, String type)
//   incrementActive() / decrementActive()
//   setQueueSize(int size)
