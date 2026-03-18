# Introduction to ORM

## Learning Objectives

- Understand the object-relational impedance mismatch and why ORM frameworks exist.
- Describe the relationship between JPA (the specification) and Hibernate (the implementation).
- Explain how object-relational mapping bridges Java objects to relational database tables.
- Identify the core advantages and trade-offs of using an ORM in enterprise applications.

## Why This Matters

The first half of this week focused on processing documents -- XML and JSON. But processed data needs to be stored somewhere. Relational databases are the backbone of enterprise data storage, and Java objects do not map neatly onto database tables without a translation layer. ORM (Object-Relational Mapping) provides that translation. Understanding ORM concepts is the entry point into Hibernate, which we will configure and use in depth for the rest of the week.

## The Concept

### The Impedance Mismatch Problem

Java and relational databases model data differently:

| Aspect           | Java (Object-Oriented)          | Relational Database              |
|------------------|---------------------------------|----------------------------------|
| Data unit        | Object (instance of a class)    | Row (in a table)                 |
| Structure        | Fields and methods              | Columns                         |
| Relationships    | Object references               | Foreign keys                    |
| Inheritance      | Class hierarchies               | No native support               |
| Identity         | `==` or `.equals()`             | Primary key                     |
| Navigation       | `order.getCustomer().getName()` | JOIN across tables              |

This fundamental mismatch is called the **object-relational impedance mismatch**. Without an ORM, developers must manually write SQL for every CRUD operation and manually map `ResultSet` data back to Java objects -- a process that is tedious, error-prone, and difficult to maintain.

### What Is ORM?

Object-Relational Mapping is a technique that automatically maps:

- **Java classes** to **database tables**.
- **Object fields** to **table columns**.
- **Object relationships** (associations) to **foreign key relationships**.
- **Object inheritance** to table structures (via various strategies).

An ORM framework generates and executes SQL on your behalf, allowing you to work with objects rather than raw SQL strings.

### Manual Approach vs ORM

**Without ORM (JDBC):**

```java
String sql = "SELECT id, name, department FROM employees WHERE id = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setInt(1, 101);
ResultSet rs = stmt.executeQuery();

if (rs.next()) {
    Employee emp = new Employee();
    emp.setId(rs.getInt("id"));
    emp.setName(rs.getString("name"));
    emp.setDepartment(rs.getString("department"));
}
```

**With ORM (Hibernate/JPA):**

```java
Employee emp = entityManager.find(Employee.class, 101);
```

The ORM handles the SQL generation, execution, and result mapping automatically.

### JPA vs Hibernate

It is important to distinguish between the specification and the implementation:

| Term      | What It Is                                                            |
|-----------|-----------------------------------------------------------------------|
| **JPA**   | Jakarta Persistence API -- a **specification** (a set of interfaces and annotations) that defines how Java objects should be mapped to relational databases. It does not contain any implementation code. |
| **Hibernate** | An ORM **framework** that **implements** the JPA specification. It provides the actual engine that translates between objects and SQL. |

Think of JPA as a contract (like a Java interface) and Hibernate as the class that implements that contract. Other JPA implementations exist (EclipseLink, OpenJPA), but Hibernate is by far the most widely used.

Why this matters: by coding against JPA interfaces (`EntityManager`, `@Entity`, etc.), your application code remains portable. You could (in theory) swap Hibernate for another JPA provider without changing your business logic.

### Core ORM Concepts

**Entity:** A Java class that maps to a database table. Marked with `@Entity`.

**Primary Key:** Every entity must have a unique identifier, marked with `@Id`.

**Mapping Annotations:** Define how fields map to columns:

| Annotation  | Purpose                                              |
|-------------|------------------------------------------------------|
| `@Entity`   | Declares a class as a JPA entity.                    |
| `@Table`    | Specifies the table name (if different from class name). |
| `@Id`       | Marks the primary key field.                         |
| `@Column`   | Customizes column name, length, nullable, etc.       |
| `@GeneratedValue` | Configures auto-generation strategy for the primary key. |

**Relationships:** ORM maps associations between entities:

| Annotation       | Relationship Type                     |
|-------------------|---------------------------------------|
| `@OneToOne`       | One entity relates to exactly one other. |
| `@OneToMany`      | One entity relates to many others.    |
| `@ManyToOne`      | Many entities relate to one.          |
| `@ManyToMany`     | Many entities relate to many.         |

### Advantages of Using ORM

- **Productivity** -- Less boilerplate code; no manual `ResultSet` parsing.
- **Maintainability** -- Changes to the data model are reflected in annotations, not scattered SQL strings.
- **Database portability** -- ORM generates database-specific SQL via configurable dialects.
- **Object-oriented querying** -- HQL and JPQL allow queries using entity names and fields rather than table names and columns.
- **Caching** -- ORM frameworks include built-in caching layers that reduce database round-trips.

### Trade-Offs

- **Performance overhead** -- The abstraction layer adds processing time compared to hand-tuned SQL.
- **Learning curve** -- Understanding lazy loading, session management, and the N+1 query problem requires experience.
- **Complex queries** -- Some advanced SQL (window functions, recursive CTEs) may be easier to express in native SQL than in HQL/JPQL.

## Summary

- The object-relational impedance mismatch makes direct JDBC tedious and error-prone.
- ORM frameworks like Hibernate automate the mapping between Java objects and database tables.
- JPA is the specification; Hibernate is the most popular implementation.
- Core annotations (`@Entity`, `@Id`, `@Column`, relationship annotations) define the mapping.
- Later today, we will configure Hibernate and create our first mapped entity classes.

## Additional Resources

- [Jakarta Persistence Specification](https://jakarta.ee/specifications/persistence/)
- [Baeldung -- Introduction to JPA with Hibernate](https://www.baeldung.com/learn-jpa-hibernate)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
