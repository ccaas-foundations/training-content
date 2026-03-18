package dev.revature.controllers;

import dev.revature.models.Order;
import dev.revature.services.OrderProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    //we'll want to inject our dev.revature.services.OrderProducer
    private OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer){
        this.orderProducer = orderProducer;
    }

    // this method should receive new orders via HTTP to introduce them to our system
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody Order newOrder){
        System.out.println("storefront-service received a new order: "+newOrder);
        //pass this newOrder to our OrderProducer to produce a message and send to our broker
        orderProducer.sendOrder(newOrder);
        return ResponseEntity.ok(newOrder);
    }


}
