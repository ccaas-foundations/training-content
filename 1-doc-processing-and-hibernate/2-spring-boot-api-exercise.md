# Group Exercise: Dual-Format REST API with Spring Boot


---

## Scenario

Build a simple **Book Catalog API** using Spring Boot that returns data in both JSON and XML,
controlled by the client via the `Accept` request header.

---

## Tech Stack

- Java 17+
- Spring Boot, including `spring-boot-starter-webmvc`
- Maven
- `jackson-dataformat-xml` (for XML support)

### Required dependency (`pom.xml`)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

> Jackson (JSON) is included in Spring Boot by default. Adding `jackson-dataformat-xml`
> extends it to handle XML with no extra configuration.

---

## Data Model

```java
@XmlRootElement
public class Book {
    private Long id;
    private String title;
    private String author;
    private int year;
    private String genre;

    // constructors, getters, setters
}
```

> `@XmlRootElement` is required for JAXB to serialize/deserialize XML.

---
## Package Structure
```
src/main/java/com/example/bookapi/
├── controller/
│   └── BookController.java     # HTTP only — routing, status codes, response wrapping
├── service/
│   └── BookService.java        # Business logic — lookups, validation, transformations
├── model/
│   └── Book.java               # Data model — POJO with JAXB/Jackson annotations
└── BookApiApplication.java
```
---

## Endpoints

| Method | Path         | Description         |
|--------|--------------|---------------------|
| GET    | `/books`     | List all books      |
| GET    | `/books/{id}`| Get a single book   |
| POST   | `/books`     | Add a new book      |

### Content Negotiation

All endpoints must support both formats via the `Accept` header:

| `Accept` Header        | Response Format |
|------------------------|-----------------|
| `application/json`     | JSON            |
| `application/xml`      | XML             |
| *(not set)*            | JSON (default)  |


---

Use an **in-memory store only** — no database required.

```java
@Service
public class BookService {
    private final List<Book> books = new ArrayList<>(List.of(
        new Book(1L, "Clean Code", "Robert C. Martin", 2008, "Software"),
        new Book(2L, "The Pragmatic Programmer", "Hunt & Thomas", 1999, "Software")
    ));

    // getAll(), findById(), add()
}
```

---

## Error Handling

| Scenario                          | Status | Behaviour                                      |
|-----------------------------------|--------|------------------------------------------------|
| Book not found (`GET /books/{id}`)| `404`  | Return error message in the requested format   |
| Missing required fields (`POST`)  | `400`  | Return validation error in the requested format|

Error responses should honour the `Accept` header — if the client asked for XML, the error body should also be XML.

---

## Done Criteria

- [ ] `GET /books` returns all books as JSON or XML depending on `Accept` header
- [ ] `GET /books/{id}` returns a single book, or a `404` error in the correct format
- [ ] `POST /books` adds a book and returns it with a `201` status
- [ ] `POST /books` with a missing field returns a `400` error
- [ ] All endpoints tested manually with curl or Postman

