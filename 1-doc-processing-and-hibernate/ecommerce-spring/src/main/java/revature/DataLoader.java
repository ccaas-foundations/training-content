package revature;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import revature.models.*;
import revature.repositories.CustomerRepository;
import revature.repositories.OrderRepository;
import revature.repositories.WarehouseRepository;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    private final WarehouseRepository warehouseRepository;

    private final OrderRepository orderRepository;

    public DataLoader(CustomerRepository customerRepository, WarehouseRepository warehouseRepository, OrderRepository orderRepository){
        this.customerRepository = customerRepository;
        this.warehouseRepository = warehouseRepository;
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

        Warehouse w1 = new Warehouse("West Hub", "Los Angeles, CA", ShippingRegion.WEST);
        w1.setItemsInStock(Set.of("shirt", "pants", "shoes", "socks", "jacket", "lizard"));

        Warehouse w2 = new Warehouse("Northwest Hub", "Seattle, WA", ShippingRegion.WEST);
        w2.setItemsInStock(Set.of("mug", "plate", "bowl", "fork", "knife", "spoon"));

        Warehouse w3 = new Warehouse("Midwest Hub", "Cleveland, OH", ShippingRegion.MIDWEST);
        w3.setItemsInStock(Set.of("shirt", "pants", "shoes", "socks", "jacket", "mug", "plate"));

        Warehouse w4 = new Warehouse("Northeast Hub", "Boston, MA", ShippingRegion.NORTHEAST);
        w4.setItemsInStock(Set.of("shirt", "pants", "socks", "fork", "knife", "spoon"));

        Warehouse w5 = new Warehouse("Southeast Hub", "Atlanta, GA", ShippingRegion.SOUTHEAST);
        w5.setItemsInStock(Set.of("shirt", "pants", "shoes", "socks", "jacket", "mug", "plate", "lizard"));

        Warehouse w6 = new Warehouse("Midwest 2", "St. Louis, MO", ShippingRegion.MIDWEST);
        w6.setItemsInStock(Set.of("lizard"));

        warehouseRepository.save(w1);
        warehouseRepository.save(w2);
        warehouseRepository.save(w3);
        warehouseRepository.save(w4);
        warehouseRepository.save(w5);
        warehouseRepository.save(w6);

        Order o1 = new Order(c1, w2, ShippingRegion.WEST, "mug", OrderStatus.SHIPPED, LocalDateTime.of(2026,03,18,8,3));

        Order o2 = new Order(c1, w3, ShippingRegion.MIDWEST, "jacket", OrderStatus.SHIPPED, LocalDateTime.of(2026,03,18,8,7));

        Order o3 = new Order(c2, w6, ShippingRegion.MIDWEST, "lizard", OrderStatus.SHIPPED, LocalDateTime.of(2026,03,15,12,30));

        Order o4 = new Order(c3, null, ShippingRegion.SOUTHEAST, "spoon", OrderStatus.CANCELLED, LocalDateTime.of(2026,03,12,11,23));

        orderRepository.save(o1);
        orderRepository.save(o2);
        orderRepository.save(o3);
        orderRepository.save(o4);


        // populating the fields in the Order object
            // the order object also includes a customer and a warehouse
        Order fetchedOrder = orderRepository.findById(2).get();
        System.out.println(fetchedOrder);




    }
}
