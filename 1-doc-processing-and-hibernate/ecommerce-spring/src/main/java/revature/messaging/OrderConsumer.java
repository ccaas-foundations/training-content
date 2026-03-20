package revature.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import revature.models.Order;
import revature.models.OrderStatus;
import revature.repositories.OrderRepository;
import revature.services.WarehouseAssignmentService;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

@Service
public class OrderConsumer {

    private final WarehouseAssignmentService warehouseAssignmentService;
    private final OrderRepository orderRepository;

    public OrderConsumer(WarehouseAssignmentService warehouseAssignmentService, OrderRepository orderRepository){
        this.warehouseAssignmentService = warehouseAssignmentService;
        this.orderRepository = orderRepository;
    }

    // listen for messages in our queue
    @JmsListener(destination = "order.fulfillment.queue")
    public void processOrder(String orderString){
        System.out.println(orderString);
        JsonMapper jsonMapper = new JsonMapper();
        Order order = jsonMapper.readValue(orderString, Order.class);
        System.out.println(order);
        order.setTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.RECEIVED);
        // validate that the customer id exists -- we could create a service to do this, as well as other validation for the message or the state of our objects
        Order assignedOrder = warehouseAssignmentService.assignWarehouseToOrder(order);
        System.out.println(assignedOrder);
        orderRepository.save(order);
    }


}
