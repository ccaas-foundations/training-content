# Spring Boot Review

## Learning Objectives

- Recall the core principles of the Spring Framework and Spring Boot.
- Understand project structure, dependency injection, and auto-configuration.
- Identify key annotations used to bootstrap and configure a Spring Boot application.

## Why This Matters

This week centers on processing structured data formats (XML and JSON) and integrating persistent storage through Hibernate ORM. All of these capabilities are built on top of the Spring ecosystem. A solid understanding of Spring Boot's conventions -- how it discovers beans, wires dependencies, and auto-configures modules -- is essential before layering on the document processing and ORM features that follow.

## The Concept

### What Is Spring Boot?

Spring Boot is an opinionated framework built on top of the Spring Framework. It eliminates much of the boilerplate configuration traditionally associated with Spring applications by applying sensible defaults through **auto-configuration**.

At its core, Spring Boot provides:

- **Embedded Servers** -- Applications ship with an embedded Tomcat, Jetty, or Undertow server, meaning there is no need to deploy WAR files to an external container.
- **Starter Dependencies** -- Curated sets of compatible dependencies bundled under a single artifact (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`).
- **Auto-Configuration** -- Spring Boot inspects the classpath and automatically configures beans and infrastructure components based on what it finds.

### Project Structure

A typical Spring Boot project follows this layout:

```
my-app/
  src/
    main/
      java/
        com/example/
          Application.java       <-- Entry point
          controller/
          service/
          model/
          repository/
      resources/
        application.properties        <-- Configuration
        static/
        templates/
  pom.xml (or build.gradle)
```

The entry point class is annotated with `@SpringBootApplication`, which is a convenience annotation that combines three annotations:

| Annotation               | Purpose                                                      |
|---------------------------|--------------------------------------------------------------|
| `@Configuration`          | Marks the class as a source of bean definitions.             |
| `@EnableAutoConfiguration`| Activates Spring Boot's auto-configuration mechanism.        |
| `@ComponentScan`          | Tells Spring to scan the current package and sub-packages.   |

### Dependency Injection

Dependency Injection (DI) is the mechanism by which Spring manages object creation and wiring. Rather than instantiating collaborators manually, you declare dependencies and let the Spring IoC (Inversion of Control) container provide them.

There are three common injection styles:

**Constructor Injection (Recommended):**

```java
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
```

**Field Injection:**

```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
}
```

**Setter Injection:**

```java
@Service
public class OrderService {

    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
```

Constructor injection is preferred because it makes dependencies explicit, supports immutability (via `final`), and simplifies unit testing.

### Auto-Configuration

Spring Boot's auto-configuration works by scanning the classpath for specific libraries. For example:

- If `spring-boot-starter-web` is on the classpath, Spring Boot automatically configures an embedded Tomcat server, a `DispatcherServlet`, and default error handling.
- If `spring-boot-starter-data-jpa` is present along with a JDBC driver, Spring Boot configures a `DataSource`, an `EntityManagerFactory`, and transaction management.

You can override any auto-configured bean by defining your own `@Bean` method in a `@Configuration` class.

### Key Annotations Reference

| Annotation         | Purpose                                                              |
|--------------------|----------------------------------------------------------------------|
| `@RestController`  | Combines `@Controller` and `@ResponseBody`; returns data directly.   |
| `@RequestMapping`  | Maps HTTP requests to handler methods.                               |
| `@GetMapping`      | Shortcut for `@RequestMapping(method = GET)`.                        |
| `@PostMapping`     | Shortcut for `@RequestMapping(method = POST)`.                       |
| `@Service`         | Marks a class as a service-layer component.                          |
| `@Repository`      | Marks a class as a data access component; enables exception translation. |
| `@Component`       | Generic stereotype for any Spring-managed bean.                      |
| `@Value`           | Injects values from `application.properties` into fields.            |

### Configuration with application.properties

Spring Boot loads configuration from `src/main/resources/application.properties` (or `application.yml`). Common properties include:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update
```

The equivalent configuration in yml:

```yml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

These properties can be overridden at runtime via environment variables, command-line arguments, or profile-specific configuration files.

## Code Example

Below is a minimal Spring Boot application that exposes a simple REST endpoint:

```java
package com.example.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

```java
package com.example.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!";
    }
}
```

Running `MyAppApplication.main()` starts the embedded server. Navigating to `http://localhost:8080/hello` returns the plain text response.

## Summary

- Spring Boot reduces configuration overhead through starter dependencies and auto-configuration.
- The `@SpringBootApplication` annotation bootstraps the entire application context.
- Dependency Injection via constructor injection is the recommended approach for clean, testable code.
- Configuration lives in `application.properties` or `application.yml` and can be overridden per environment.
- Understanding these fundamentals is critical, as every topic this week -- XML processing, JSON handling, JMS, and Hibernate -- builds directly on this Spring Boot foundation.

## Additional Resources

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Baeldung -- Spring Boot Tutorial](https://www.baeldung.com/spring-boot)
- [Spring Guides -- Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
