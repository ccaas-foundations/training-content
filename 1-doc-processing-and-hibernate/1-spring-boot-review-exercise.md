# Spring Boot CRUD API

---

## Overview

Build a Spring Boot REST API that performs full CRUD operations on a resource of your group's choosing. Your API should accept and return JSON, persist data using Spring Data JPA, and use an H2 in-memory database.

---

## Project Setup

Bootstrap your project at [https://start.spring.io](https://start.spring.io) with these dependencies:

- Spring Web
- Spring Data JPA
- H2 Database

Add the following to `application.properties`:

```properties
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

---

## Requirements

Your group decides what the resource is. It should have an `id` and at least 3 other fields.

Your application must include:

1. An `@Entity` class with `@Id`, `@GeneratedValue`, and at least 3 fields
2. A repository interface that extends `JpaRepository`
3. A `@Service` class that contains your business logic and talks to the repository
4. A `@RestController` that delegates to the service, with all five endpoints below:

| Method   | Endpoint              | Description                        |
|----------|-----------------------|------------------------------------|
| GET      | `/api/{resource}`     | Return all records                 |
| GET      | `/api/{resource}/{id}`| Return a single record by ID       |
| POST     | `/api/{resource}`     | Create a new record (JSON body)    |
| PUT      | `/api/{resource}/{id}`| Update a record by ID (JSON body)  |
| DELETE   | `/api/{resource}/{id}`| Delete a record by ID              |

Return a 404 when a requested ID does not exist.

---

## You're Done When…

1. The app starts without errors
2. You can POST a new record and see it returned by GET
3. You can UPDATE a record and see the change reflected
4. You can DELETE a record and a follow-up GET returns 404
5. The H2 console (`http://localhost:8080/h2-console`) shows your table and data

---

## Stretch Goals

- Add validation with `@Valid` and JSR-303 annotations (`@NotBlank`, `@Min`, etc.)
- Add a custom finder method to the repository (e.g., `findByName`)
- Seed data on startup using a `CommandLineRunner` bean
