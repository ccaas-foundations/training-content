package dev.revature.controllers;

import dev.revature.entities.Warehouse;
import dev.revature.enums.ShippingRegion;
import dev.revature.repositories.WarehouseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    public WarehouseController(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }

    // /warehouses/{id} -> path variable
    // /warehouses?region=WEST -> request param
    // /warehouses?region=WEST?sku=socks -> returns a collection
    @GetMapping("/warehouses")
    public List<Warehouse> getWarehouses(@RequestParam(name="region", required = false)ShippingRegion region){
        System.out.println("region: "+region);
        if(region!=null){
            return warehouseRepository.findByShippingRegion(region);
        }
        return warehouseRepository.findAll();
    }

    // /warehouse-fulfillment could be implemented to return one warehouse
    @GetMapping("/warehouse-fulfillment")
    public ResponseEntity<Warehouse> recommendWarehouse(@RequestParam("region")ShippingRegion region, @RequestParam("sku")String sku){
        List<Warehouse> warehouseCandidates =  warehouseRepository.findByShippingRegionAndSku(region,sku);
        // maybe we could find any warehouse that contains that item
        if(warehouseCandidates!=null && !warehouseCandidates.isEmpty()){
            return ResponseEntity.ok(warehouseCandidates.getFirst());
        }
        return ResponseEntity.notFound().build();
    }



}
