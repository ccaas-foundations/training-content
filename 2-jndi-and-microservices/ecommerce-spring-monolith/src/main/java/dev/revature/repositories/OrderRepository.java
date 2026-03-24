package dev.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dev.revature.models.Order;
import dev.revature.models.OrderStatus;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    public List<Order> findByOrderStatus(OrderStatus status);


}
