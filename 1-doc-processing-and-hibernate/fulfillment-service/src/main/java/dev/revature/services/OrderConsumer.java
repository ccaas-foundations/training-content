package dev.revature.services;

import dev.revature.models.Order;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class OrderConsumer {

    @JmsListener(destination = "order.fulfillment.queue")
    public void processOrder(String orderJson){
        JsonMapper jsonMapper = new JsonMapper();
        Order order = jsonMapper.readValue(orderJson, Order.class);
        System.out.println("[fulfillment-service] Recieved order: "+order);
        System.out.println("[fulfillment-service] Order fulfilled!");
    }

}
