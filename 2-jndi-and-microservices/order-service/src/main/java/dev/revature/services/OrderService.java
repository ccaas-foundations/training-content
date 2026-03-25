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
        Optional<WarehouseSummary> warehouseOptional = warehouseAssignmentService.findWarehouseForOrder(order);
        if(warehouseOptional.isPresent()){
            System.out.println("found a warehouse for order: "+ warehouseOptional.get());
            order.setWarehouseId(warehouseOptional.get().getId());
            order.setOrderStatus(OrderStatus.ASSIGNED);
        } else {
            System.out.println("no warehouse found");
            order.setOrderStatus(OrderStatus.CANCELLED);
        }
        return orderRepository.save(order);
    }

}
