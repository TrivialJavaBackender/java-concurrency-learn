package by.pavel.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    @GetMapping
    public Collection<Order> list() {
        log.info("Listing {} orders", orders.size());
        return orders.values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable String id) {
        Order order = orders.get(id);
        if (order == null) {
            log.warn("Order not found: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public Order create(@RequestBody CreateOrderRequest req) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Order order = new Order(id, req.product(), req.quantity(), "PENDING");
        orders.put(id, order);
        log.info("Created order id={} product={} qty={}", id, req.product(), req.quantity());
        return order;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (orders.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("Deleted order id={}", id);
        return ResponseEntity.noContent().build();
    }

    public record CreateOrderRequest(String product, int quantity) {}
}
