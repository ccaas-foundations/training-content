# JSON Processing in Spring

## Learning Objectives

- Understand JSON as a lightweight data interchange format.
- Compare the Jackson and Gson libraries for JSON processing in Java.
- Perform serialization (Java object to JSON) and deserialization (JSON to Java object) using both libraries.
- Integrate JSON handling into a Spring Boot application.

## Why This Matters

While we explored XML as an enterprise data format, JSON (JavaScript Object Notation) is the dominant format for modern web APIs and microservice communication. As you build toward a full understanding of document processing this week, mastering JSON serialization and deserialization is essential. Spring Boot uses Jackson as its default JSON processor, making it a critical tool in every Spring developer's workflow.

## The Concept

### What Is JSON?

JSON is a text-based format that represents data as key-value pairs and ordered lists:

```json
{
    "id": 101,
    "name": "Alice Johnson",
    "department": "Engineering",
    "active": true
}
```

Compared to XML:

| Characteristic | JSON          | XML             |
|----------------|---------------|-----------------|
| Verbosity      | Compact       | Verbose         |
| Readability    | High          | Moderate        |
| Data types     | Strings, numbers, booleans, arrays, objects, null | Everything is text |
| Schema support | JSON Schema   | XSD             |
| Usage trend    | REST APIs, modern services | SOAP, legacy integrations |

### Jackson

Jackson is the default JSON library in Spring Boot. It is included automatically when you add `spring-boot-starter-web` to your project. The central class is `ObjectMapper`.

**Serialization (Object to JSON):**

```java
import com.fasterxml.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();
Employee employee = new Employee(101, "Alice Johnson", "Engineering");

// Convert to JSON string
String json = mapper.writeValueAsString(employee);
System.out.println(json);
// Output: {"id":101,"name":"Alice Johnson","department":"Engineering"}
```

**Deserialization (JSON to Object):**

```java
String json = "{\"id\":101,\"name\":\"Alice Johnson\",\"department\":\"Engineering\"}";

Employee employee = mapper.readValue(json, Employee.class);
System.out.println(employee.getName());
// Output: Alice Johnson
```

**Key Jackson Annotations:**

| Annotation             | Purpose                                                    |
|------------------------|------------------------------------------------------------|
| `@JsonProperty`        | Maps a JSON key to a Java field with a different name.     |
| `@JsonIgnore`          | Excludes a field from serialization/deserialization.        |
| `@JsonFormat`          | Specifies formatting for dates, numbers, etc.              |
| `@JsonInclude`         | Controls inclusion rules (e.g., exclude null values).      |
| `@JsonCreator`         | Marks a constructor or factory method for deserialization.  |

**Annotation Example:**

```java
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {

    @JsonProperty("employee_id")
    private int id;

    private String name;

    @JsonIgnore
    private String internalCode;

    // constructors, getters, setters
}
```

This produces JSON like `{"employee_id":101,"name":"Alice Johnson"}`, omitting `internalCode` and renaming `id`.

### Gson

Gson is Google's JSON library. It is not included by default in Spring Boot, so you must add it as a dependency:

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
```

**Serialization:**

```java
import com.google.gson.Gson;

Gson gson = new Gson();
Employee employee = new Employee(101, "Alice Johnson", "Engineering");

String json = gson.toJson(employee);
System.out.println(json);
// Output: {"id":101,"name":"Alice Johnson","department":"Engineering"}
```

**Deserialization:**

```java
String json = "{\"id\":101,\"name\":\"Alice Johnson\",\"department\":\"Engineering\"}";

Employee employee = gson.fromJson(json, Employee.class);
System.out.println(employee.getName());
// Output: Alice Johnson
```

### Jackson vs Gson Comparison

| Feature          | Jackson                         | Gson                           |
|------------------|---------------------------------|--------------------------------|
| Spring default   | Yes                             | No (requires manual addition)  |
| Performance      | Generally faster for large payloads | Slightly simpler API         |
| Annotations      | Rich annotation set             | Limited annotations            |
| Streaming API    | Yes                             | Yes                            |
| Null handling    | Configurable via annotations    | Serializes nulls by default    |

In practice, Jackson is the standard choice for Spring Boot applications. Gson is common in Android development and smaller Java projects.

### Spring Boot Integration

Spring Boot auto-configures Jackson for REST controllers. When you return an object from a `@RestController` method, Spring automatically serializes it to JSON:

```java
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable int id) {
        // Spring auto-serializes this to JSON via Jackson
        return new Employee(id, "Alice Johnson", "Engineering");
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        // Spring auto-deserializes the JSON request body
        System.out.println("Received: " + employee.getName());
        return employee;
    }
}
```

The `@RequestBody` annotation triggers automatic deserialization of the incoming JSON payload, and the return value is automatically serialized to JSON in the response.

### Customizing Jackson in Spring Boot

You can customize Jackson's behavior globally via `application.properties`:

```properties
spring.jackson.serialization.indent-output=true
spring.jackson.default-property-inclusion=non_null
spring.jackson.date-format=yyyy-MM-dd
```

Or by defining a custom `ObjectMapper` bean:

```java
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
}
```

## Summary

- JSON is the standard data format for modern REST APIs and is more compact than XML.
- Jackson is Spring Boot's default JSON library, providing powerful annotations and auto-configured integration.
- Gson is a lightweight alternative, useful in non-Spring contexts but fully compatible with Spring Boot.
- Spring Boot automatically handles JSON serialization in `@RestController` methods via `@RequestBody` and return-value conversion.
- The patterns learned here -- serialization, deserialization, and annotation-driven mapping -- mirror the XML processing patterns from yesterday's material.

## Additional Resources

- [Jackson Project on GitHub](https://github.com/FasterXML/jackson)
- [Baeldung -- Jackson Tutorial](https://www.baeldung.com/jackson)
- [Gson User Guide](https://github.com/google/gson/blob/main/UserGuide.md)
