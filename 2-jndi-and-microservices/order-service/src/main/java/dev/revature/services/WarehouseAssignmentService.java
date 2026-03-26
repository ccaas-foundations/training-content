package dev.revature.services;

import dev.revature.dtos.WarehouseSummary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import dev.revature.entities.Order;
import org.springframework.web.client.RestClient;


import java.util.Optional;

@Service
public class WarehouseAssignmentService {

    private final RestClient restClient;

    public WarehouseAssignmentService(RestClient.Builder restClientBuilder){
        this.restClient = restClientBuilder.build();
    }

    @CircuitBreaker(name="warehouseServiceCircuitBreaker", fallbackMethod="warehouseFallback")
    @Retry(name="warehouseServiceRetry")
    public Optional<WarehouseSummary> findWarehouseForOrder(Order order){
        // we have to get this warehouse information from the warehouse service
            // we'll use RestClient to help us
        //RestClient restClient = RestClient.create();

        //String requestString = "/warehouse-fulfillment?region="+order.getWarehouseId()+"&sku="+order.getSku();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/warehouse-fulfillment")
                        .queryParam("region",order.getShippingRegion())
                        .queryParam("sku", order.getSku())
                        .build()
                )
                .exchange((request, response)->{
                    if(response.getStatusCode().is2xxSuccessful()){
                        WarehouseSummary ws = response.bodyTo(WarehouseSummary.class);
                        return Optional.of(ws);
                    }
                    return Optional.empty();
                });
    }

    // define warehouseFallback method signature
    public Optional<WarehouseSummary> warehouseFallback(Order order, Throwable t){
        System.out.println("fallback method triggered " + t.getMessage());
        return Optional.empty();
    }


}
