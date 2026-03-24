package dev.revature.entities;



import dev.revature.enums.OrderStatus;
import dev.revature.enums.ShippingRegion;
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


    private int warehouseId;

    private ShippingRegion shippingRegion;
    private String sku;

    //annotation can be used to store your enums in the database as a string
//    @Enumerated(EnumType.STRING)
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

    public Order(Customer customer, int warehouseId, ShippingRegion shippingRegion, String sku, OrderStatus orderStatus, LocalDateTime timestamp) {
        this.customer = customer;
        this.warehouseId = warehouseId;
        this.shippingRegion = shippingRegion;
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

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
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
        return id == order.id && Objects.equals(customer, order.customer) && Objects.equals(warehouseId, order.warehouseId) && Objects.equals(shippingRegion, order.shippingRegion) && Objects.equals(sku, order.sku) && orderStatus == order.orderStatus && Objects.equals(timestamp, order.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, warehouseId, shippingRegion, sku, orderStatus, timestamp);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", warehouse=" + warehouseId +
                ", shippingRegion='" + shippingRegion + '\'' +
                ", sku='" + sku + '\'' +
                ", orderStatus=" + orderStatus +
                ", timestamp=" + timestamp +
                '}';
    }
}
