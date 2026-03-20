package revature.models;



import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="purchase_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Warehouse warehouse;

    private ShippingRegion shippingRegion;

    private String sku;

    private OrderStatus orderStatus;

    private LocalDateTime timestamp;

    public Order() {
    }

    public Order(Customer customer, String sku, LocalDateTime timestamp) {
        this.customer = customer;
        this.sku = sku;
        this.timestamp = timestamp;
    }

    public Order(Customer customer, String sku, OrderStatus orderStatus, LocalDateTime timestamp) {
        this.customer = customer;
        this.sku = sku;
        this.orderStatus = orderStatus;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public ShippingRegion getShippingRegion() {
        return shippingRegion;
    }

    public void setShippingRegion(ShippingRegion shippingRegion) {
        this.shippingRegion = shippingRegion;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && Objects.equals(customer, order.customer) && Objects.equals(warehouse, order.warehouse) && Objects.equals(shippingRegion, order.shippingRegion) && Objects.equals(sku, order.sku) && orderStatus == order.orderStatus && Objects.equals(timestamp, order.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, warehouse, shippingRegion, sku, orderStatus, timestamp);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", warehouse=" + warehouse +
                ", shippingRegion='" + shippingRegion + '\'' +
                ", sku='" + sku + '\'' +
                ", orderStatus=" + orderStatus +
                ", timestamp=" + timestamp +
                '}';
    }
}
