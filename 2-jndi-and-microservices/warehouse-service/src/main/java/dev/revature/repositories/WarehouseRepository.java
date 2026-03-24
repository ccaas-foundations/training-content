package dev.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import dev.revature.enums.ShippingRegion;
import dev.revature.entities.Warehouse;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    List<Warehouse> findByShippingRegion(ShippingRegion shippingRegion);

        // Spring Data JPA does not support items in collections, it could not parse something like this:
    //List<Warehouse> findByShippingRegionAndItemsInStockContains(ShippingRegion region, String sku);
        // instead we can either use the @Query annotation to write a custom HQL(JPQL statement) or we can create an interace and implementing class to leverage Hibernate's Criteria interface


    // find warehouse WHERE the warehouse inventory contains the sku AND the warehouse is in the shipping region
        // SELECT * FROM WAREHOUSE w JOIN WAREHOUSE_STOCK ws WHERE w.SHIPPING_REGION = {REGION} AND ws.SKU = {SKU}
    @Query("SELECT w FROM Warehouse w JOIN w.itemsInStock sku WHERE w.shippingRegion = :region AND sku = :sku")
    List<Warehouse> findByShippingRegionAndSku(@Param("region") ShippingRegion region, @Param("sku") String sku);



}
