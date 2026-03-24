package dev.revature.services;

import org.springframework.stereotype.Service;
import dev.revature.models.Order;
import dev.revature.models.Warehouse;
import dev.revature.repositories.WarehouseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseAssignmentService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseAssignmentService(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }

    public Optional<Warehouse> findWarehouseForOrder(Order order){
        List<Warehouse> warehouseCandidates = warehouseRepository.findByShippingRegionAndSku(order.getShippingRegion(), order.getSku());
        if(warehouseCandidates.isEmpty()){
            return Optional.empty();
        } 
        return Optional.of(warehouseCandidates.getFirst());
    }


}
