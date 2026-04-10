package dev.revature.controllers;

import dev.revature.models.InvoiceEvent;
import dev.revature.models.InvoicesByCustomer;
import dev.revature.models.WarehouseQueueEntry;
import dev.revature.models.WarehouseQueueKey;
import dev.revature.repositories.InvoiceEventRepository;
import dev.revature.services.EcommerceAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
public class AnalyticsController {

    /*
        /invoices/{invoiceId}/events -> one invoice's history

        /customers/{customerId}/invoices -> all invoices belonging to one customer
        /customers/{customerId}/total-spend -> the total amount a customer has spent
                                                across all invoices
        /warehouses/{warehouse}/queue -> what is in the queue for this warehouse
        /warehouses/{warehouse}/queue/count -> how many items are in the queue
     */

    private EcommerceAnalyticsService analyticsService;

    public AnalyticsController(EcommerceAnalyticsService analyticsService){
        this.analyticsService = analyticsService;
    }

    @GetMapping("/invoices/{invoiceId}/events")
    public List<InvoiceEvent> getInvoiceHistory(@PathVariable("invoiceId")UUID invoiceId){
        return analyticsService.getInvoiceEventHistory(invoiceId);
    }

    @GetMapping("/customers/{customerId}/invoices")
    public List<InvoicesByCustomer> getInvoicesByCustomer(@PathVariable("customerId")String customerId){
        return analyticsService.getInvoicesByCustomer(customerId);
    }

    @GetMapping("/customers/{customerId}/total-spend")
    public BigDecimal getCustomerTotalSpend(@PathVariable("customerId")String customerId){
        return analyticsService.getCustomerSpend(customerId);
    }

    @GetMapping("/warehouses/{warehouse}/queue")
    public List<WarehouseQueueEntry> getWarehouseQueue(@PathVariable("warehouse") String warehouse){
        return analyticsService.getWarehouseQueue(warehouse);
    }

    @GetMapping("/warehouses/{warehouse}/queue/count")
    public int getWarehouseQueueCount(@PathVariable("warehouse") String warehouse){
        return analyticsService.getWarehouseQueueCount(warehouse);
    }


}
