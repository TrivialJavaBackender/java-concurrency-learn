package by.pavel.order;

import java.time.Instant;

public class Order {
    private final String id;
    private final String product;
    private final int quantity;
    private final String status;
    private final Instant createdAt;

    public Order(String id, String product, int quantity, String status) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
