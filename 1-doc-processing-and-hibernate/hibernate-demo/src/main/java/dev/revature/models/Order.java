package dev.revature.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="purchase_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int customerId;

    private String sku;

    private LocalDateTime timestamp;

    public Order() {
    }

    public Order(int customerId, String sku, LocalDateTime timestamp) {
        this.customerId = customerId;
        this.sku = sku;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && customerId == order.customerId && Objects.equals(sku, order.sku) && Objects.equals(timestamp, order.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, sku, timestamp);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", sku='" + sku + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
