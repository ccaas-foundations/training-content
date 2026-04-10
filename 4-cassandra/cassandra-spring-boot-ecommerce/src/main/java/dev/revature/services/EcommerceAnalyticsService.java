package dev.revature.services;

import dev.revature.models.InvoiceEvent;
import dev.revature.models.InvoiceEventKey;
import dev.revature.models.InvoicesByCustomer;
import dev.revature.models.WarehouseQueueEntry;
import dev.revature.repositories.InvoiceEventRepository;
import dev.revature.repositories.InvoicesByCustomerRepository;
import dev.revature.repositories.WarehouseQueueRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class EcommerceAnalyticsService {

    private final InvoiceEventRepository invoiceEventRepository;
    private final InvoicesByCustomerRepository invoicesByCustomerRepository;
    private final WarehouseQueueRepository warehouseQueueRepository;

    public EcommerceAnalyticsService(InvoiceEventRepository invoiceEventRepository, InvoicesByCustomerRepository invoicesByCustomerRepository, WarehouseQueueRepository warehouseQueueRepository){
        this.invoiceEventRepository = invoiceEventRepository;
        this.invoicesByCustomerRepository = invoicesByCustomerRepository;
        this.warehouseQueueRepository = warehouseQueueRepository;
    }

    public List<InvoiceEvent> getInvoiceEventHistory(UUID invoiceId){
        return invoiceEventRepository.findByInvoiceEventKeyInvoiceId(invoiceId);
    }

    public List<InvoicesByCustomer> getInvoicesByCustomer(String customerId){
        return invoicesByCustomerRepository.findByInvoicesByCustomerKeyCustomerId(customerId);
    }

    public BigDecimal getCustomerSpend(String customerId){
        List<InvoicesByCustomer> invoices = invoicesByCustomerRepository.findByInvoicesByCustomerKeyCustomerId(customerId);

        BigDecimal customerTotal = BigDecimal.ZERO;

        for(InvoicesByCustomer invoice : invoices){
            customerTotal = customerTotal.add(invoice.getTotal());
        }

        return customerTotal;
    }

    public List<WarehouseQueueEntry> getWarehouseQueue(String warehouse){
        return warehouseQueueRepository.findByWarehouseQueueKeyWarehouseAndWarehouseQueueKeyStatus(warehouse, "ASSIGNED");
    }

    public int getWarehouseQueueCount(String warehouse){
        return warehouseQueueRepository.findByWarehouseQueueKeyWarehouseAndWarehouseQueueKeyStatus(warehouse, "ASSIGNED").size();
    }

}
