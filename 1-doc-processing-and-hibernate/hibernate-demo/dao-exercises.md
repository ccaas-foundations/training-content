# DAO Practice Exercises

### Exercise 1: Create a Customer (CustomerDao)

Create an interface method and implementation:
```java
public void create(Customer customer);
```

### Exercise 2: Find all Customers (CustomerDao)

Create an interface method and implementation:
```java
public List<Customer> findAll();
```

### Exercise 3: Update Customer

Create an interface method and implementation:
```java
public Customer update(Customer customer);
```


### Exercise 4: Find Orders Before a Date

Create an interface method and implementation:
```java
List<Order> findOrdersBeforeDate(LocalDateTime date);
```

### Exercise 5: Count Orders by Customer

Create an interface method and implementation:
```java
long countOrdersByCustomerId(int customerId);
```


---


### Challenge Questions: Complex Filter Chain (Criteria API)

Create an interface method and implementation:
```java
List<Order> findOrdersWithComplexFilterByCriteria(
    String sku, 
    LocalDateTime afterDate, 
    OrderStatus status
);
```

- Use Criteria API with multiple restrictions combined with AND
- Filter by: sku, date range, and status
- Allow flexible filtering (some parameters can be null to indicate "ignore this filter")
- Example: "Find pants orders after today that are pending"

**Hint:** Build the WHERE clause conditionally - only add restrictions if parameters are non-null
