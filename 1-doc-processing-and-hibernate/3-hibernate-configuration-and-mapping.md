# Hibernate Configuration and Mapping

## Learning Objectives

- Configure a Hibernate project using both XML and annotation-based approaches.
- Annotate Java model classes with JPA annotations to map them to database tables.
- Understand the role of the `hibernate.cfg.xml` file at a high level.
- Create entity classes with field-level and relationship mappings.

## Why This Matters

Earlier today, you learned what ORM is and how it solves the impedance mismatch between Java objects and relational databases. Now it is time to put that knowledge into practice. Configuring Hibernate and annotating entity classes are the foundational skills that everything else this week builds upon -- from querying with HQL and the Criteria API (Thursday) to understanding Hibernate's architecture and advanced features (Friday).
## The Core Workflow

Hibernate follows a strict hierarchy to interact with the database. Every database operation flows through this chain:

```
hibernate.cfg.xml
       ↓
 SessionFactory        ← built once, app-wide singleton
       ↓
    Session            ← built per request / unit of work
       ↓
  Transaction          ← wraps your DB operations
       ↓
  Session Methods      ← save(), get(), update(), delete(), etc.
```

---

## 1. hibernate.cfg.xml

The main configuration file. Lives in `src/main/resources/`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
    "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
  <session-factory>

    <!-- Database connection -->
    <property name="hibernate.connection.driver_class">org.postgresql.Driver</property>
    <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/mydb</property>
    <property name="hibernate.connection.username">admin</property>
    <property name="hibernate.connection.password">secret</property>

    <!-- Dialect tells Hibernate which SQL flavor to generate -->
    <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>

    <!-- DDL behavior: validate | update | create | create-drop -->
    <property name="hibernate.hbm2ddl.auto">update</property>

    <!-- Log generated SQL to console -->
    <property name="hibernate.show_sql">true</property>
    <property name="hibernate.format_sql">true</property>

    <!-- Register your entity classes -->
    <mapping class="com.example.model.Product"/>
    <mapping class="com.example.model.Order"/>

  </session-factory>
</hibernate-configuration>
```

### hbm2ddl.auto Options

| Value | Behavior | Use When |
|---|---|---|
| `validate` | Checks schema matches entities, no changes | Production |
| `update` | Adds missing columns/tables, never drops | Dev / staging |
| `create` | Drops and recreates schema on startup | Testing |
| `create-drop` | Creates on startup, drops on shutdown | Integration tests |

---

## 2. SessionFactory

Built **once** at application startup from the config. Expensive to create — treat it as a singleton.

```java
SessionFactory sessionFactory = new Configuration()
    .configure("hibernate.cfg.xml")   // loads the XML config
    .buildSessionFactory();
