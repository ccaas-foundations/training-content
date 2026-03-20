# Spring Data JPA with Hibernate

## From Hibernate to Spring Data JPA

In the previous section we worked with Hibernate directly - manually creating a `SessionFactory`, opening sessions, writing transactions by hand, and managing the lifecycle of our persistence objects ourselves. Spring Data JPA does not replace any of that. It wraps it.

Under the hood, Spring Data JPA still uses Hibernate as its JPA provider. Every query you write, every entity you persist, every transaction that commits is still going through Hibernate. What Spring Data removes is the ceremony - the repetitive infrastructure code that looks the same in every project.

| Raw Hibernate | Spring Data JPA equivalent |
|---|---|
| `SessionFactory` / `EntityManagerFactory` | Auto-configured by Spring Boot |
| `session.beginTransaction()` | `@Transactional` |
| `session.save(entity)` | `repository.save(entity)` |
| `session.get(Entity.class, id)` | `repository.findById(id)` |
| `session.createQuery(...)` | Derived query methods or `@Query` |
| `session.close()` | Managed automatically |

The `JpaRepository` interface gives you all standard CRUD operations for free. Spring generates the implementation at runtime - you never write it.

---

## Service Locator vs Dependency Injection

When we wrote our `HibernateUtil` class, we were using the **Service Locator pattern**. Any class that needed a `SessionFactory` called `HibernateUtil.getSessionFactory()` directly - it reached out and fetched its own dependency.

```java
// Service Locator - the class finds its own dependency
SessionFactory factory = HibernateUtil.getSessionFactory();
```

This works, but it creates hidden coupling. The class knows where to find `HibernateUtil`, it depends on it being initialized, and it is difficult to swap out for testing.

Spring uses **Dependency Injection** instead. Rather than a class finding its dependencies, Spring constructs the dependency and hands it in. The class declares what it needs and Spring takes care of the rest.

```java
// Dependency Injection - Spring provides the dependency
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
}
```

The practical difference: with Service Locator, the class is in control. With DI, the framework is in control. DI makes dependencies explicit, swappable, and testable.

---

## Derived Query Methods

Spring Data JPA can generate queries from method names by parsing the method signature. For straightforward field equality and basic comparisons this removes the need to write any query at all.

```java
// Spring generates: SELECT o FROM Order o WHERE o.status = ?1
List<Order> findByStatus(OrderStatus status);

// SELECT o FROM Order o WHERE o.shippingRegion = ?1 AND o.status = ?2
List<Order> findByShippingRegionAndStatus(ShippingRegion region, OrderStatus status);

// SELECT o FROM Order o WHERE o.customerId = ?1 ORDER BY o.receivedAt DESC
List<Order> findByCustomerIdOrderByReceivedAtDesc(String customerId);
```

This works well for simple cases. The limitation is that derived methods can only express what the method name parser understands - direct field access, equality, comparisons, and keywords like `Like`, `Between`, and `OrderBy`. Anything involving collection membership, joins on `@ElementCollection`, or dynamic predicates requires a different approach.

For the full list of supported keywords see the [Spring Data JPA query methods reference](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html).

---

## Custom Queries with @Query

When a derived method name cannot express the query you need, annotate the method with `@Query` and write JPQL directly. The method still lives in the repository interface - Spring handles execution.

```java
@Query("SELECT o FROM Order o WHERE o.assignedWarehouse.id = :warehouseId AND o.status = :status")
List<Order> findByWarehouseAndStatus(
    @Param("warehouseId") Long warehouseId,
    @Param("status") OrderStatus status
);
```

You can also query across the full order history for a customer, ordered by most recent:

```java
@Query("SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.receivedAt DESC")
List<Order> findOrderHistoryByCustomer(@Param("customerId") String customerId);
```

---

## Custom Queries with the Fragment Interface Pattern

For queries that need the Criteria API - particularly when predicates are built dynamically at runtime - Spring Data supports a fragment interface pattern. You define a separate interface for the custom method, implement it with access to `EntityManager`, and merge it into the main repository.

**Fragment interface**
```java
public interface OrderRepositoryCustom {
    List<Order> findByStatusAndRegion(OrderStatus status, ShippingRegion region);
}
```

**Implementation** - the `Impl` suffix is required for Spring to detect it automatically
```java
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Order> findByStatusAndRegion(OrderStatus status, ShippingRegion region) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        query.select(root).where(
            cb.equal(root.get("status"), status),
            cb.equal(root.get("shippingRegion"), region)
        );

        return entityManager.createQuery(query).getResultList();
    }
}
```

**Main repository - extend both**
```java
public interface OrderRepository
    extends JpaRepository<Order, Long>, OrderRepositoryCustom {
}
```

Callers use `orderRepository.findByStatusAndRegion(...)` like any other repository method - the fragment implementation is invisible to the rest of the application.

---

## When to use each approach

| Approach | Use when |
|---|---|
| Derived method | Simple field equality or comparison, no joins |
| `@Query` | Fixed JPQL needed, involves joins or association traversal |
| Fragment + Criteria API | Query predicates are built dynamically at runtime |
