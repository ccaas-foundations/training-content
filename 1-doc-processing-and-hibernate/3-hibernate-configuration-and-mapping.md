# Hibernate Configuration and Mapping

## Learning Objectives

- Configure a Hibernate project using both XML and annotation-based approaches.
- Annotate Java model classes with JPA annotations to map them to database tables.
- Understand the role of the `hibernate.cfg.xml` file at a high level.
- Create entity classes with field-level and relationship mappings.

## Why This Matters

Earlier today, you learned what ORM is and how it solves the impedance mismatch between Java objects and relational databases. Now it is time to put that knowledge into practice. Configuring Hibernate and annotating entity classes are the foundational skills that everything else this week builds upon -- from querying with HQL and the Criteria API (Thursday) to understanding Hibernate's architecture and advanced features (Friday).

## The Concept

### Hibernate Setup in a Spring Boot Project

The fastest way to add Hibernate to a Spring Boot project is through the JPA starter dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

Spring Boot auto-configures Hibernate as the JPA provider when this starter is on the classpath.

### Configuration via application.properties

In a Spring Boot project, Hibernate is typically configured through `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/company_db
spring.datasource.username=root
spring.datasource.password=secret
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

Key properties explained:

| Property                          | Purpose                                                        |
|-----------------------------------|----------------------------------------------------------------|
| `spring.datasource.url`          | JDBC URL of the database.                                      |
| `spring.jpa.hibernate.ddl-auto`  | Controls schema generation: `none`, `validate`, `update`, `create`, `create-drop`. |
| `spring.jpa.show-sql`            | Prints generated SQL to the console (useful for development).  |
| `hibernate.dialect`              | Tells Hibernate which SQL dialect to generate.                 |

The `ddl-auto` values:

| Value         | Behavior                                                       |
|---------------|----------------------------------------------------------------|
| `none`        | No schema management. You handle it manually.                  |
| `validate`    | Validates the schema matches the entities. Does not modify.    |
| `update`      | Updates the schema to match entities (additive only).          |
| `create`      | Drops and recreates the schema on startup.                     |
| `create-drop` | Same as `create`, but also drops on shutdown.                  |

We will explore the standalone `hibernate.cfg.xml` configuration file in detail on Thursday.

### Annotating Entity Classes

A JPA entity is a plain Java class annotated with `@Entity`. Every entity must have a no-argument constructor and a primary key field.

**Basic Entity:**

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String department;

    @Column(name = "is_active")
    private boolean active;

    // No-argument constructor required by JPA
    public Employee() {}

    public Employee(String name, String department, boolean active) {
        this.name = name;
        this.department = department;
        this.active = active;
    }

    // Getters and setters omitted for brevity
}
```

### Annotation Reference

| Annotation          | Purpose                                                              |
|---------------------|----------------------------------------------------------------------|
| `@Entity`           | Marks the class as a persistent entity.                              |
| `@Table`            | Specifies the database table name and schema.                        |
| `@Id`               | Identifies the primary key field.                                    |
| `@GeneratedValue`   | Configures primary key generation strategy.                          |
| `@Column`           | Customizes the column mapping (name, nullable, length, unique).      |
| `@Transient`        | Excludes a field from persistence (not stored in the database).      |
| `@Temporal`         | Specifies the type for `java.util.Date` fields (DATE, TIME, TIMESTAMP). |
| `@Enumerated`       | Maps an enum to a column (ORDINAL or STRING).                        |
| `@Lob`              | Maps a field to a large object (BLOB/CLOB).                         |

### Primary Key Generation Strategies

| Strategy                        | Behavior                                                 |
|---------------------------------|----------------------------------------------------------|
| `GenerationType.IDENTITY`       | Uses the database's auto-increment column.               |
| `GenerationType.SEQUENCE`       | Uses a database sequence (preferred for PostgreSQL, Oracle). |
| `GenerationType.TABLE`          | Uses a separate table to track generated values.         |
| `GenerationType.AUTO`           | Lets Hibernate choose the best strategy for the database.|

### Mapping Relationships

**One-to-Many / Many-to-One:**

```java
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
}
```

```java
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
```

Key points:

- `mappedBy` indicates the inverse side of the relationship (the side that does not own the foreign key).
- `@JoinColumn` specifies the foreign key column on the owning side.
- `cascade = CascadeType.ALL` means operations (persist, merge, remove) on the parent cascade to the children.

**Many-to-Many:**

```java
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}
```

`@JoinTable` defines the intermediate join table and its foreign key columns.

### Fetch Types

When loading an entity, Hibernate can load its associated entities **eagerly** (immediately) or **lazily** (on demand):

| Fetch Type        | Default For           | Behavior                                  |
|-------------------|-----------------------|-------------------------------------------|
| `FetchType.EAGER` | `@ManyToOne`, `@OneToOne` | Loads the association immediately.        |
| `FetchType.LAZY`  | `@OneToMany`, `@ManyToMany` | Loads the association only when accessed. |

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id")
private Department department;
```

Lazy loading is generally preferred to avoid loading unnecessary data. We will discuss the implications of fetch strategies in more detail on Friday.

## Summary

- Spring Boot auto-configures Hibernate via `spring-boot-starter-data-jpa` and `application.properties`.
- Every JPA entity requires `@Entity`, `@Id`, and a no-argument constructor.
- `@Column`, `@Table`, `@GeneratedValue`, and relationship annotations (`@OneToMany`, `@ManyToOne`, `@ManyToMany`) define the object-to-table mapping.
- `ddl-auto` controls schema generation behavior; `update` is suitable for development, while `validate` or `none` should be used in production.
- Tomorrow, we will examine the `hibernate.cfg.xml` file in detail and explore Hibernate's core interfaces and advanced features like the Criteria API and caching.

## Additional Resources

- [Hibernate ORM User Guide -- Entity Mapping](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#entity)
- [Baeldung -- JPA Entity Annotations](https://www.baeldung.com/jpa-entities)
- [Baeldung -- Hibernate One-to-Many Mapping](https://www.baeldung.com/hibernate-one-to-many)
