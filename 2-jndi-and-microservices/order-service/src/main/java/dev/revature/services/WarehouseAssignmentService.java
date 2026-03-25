package dev.revature.services;

import dev.revature.dtos.WarehouseSummary;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import dev.revature.entities.Order;
import org.springframework.web.client.RestClient;


import java.util.Optional;

@Service
public class WarehouseAssignmentService {


    public WarehouseAssignmentService(){
    }

    public Optional<WarehouseSummary> findWarehouseForOrder(Order order){
        // we have to get this warehouse information from the warehouse service
            // we'll use RestClient to help us
        //RestClient restClient = RestClient.create();
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();

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


}
