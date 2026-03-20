package dev.revature.services;

import org.springframework.stereotype.Service;
import dev.revature.models.Order;
import dev.revature.models.OrderStatus;
import dev.revature.models.Warehouse;
import dev.revature.repositories.WarehouseRepository;

import java.util.List;

@Service
public class WarehouseAssignmentService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseAssignmentService(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }


    public Order assignWarehouseToOrder(Order order){
        List<Warehouse> warehouseCandidates = warehouseRepository.findByShippingRegionAndSku(order.getShippingRegion(), order.getSku());

        if(!warehouseCandidates.isEmpty()){
            order.setWarehouse(warehouseCandidates.getFirst());
            order.setOrderStatus(OrderStatus.ASSIGNED);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }
        return order;
    }


}
