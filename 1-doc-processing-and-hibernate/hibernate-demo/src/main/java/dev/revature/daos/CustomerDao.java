package dev.revature.daos;

import dev.revature.models.Customer;

import java.util.List;

public interface CustomerDao {

    public void create(Customer customer);
    public List<Customer> findAll();
    public Customer update(Customer customer);

}
