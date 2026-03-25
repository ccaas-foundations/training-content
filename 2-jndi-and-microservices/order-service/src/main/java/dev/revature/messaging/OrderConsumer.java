package dev.revature.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import dev.revature.entities.Order;
import dev.revature.services.OrderService;

@Service
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(OrderService orderService){
        this.orderService = orderService;
    }

    // listen for messages in our queue
    @JmsListener(destination = "order.fulfillment.queue")
    public void processOrder(String orderString) throws JsonProcessingException {
        System.out.println(orderString);
        JsonMapper jsonMapper = new JsonMapper();
        Order order = jsonMapper.readValue(orderString, Order.class);
        System.out.println(order);
        // validate that the customer id exists -- we could create a service to do this, as well as other validation for the message or the state of our objects
        Order processedOrder = orderService.processIncomingOrder(order);
        System.out.println(processedOrder);
    }


}
