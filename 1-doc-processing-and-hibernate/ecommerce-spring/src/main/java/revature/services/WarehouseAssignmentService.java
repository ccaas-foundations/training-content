package revature.services;

import org.springframework.stereotype.Service;
import revature.models.Order;
import revature.models.OrderStatus;
import revature.models.Warehouse;
import revature.repositories.WarehouseRepository;

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
