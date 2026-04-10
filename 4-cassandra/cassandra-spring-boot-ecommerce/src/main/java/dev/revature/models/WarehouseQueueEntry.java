package dev.revature.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Table("warehouse_queue")
public class WarehouseQueueEntry {

    @PrimaryKey
    private WarehouseQueueKey warehouseQueueKey;

    private String customer;

    @Column("placed_at")
    private Instant placedAt;

    private BigDecimal total;

    public WarehouseQueueEntry() {
    }

    public WarehouseQueueKey getWarehouseQueueKey() {
        return warehouseQueueKey;
    }

    public void setWarehouseQueueKey(WarehouseQueueKey warehouseQueueKey) {
        this.warehouseQueueKey = warehouseQueueKey;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Instant placedAt) {
        this.placedAt = placedAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseQueueEntry that = (WarehouseQueueEntry) o;
        return Objects.equals(warehouseQueueKey, that.warehouseQueueKey) && Objects.equals(customer, that.customer) && Objects.equals(placedAt, that.placedAt) && Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseQueueKey, customer, placedAt, total);
    }

    @Override
    public String toString() {
        return "WarehouseQueueEntry{" +
                "warehouseQueueKey=" + warehouseQueueKey +
                ", customer='" + customer + '\'' +
                ", placedAt=" + placedAt +
                ", total=" + total +
                '}';
    }
}
