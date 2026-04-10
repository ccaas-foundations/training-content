package dev.revature.models;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Objects;

@Table("invoice_events")
public class InvoiceEvent {

    @PrimaryKey
    private InvoiceEventKey invoiceEventKey;

    private String status;

    private String warehouse;

    public InvoiceEvent() {
    }

    public InvoiceEventKey getInvoiceEventKey() {
        return invoiceEventKey;
    }

    public void setInvoiceEventKey(InvoiceEventKey invoiceEventKey) {
        this.invoiceEventKey = invoiceEventKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceEvent that = (InvoiceEvent) o;
        return Objects.equals(invoiceEventKey, that.invoiceEventKey) && Objects.equals(status, that.status) && Objects.equals(warehouse, that.warehouse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceEventKey, status, warehouse);
    }

    @Override
    public String toString() {
        return "InvoiceEvent{" +
                "invoiceEventKey=" + invoiceEventKey +
                ", status='" + status + '\'' +
                ", warehouse='" + warehouse + '\'' +
                '}';
    }
}
