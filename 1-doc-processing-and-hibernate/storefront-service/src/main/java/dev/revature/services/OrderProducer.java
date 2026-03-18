package dev.revature.services;

import dev.revature.models.Order;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class OrderProducer {

    // include a JmsTemplate to prepare and send our message
    private JmsTemplate jmsTemplate;

    public OrderProducer(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    public void sendOrder(Order order){
        JsonMapper jsonMapper = new JsonMapper();
        String orderJson = jsonMapper.writeValueAsString(order);
        jmsTemplate.convertAndSend("order.fulfillment.queue", orderJson);
        System.out.println("[storefront-service] Sending order to queue");
    }

}
