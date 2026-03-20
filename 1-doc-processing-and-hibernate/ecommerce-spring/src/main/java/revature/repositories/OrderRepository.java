package revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import revature.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
