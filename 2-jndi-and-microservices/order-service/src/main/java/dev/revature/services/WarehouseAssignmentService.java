package dev.revature.services;

import dev.revature.dtos.WarehouseSummary;
import org.springframework.stereotype.Service;
import dev.revature.entities.Order;


import java.util.Optional;

@Service
public class WarehouseAssignmentService {


    public WarehouseAssignmentService(){
    }

    public Optional<WarehouseSummary> findWarehouseForOrder(Order order){

        // we have to get this warehouse information from the warehouse service
            // we'll use RestClient to help us

        return Optional.empty();
    }


}
