package dev.revature;

import dev.revature.entities.Warehouse;
import dev.revature.enums.ShippingRegion;
import dev.revature.repositories.WarehouseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final WarehouseRepository warehouseRepository;

    public DataLoader(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }
    @Override
    public void run(String... args) throws Exception {
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
    }
}
