## Hibernate Criteria
The Criteria API lets you build queries programmatically as Java objects instead of strings. The main payoff is type safety and composability - you can't typo a column name at runtime, and you can build up query conditions with normal Java logic.

---

## The Three Core Objects

Every Criteria query starts with the same three lines. Understanding what each one represents is the key to everything else.

```java
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Product> cq = cb.createQuery(Product.class);
Root<Product> root = cq.from(Product.class);
```

### CriteriaBuilder

The **factory**. You never construct query components directly - you ask `cb` to make them for you. Every condition, expression, ordering, and aggregate comes from here.

```java
cb.equal(...)           // =
cb.greaterThan(...)     // >
cb.like(...)            // LIKE
cb.and(...)             // AND
cb.count(...)           // COUNT
cb.asc(...)             // ORDER BY ASC
```

One `CriteriaBuilder` per `Session`. Get it once and reuse it across multiple queries in the same session.

### CriteriaQuery\<T\>

Represents **the query itself**. The generic type `T` is your **return type** - the shape of what comes back from the database.

```java
CriteriaQuery<Product> cq       // returns full Product entities
CriteriaQuery<Long> cq          // returns a Long (e.g. COUNT)
CriteriaQuery<ProductSummary>   // returns a DTO
CriteriaQuery<Object[]>         // returns raw column arrays
```

### Root\<X\>

Represents **the FROM clause** — the table (entity) you are querying against. It is your handle into the entity's fields and relationships.

```java
Root<Product> root = cq.from(Product.class);

root.get("name")     // path to the name column
root.get("price")    // path to the price column
root.join("items")   // join to a related entity
```

---

## Why CriteriaQuery and Root Are Separate

In the simple case they reference the same class, which feels redundant. They are separate because **they answer different questions** and can be different types.

```
Root<X>          →  WHERE the data comes from  (the FROM clause)
CriteriaQuery<T> →  WHAT SHAPE comes back      (the SELECT clause)
```

They are the same when you want full entities back:

```java
CriteriaQuery<Product> cq = cb.createQuery(Product.class);
Root<Product> root = cq.from(Product.class);
// "Query the Product table, give me back Product objects"
```

They differ when your return type is something else — an aggregate, a DTO, a scalar:

```java
// Return type is Long, source is still Product
CriteriaQuery<Long> cq = cb.createQuery(Long.class);
Root<Product> root = cq.from(Product.class);
cq.select(cb.count(root));  // SELECT COUNT(*) FROM products
```

The API forces you to declare both explicitly because Hibernate needs to know each independently — one to build the SQL, one to deserialize the result.

---

## Step-by-Step: Building a Query

### Step 1 — Get the CriteriaBuilder

```java
CriteriaBuilder cb = session.getCriteriaBuilder();
```

### Step 2 — Create the CriteriaQuery, typed to your return

```java
CriteriaQuery<Product> cq = cb.createQuery(Product.class);
```

### Step 3 — Declare the Root (FROM clause)

```java
Root<Product> root = cq.from(Product.class);
```

### Step 4 — Build your SELECT

For full entities, this is optional — Hibernate selects the root by default:

```java
cq.select(root);  // implicit, can omit
```

For projections, specify what to select:

```java
cq.multiselect(root.get("name"), root.get("price"));                                // can select multiple columns, loaded into an Object[]
cq.select(cb.construct(ProductSummary.class, root.get("name"), root.get("price"))); // can select and invoke a DTO constructor with the selected column values 
```

### Step 5 — Add conditions (WHERE)

Build `Predicate` objects from `cb` and pass them to `cq.where()`:

```java
Predicate expensive = cb.greaterThan(root.get("price"), 100.0);
Predicate nameMatch = cb.like(root.get("name"), "%laptop%");

cq.where(cb.and(expensive, nameMatch));
```

### Step 6 — Execute

```java
List<Product> results = session.createQuery(cq).getResultList();

// Single result:
Product p = session.createQuery(cq).getSingleResult();
```

---

## Predicates

```java
cb.equal(root.get("name"), "Laptop")               // =
cb.notEqual(root.get("name"), "Laptop")            // !=
cb.greaterThan(root.get("price"), 100.0)           // >
cb.greaterThanOrEqualTo(root.get("price"), 100.0)  // >=
cb.lessThan(root.get("price"), 500.0)              // <
cb.lessThanOrEqualTo(root.get("price"), 500.0)     // <=
cb.between(root.get("price"), 100.0, 500.0)        // BETWEEN
cb.like(root.get("name"), "%laptop%")              // LIKE
cb.isNull(root.get("description"))                 // IS NULL
cb.isNotNull(root.get("description"))              // IS NOT NULL
cb.not(predicate)                                  // NOT
cb.and(p1, p2, p3)                                 // AND
cb.or(p1, p2)                                      // OR
```

