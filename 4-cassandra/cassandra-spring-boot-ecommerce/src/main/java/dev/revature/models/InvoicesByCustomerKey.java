package dev.revature.models;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@PrimaryKeyClass
public class InvoicesByCustomerKey implements Serializable {

    @PrimaryKeyColumn(name="customer_id", type = PrimaryKeyType.PARTITIONED)
    private String customerId;

    @PrimaryKeyColumn(name="placed_at", type = PrimaryKeyType.CLUSTERED)
    private Instant placedAt;

    public InvoicesByCustomerKey() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Instant placedAt) {
        this.placedAt = placedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoicesByCustomerKey that = (InvoicesByCustomerKey) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(placedAt, that.placedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, placedAt);
    }

    @Override
    public String toString() {
        return "InvoicesByCustomerKey{" +
                "customerId='" + customerId + '\'' +
                ", placedAt=" + placedAt +
                '}';
    }
}
