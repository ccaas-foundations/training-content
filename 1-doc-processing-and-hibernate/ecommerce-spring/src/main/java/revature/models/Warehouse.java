package revature.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String location;
    private ShippingRegion shippingRegion;

    @ElementCollection
    @CollectionTable(name = "warehouse_skus", joinColumns = @JoinColumn(name = "warehouse_id"))
    @Column(name = "sku")
    private Set<String> itemsInStock = new HashSet<>();

    public Warehouse() {
    }

    public Warehouse(String name, String location, ShippingRegion shippingRegion) {
        this.name = name;
        this.location = location;
        this.shippingRegion = shippingRegion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ShippingRegion getShippingRegion() {
        return shippingRegion;
    }

    public void setShippingRegion(ShippingRegion shippingRegion) {
        this.shippingRegion = shippingRegion;
    }

    public Set<String> getItemsInStock() {
        return itemsInStock;
    }

    public void setItemsInStock(Set<String> itemsInStock) {
        this.itemsInStock = itemsInStock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return id == warehouse.id && Objects.equals(name, warehouse.name) && Objects.equals(location, warehouse.location) && Objects.equals(shippingRegion, warehouse.shippingRegion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, shippingRegion);
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", shippingRegion='" + shippingRegion + '\'' +
                ", itemsInStock=" + itemsInStock +
                '}';
    }
}
