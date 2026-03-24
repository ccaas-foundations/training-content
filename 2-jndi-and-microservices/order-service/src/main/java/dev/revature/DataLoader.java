package dev.revature;

import dev.revature.enums.OrderStatus;
import dev.revature.enums.ShippingRegion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import dev.revature.entities.*;
import dev.revature.repositories.CustomerRepository;
import dev.revature.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;


    private final OrderRepository orderRepository;

    public DataLoader(CustomerRepository customerRepository, OrderRepository orderRepository){
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Customer c1 = new Customer("John Doe", "jdoe@gmail.com");
        Customer c2 = new Customer("Sally Jenkins", "sjenkins23@gmail.com");
        Customer c3 = new Customer("Jane Doe", "jdoe22@gmail.com");

        customerRepository.save(c1);
        customerRepository.save(c2);
        customerRepository.save(c3);



        Order o1 = new Order(c1, 2, ShippingRegion.WEST, "mug", OrderStatus.SHIPPED, LocalDateTime.of(2026,03,18,8,3));

        Order o2 = new Order(c1, 3, ShippingRegion.MIDWEST, "jacket", OrderStatus.SHIPPED, LocalDateTime.of(2026,03,18,8,7));

        Order o3 = new Order(c2, 6, ShippingRegion.MIDWEST, "lizard", OrderStatus.SHIPPED, LocalDateTime.of(2026,03,15,12,30));

        Order o4 = new Order(c3, 0, ShippingRegion.SOUTHEAST, "spoon", OrderStatus.CANCELLED, LocalDateTime.of(2026,03,12,11,23));

        orderRepository.save(o1);
        orderRepository.save(o2);
        orderRepository.save(o3);
        orderRepository.save(o4);


        /*
        // populating the fields in the Order object
            // the order object also includes a customer and a warehouse
        Order fetchedOrder = orderRepository.findById(2).get();
        System.out.println(fetchedOrder);


        List<Order> cancelledOrders = orderRepository.findByOrderStatus(OrderStatus.CANCELLED);

        System.out.println("Canceled Orders:");
        for(Order canceledOrder : cancelledOrders){
            System.out.println(canceledOrder);
        }

        List<Order> shippedOrders = orderRepository.findByOrderStatus(OrderStatus.SHIPPED);

        System.out.println("Shipped Orders:");
        for(Order shippedOrder : shippedOrders){
            System.out.println(shippedOrder);
        }


        Order newOrder = new Order(c3, null, ShippingRegion.MIDWEST, "spoon", OrderStatus.RECEIVED, LocalDateTime.now());

        List<Warehouse> potentialFulfillmentWarehouses = warehouseRepository.findByShippingRegionAndSku(newOrder.getShippingRegion(), newOrder.getSku());

        if(!potentialFulfillmentWarehouses.isEmpty()){
            Warehouse fulfillmentWarehouse = potentialFulfillmentWarehouses.getFirst();
            newOrder.setWarehouse(fulfillmentWarehouse);
            newOrder.setOrderStatus(OrderStatus.ASSIGNED);
        } else {
            newOrder.setOrderStatus(OrderStatus.CANCELLED);
        }

        Order persistedOrder = orderRepository.save(newOrder);
        System.out.println(persistedOrder);
        */

    }
}