```

With a utility class (common pattern):

```java
public class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY;

    static {
        try {
            SESSION_FACTORY = new Configuration()
                .configure()
                .buildSessionFactory();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}
```

**Key points:**
- Thread-safe — share it across the entire application
- Creates the connection pool
- Knows all registered entity mappings
- `close()` it when the app shuts down

---

## 3. Session

A **unit of work** — represents a single conversation with the database. Lightweight, not thread-safe. Open one per request or operation, then close it.

```java
Session session = sessionFactory.openSession();
```

**Key points:**
- Wraps a single JDBC connection
- Maintains a **first-level cache** (identity map) — if you `get()` the same entity twice, the second call hits the cache, not the DB
- Should be opened and closed within the same thread
- Always close it in a `finally` block or use try-with-resources

```java
try (Session session = sessionFactory.openSession()) {
    // do work
}
```

---

## 4. Transaction

Every write operation (insert, update, delete) must happen inside a transaction. Read operations technically don't require one, but it's good practice to always use one.

```java
Transaction tx = null;

try (Session session = sessionFactory.openSession()) {
    tx = session.beginTransaction();

    // ... your DB operations here ...

    tx.commit();                        // persist changes
} catch (Exception e) {
    if (tx != null) tx.rollback();      // undo on failure
    e.printStackTrace();
}
```

---

## 5. Session Methods

### Persisting

| Method | Behavior |
|---|---|
| `session.save(entity)` | Inserts entity, returns generated ID |
| `session.persist(entity)` | Inserts entity, JPA-standard (void return) |
| `session.saveOrUpdate(entity)` | Inserts if new, updates if detached |
| `session.merge(entity)` | Merges detached entity state into session |

```java
Product p = new Product("Laptop", 999.99);
Long id = (Long) session.save(p);
```

### Reading

| Method | Behavior |
|---|---|
| `session.get(Class, id)` | Returns entity or **null** if not found |
| `session.load(Class, id)` | Returns a **proxy** — throws exception if not found on access |

```java
Product p = session.get(Product.class, 1L);   // null-safe
Product p = session.load(Product.class, 1L);  // proxy, lazy
```

Prefer `get()` when you're not sure the record exists. Use `load()` when you need a proxy reference (e.g., setting a foreign key without a full fetch).

### Updating

```java
// If entity is in the current session (persistent state):
product.setPrice(1099.99);
tx.commit();  // Hibernate detects the change via dirty checking — no explicit update needed

// If entity came from outside the session (detached state):
session.update(product);
// or
session.saveOrUpdate(product);
```

### Deleting

```java
Product p = session.get(Product.class, 1L);
session.delete(p);
```

### Querying

```java
// HQL (Hibernate Query Language) — entity-based, not table-based
List<Product> products = session.createQuery(
    "FROM Product WHERE price > :minPrice", Product.class)
    .setParameter("minPrice", 500.0)
    .list();

// Native SQL — when you need DB-specific queries
List<Product> products = session.createNativeQuery(
    "SELECT * FROM products WHERE price > 500", Product.class)
    .list();
```

---

## 6. Entity Lifecycle / Object States

Understanding which state your object is in determines which methods to use.

```
new MyEntity()          →  Transient   (Hibernate doesn't know about it)
      ↓  save() / persist()
   Persistent           (session is tracking it, changes auto-synced)
      ↓  session closes / evict()
   Detached             (was persistent, session is gone)
      ↓  merge() / update()
   Persistent again
      ↓  delete()
   Removed              (scheduled for deletion on commit)
```

**Dirty checking:** While an entity is **persistent**, Hibernate watches it. Any field change is automatically synced to the DB on `flush()` or `commit()` — no explicit `update()` call needed.

---

## 7. Annotations

Hibernate annotations replace XML mapping files (`*.hbm.xml`). Applied directly to entity classes.

### Basic Entity

```java
import jakarta.persistence.*;

@Entity                          // marks this class as a mapped entity
@Table(name = "products")        // maps to this table name (optional if class name matches)
public class Product {

    @Id                          // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto-increment
    private Long id;

    @Column(name = "product_name", nullable = false, length = 100)
    private String name;

    @Column(name = "price")
    private Double price;

    @Transient                   // not persisted to DB
    private String tempLabel;

    // constructors, getters, setters
}
```

### GenerationType Strategies

| Strategy | Behavior |
|---|---|
| `IDENTITY` | DB auto-increment (MySQL, PostgreSQL) |
| `SEQUENCE` | DB sequence object (PostgreSQL preferred) |
| `TABLE` | Hibernate-managed ID table (portable, slower) |
| `AUTO` | Hibernate picks based on dialect |

### Relationships

**One-to-Many / Many-to-One:**

```java
// Parent side (Order)
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<OrderItem> items = new ArrayList<>();

// Child side (OrderItem)
@ManyToOne
@JoinColumn(name = "order_id")   // FK column in order_items table
private Order order;
```

**Many-to-Many:**

```java
@ManyToMany
@JoinTable(
    name = "student_course",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id")
)
private List<Course> courses = new ArrayList<>();
```

**One-to-One:**

```java
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "profile_id", referencedColumnName = "id")
private UserProfile profile;
```

### Fetch Types

| Type | Behavior | Default for |
|---|---|---|
| `LAZY` | Loads related data only when accessed | `@OneToMany`, `@ManyToMany` |
| `EAGER` | Loads related data immediately with parent | `@ManyToOne`, `@OneToOne` |

```java
// Override default:
@OneToMany(fetch = FetchType.EAGER)   // not recommended for large collections
@ManyToOne(fetch = FetchType.LAZY)    // avoids N+1 in some scenarios
```

### Cascade Types

| Type | Effect |
|---|---|
| `PERSIST` | Save children when parent is saved |
| `MERGE` | Merge children when parent is merged |
| `REMOVE` | Delete children when parent is deleted |
| `REFRESH` | Refresh children when parent is refreshed |
| `DETACH` | Detach children when parent is detached |
| `ALL` | All of the above |

```java
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
```

`orphanRemoval = true` deletes child records when they're removed from the parent's collection — useful for owned relationships.

---

## Full Example

```java
// Entity
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // getters, setters
}

// DAO / Service
public class OrderService {

    public void createOrder(Order order) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public Order findOrder(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Order.class, id);
        }
    }

    public void updateOrder(Order order) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteOrder(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Order order = session.get(Order.class, id);
            if (order != null) session.delete(order);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
```

---

## Quick Reference Cheatsheet

```
hibernate.cfg.xml          →  DB connection + dialect + entity registration
SessionFactory             →  singleton, built once, thread-safe
Session                    →  per-request, not thread-safe, has 1st level cache
Transaction                →  wraps all writes, commit or rollback

session.save()             →  insert, returns ID
session.get()              →  select by ID, returns null if missing
session.load()             →  select by ID, returns proxy, throws if missing
session.update()           →  update detached entity
session.saveOrUpdate()     →  insert or update based on ID
session.merge()            →  merge detached state into current session
session.delete()           →  delete persistent entity

@Entity                    →  mark class as table-mapped
@Table(name="...")         →  specify table name
@Id                        →  primary key
@GeneratedValue            →  auto-generate PK
@Column                    →  column mapping + constraints
@Transient                 →  exclude field from persistence
@OneToMany / @ManyToOne    →  relationship mapping
@JoinColumn                →  specify FK column
```
