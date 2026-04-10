package dev.revature.models;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@PrimaryKeyClass
public class InvoiceEventKey implements Serializable {

    @PrimaryKeyColumn(name="invoice_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID invoiceId; // partition

    @PrimaryKeyColumn(name="event_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant eventTime; // cluster

    public InvoiceEventKey() {
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceEventKey that = (InvoiceEventKey) o;
        return Objects.equals(invoiceId, that.invoiceId) && Objects.equals(eventTime, that.eventTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, eventTime);
    }

    @Override
    public String toString() {
        return "InvoiceEventKey{" +
                "invoiceId=" + invoiceId +
                ", eventTime=" + eventTime +
                '}';
    }
}
