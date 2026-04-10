package dev.revature.models;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class WarehouseQueueKey implements Serializable {

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String warehouse;

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String status;

    @PrimaryKeyColumn(name="invoice_id",ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID invoiceId;

    public WarehouseQueueKey() {
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseQueueKey that = (WarehouseQueueKey) o;
        return Objects.equals(warehouse, that.warehouse) && Objects.equals(status, that.status) && Objects.equals(invoiceId, that.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouse, status, invoiceId);
    }

    @Override
    public String toString() {
        return "WarehouseQueueKey{" +
                "warehouse='" + warehouse + '\'' +
                ", status='" + status + '\'' +
                ", invoiceId=" + invoiceId +
                '}';
    }
}
