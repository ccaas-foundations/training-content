package dev.revature.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table("invoices_by_customer")
public class InvoicesByCustomer {

    @PrimaryKey
    private InvoicesByCustomerKey invoicesByCustomerKey;

    @Column("invoice_id")
    private UUID invoiceId;

    private BigDecimal total;

    @Column("item_skus")
    private List<String> itemSkus;

    public InvoicesByCustomer() {
    }

    public InvoicesByCustomerKey getInvoicesByCustomerKey() {
        return invoicesByCustomerKey;
    }

    public void setInvoicesByCustomerKey(InvoicesByCustomerKey invoicesByCustomerKey) {
        this.invoicesByCustomerKey = invoicesByCustomerKey;
    }

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<String> getItemSkus() {
        return itemSkus;
    }

    public void setItemSkus(List<String> itemSkus) {
        this.itemSkus = itemSkus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoicesByCustomer that = (InvoicesByCustomer) o;
        return Objects.equals(invoicesByCustomerKey, that.invoicesByCustomerKey) && Objects.equals(invoiceId, that.invoiceId) && Objects.equals(total, that.total) && Objects.equals(itemSkus, that.itemSkus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoicesByCustomerKey, invoiceId, total, itemSkus);
    }

    @Override
    public String toString() {
        return "InvoicesByCustomer{" +
                "invoicesByCustomerKey=" + invoicesByCustomerKey +
                ", invoiceId=" + invoiceId +
                ", total=" + total +
                ", itemSkus=" + itemSkus +
                '}';
    }
}
