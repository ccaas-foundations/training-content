package dev.revature.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    private int orderId;

    private int customerId;

    private String sku;

    @JsonFormat(pattern = "MM/dd/yyyy HH:mm")
    private LocalDateTime timestamp;

    public Order() {
    }

    public Order(int orderId, int customerId, String sku, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.sku = sku;
        this.timestamp = timestamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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
        return orderId == order.orderId && customerId == order.customerId && Objects.equals(sku, order.sku) && Objects.equals(timestamp, order.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customerId, sku, timestamp);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", sku='" + sku + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
