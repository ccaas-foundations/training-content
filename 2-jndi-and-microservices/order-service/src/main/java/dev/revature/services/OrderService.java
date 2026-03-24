package dev.revature.services;

import dev.revature.dtos.WarehouseSummary;
import org.springframework.stereotype.Service;
import dev.revature.entities.Order;
import dev.revature.enums.OrderStatus;
import dev.revature.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseAssignmentService warehouseAssignmentService;

    public OrderService(OrderRepository orderRepository, WarehouseAssignmentService warehouseAssignmentService){
        this.orderRepository = orderRepository;
        this.warehouseAssignmentService = warehouseAssignmentService;
    }

    public Order processIncomingOrder(Order order){
        order.setTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.RECEIVED);

        // we can just use the dto here for the summary (or our warehouse representation of our order-service)
        Optional<WarehouseSummary> warehouse = warehouseAssignmentService.findWarehouseForOrder(order);
        if(warehouse.isPresent()){
            //order.setWarehouseId(warehouse.get());
            order.setOrderStatus(OrderStatus.ASSIGNED);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }

        return orderRepository.save(order);
    }

}
