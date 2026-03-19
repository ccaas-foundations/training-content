package dev.revature.daos;

import dev.revature.models.Order;
import dev.revature.models.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao {

    public void create(Order order);
    public Order getOrderById(int id);
    public List<Order> getAllOrders();
    public List<Order> getOrdersByCustomer(int id);
    public List<Order> getOrdersByStatus(OrderStatus status);
    public List<Order> getOrdersByCustomerAndStatus(int CustomerId, OrderStatus status);
    List<Order> findOrdersBeforeDate(LocalDateTime date);
    long countOrdersByCustomerId(int customerId);
    List<Order> findOrdersWithComplexFilterByCriteria(String sku, LocalDateTime afterDate, OrderStatus status);

}
