package dev.revature.services;

import org.springframework.stereotype.Service;
import dev.revature.models.Order;
import dev.revature.models.OrderStatus;
import dev.revature.models.Warehouse;
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

        Optional<Warehouse> warehouse = warehouseAssignmentService.findWarehouseForOrder(order);
        if(warehouse.isPresent()){
            order.setWarehouse(warehouse.get());
            order.setOrderStatus(OrderStatus.ASSIGNED);
        } else {
            order.setOrderStatus(OrderStatus.CANCELLED);
        }

        return orderRepository.save(order);
    }

}
