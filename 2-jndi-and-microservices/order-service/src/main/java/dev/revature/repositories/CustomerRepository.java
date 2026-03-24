package dev.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import dev.revature.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
