package dev.revature.dtos;

import dev.revature.entities.Customer;
import dev.revature.enums.OrderStatus;
import dev.revature.enums.ShippingRegion;

import java.time.LocalDateTime;
import java.util.Objects;


public class FulfilledOrderResponse {


    private int id;

    private Customer customer;
    private WarehouseSummary warehouseSummary;

    private ShippingRegion shippingRegion;
    private String sku;

    //annotation can be used to store your enums in the database as a string
//    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDateTime timestamp;


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

    public WarehouseSummary getWarehouseSummary() {
        return warehouseSummary;
    }

    public void setWarehouseSummary(WarehouseSummary warehouseSummary) {
        this.warehouseSummary = warehouseSummary;
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
        FulfilledOrderResponse that = (FulfilledOrderResponse) o;
        return id == that.id && Objects.equals(customer, that.customer) && Objects.equals(warehouseSummary, that.warehouseSummary) && shippingRegion == that.shippingRegion && Objects.equals(sku, that.sku) && orderStatus == that.orderStatus && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, warehouseSummary, shippingRegion, sku, orderStatus, timestamp);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", warehouse=" + warehouseSummary +
                ", shippingRegion='" + shippingRegion + '\'' +
                ", sku='" + sku + '\'' +
                ", orderStatus=" + orderStatus +
                ", timestamp=" + timestamp +
                '}';
    }
}