---

## Dynamic Queries — The Main Advantage

Build predicates conditionally and apply them all at once. No string concatenation, no fragile null-checking.

```java
public List<Product> search(String name, Double minPrice, Double maxPrice) {

    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);

    List<Predicate> predicates = new ArrayList<>();

    if (name != null && !name.isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }
    if (minPrice != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
    }
    if (maxPrice != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
    }

    cq.where(cb.and(predicates.toArray(new Predicate[0])));

    return session.createQuery(cq).getResultList();
}
```

Every parameter is optional. If none are passed you get all products. The query assembles itself from whatever was provided.

---

## What Else You Can Do

The Criteria API also supports ordering (`cb.asc`, `cb.desc`), pagination (`setFirstResult`, `setMaxResults`), aggregates (`cb.count`, `cb.avg`, `cb.sum`, `cb.max`, `cb.min`), grouping (`cq.groupBy`, `cq.having`), and joins (`root.join`, `JoinType.LEFT`). For type-safe field references at compile time, look into the JPA metamodel (`Product_.price` instead of `root.get("price")`).

## HQL (Hibernate Query Language) Examples
#### Queries for data retrieval, filtering, aggregation, and joins.

HQL is Hibernate's object-oriented query language. It looks similar to SQL, but operates on **entity classes and their fields** rather than database tables and columns.

**Basic SELECT:**

```java
// SQL:  SELECT * FROM employees
// HQL equivalent:
List<Employee> employees = session.createQuery(
    "FROM Employee", Employee.class
).getResultList();
```

**WHERE clause with named parameters:**

```java
List<Employee> engineers = session.createQuery(
    "FROM Employee e WHERE e.department = :dept", Employee.class
).setParameter("dept", "Engineering")
 .getResultList();
```

Named parameters (`:paramName`) are preferred over positional parameters (`?1`) because they are self-documenting and can be reused in the same query.

**SELECT specific fields:**

```java
List<String> names = session.createQuery(
    "SELECT e.name FROM Employee e WHERE e.active = true", String.class
).getResultList();
```

**Aggregate functions:**

```java
Long count = session.createQuery(
    "SELECT COUNT(e) FROM Employee e WHERE e.department = :dept", Long.class
).setParameter("dept", "Engineering")
 .getSingleResult();

Double avgSalary = session.createQuery(
    "SELECT AVG(e.salary) FROM Employee e", Double.class
).getSingleResult();
```

Supported aggregate functions: `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`.

**GROUP BY and HAVING:**

```java
List<Object[]> results = session.createQuery(
    "SELECT e.department, COUNT(e) FROM Employee e " +
    "GROUP BY e.department HAVING COUNT(e) > 5"
).getResultList();

for (Object[] row : results) {
    System.out.println("Department: " + row[0] + ", Count: " + row[1]);
}
```

### HQL Joins

HQL supports implicit and explicit joins based on entity relationships:

**Implicit Join (dot navigation):**

```java
// Navigates the ManyToOne relationship from Employee to Department
List<Employee> employees = session.createQuery(
    "FROM Employee e WHERE e.department.name = 'Engineering'", Employee.class
).getResultList();
```

**Explicit JOIN:**

```java
List<Employee> employees = session.createQuery(
    "SELECT e FROM Employee e JOIN e.department d WHERE d.name = :deptName",
    Employee.class
).setParameter("deptName", "Engineering")
 .getResultList();
```

**LEFT JOIN (includes employees without a department):**

```java
List<Employee> allEmployees = session.createQuery(
    "SELECT e FROM Employee e LEFT JOIN e.department d",
    Employee.class
).getResultList();
```

**FETCH JOIN (eager loading for a specific query):**

```java
List<Department> departments = session.createQuery(
    "SELECT d FROM Department d JOIN FETCH d.employees", Department.class
).getResultList();
```

`JOIN FETCH` overrides the lazy loading configuration for that specific query, loading the associated collection in a single SQL statement. This is one of the most important tools for avoiding the N+1 query problem.

### HQL Subqueries

HQL supports subqueries in the `WHERE` and `HAVING` clauses:

```java
// Find employees who earn more than the average salary
List<Employee> highEarners = session.createQuery(
    "FROM Employee e WHERE e.salary > " +
    "(SELECT AVG(e2.salary) FROM Employee e2)",
    Employee.class
).getResultList();
```

```java
// Find departments that have at least one active employee
List<Department> activeDepts = session.createQuery(
    "FROM Department d WHERE EXISTS " +
    "(SELECT 1 FROM Employee e WHERE e.department = d AND e.active = true)",
    Department.class
).getResultList();
```