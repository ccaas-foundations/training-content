# Microservices Foundations

## What is a microservice?

A microservice is a small, independently deployable unit that owns a single business capability. Rather than one large application handling everything, each concern - users, catalog, orders, billing, notifications - lives in its own process, with its own codebase, database, and deployment lifecycle.

Services communicate over the network - typically REST/HTTP for synchronous calls, or a message bus (Kafka, JMS/ActiveMQ) for asynchronous events.

---

## The scaling problem

In a monolith, the unit of scale is the entire application. If one module gets a traffic spike, the only lever is to clone the whole thing.

### Healthy baseline - no scaling needed

![healthy baseline](./2-microservices-notes-images/baseline-healthy.png)

At normal load both architectures look identical. Catalog runs at ~72%, everything else between 30–55%. This is the target zone: busy enough to justify the instance, with headroom to absorb a burst. No scaling needed.

### Monolith under a Catalog spike

![monolith spike](./2-microservices-notes-images/monolith-spike.png)


Catalog's traffic tripled. To bring it back to a healthy utilisation level the monolith scales to 3 instances. The load balancer distributes evenly - Catalog lands at 72% on each instance. But Orders, Users, Billing, and Notif. had their traffic split across 3 instances too, dropping from 30–55% to 10–18%. 
You paid for 15 module-instances to solve a 3-instance Catalog problem.

### Microservices under the same spike

[!microservices spike](./2-microservices-notes-images/ms-spike.png)

Only the Catalog service scaled. Its load balancer split traffic across 3 Catalog instances - each sitting at 72%, right in the healthy zone. Every other service stayed exactly at baseline on a single instance. 
Total: 7 instances vs 15. No wasted capacity.

---

## Benefits of Microservices

### Independent scalability

Each service scales on its own terms. If Catalog sees a traffic spike, only the Catalog service gets new instances - Orders, Billing, and Notifications stay exactly where they are. You pay for what you need, where you need it.

This also means you can apply different scaling strategies per service. A CPU-bound image processing service scales horizontally with more instances. A memory-bound caching service scales vertically with larger instances. A queue-backed notification service scales based on queue depth rather than CPU. In a monolith, all of these would share the same scaling policy because they share the same process.

### Resilience and failure isolation

In a monolith, a bug in the Notification module can take down the entire application - one unhandled exception, one memory leak, one thread pool exhaustion can crash Orders, Catalog, and Billing alongside it. This is the cascading failure problem.

Microservices contain failures at the service boundary. If the Notification service crashes, Orders keeps processing. If the Recommendation service develops a memory leak, Checkout is unaffected. The blast radius of any single failure is limited to the users and services that depend on that one capability.

This isolation is reinforced by patterns like the **circuit breaker**: when a downstream service becomes slow or unresponsive, the circuit opens and calls short-circuit to a fallback immediately, rather than letting threads pile up and starve the calling service of resources. The system degrades gracefully instead of failing completely.

![circuit breaking](./2-microservices-notes-images/circuit-breaker-schematic.png)

For critical user-facing systems - e-commerce, banking, healthcare - the difference between "the recommendation engine is down" and "the entire site is down" is enormous, both operationally and commercially.

### Technology flexibility (polyglot architecture)

A monolith has one tech stack. Every module uses the same language, the same framework, the same database engine, because they all live in the same
deployable. Adding a new dependency means the whole application takes it on.


![polyglot](./2-microservices-notes-images/polyglot.png)

In a microservices architecture, each service owns its technology choices independently:

- A latency-sensitive routing service can be written in Go or Rust
- A data-science-heavy recommendation engine can be Python with PyTorch
- A legacy integration service can stay on Java 11 while the rest of the system moves to Java 21
- A read-heavy catalog service can use Elasticsearch while a transactional order service uses PostgreSQL
- A notification service can use a lightweight framework like Micronaut or Quarkus rather than the full Spring Boot stack

This is called a **polyglot architecture**. The constraint is the contract between services - the API shape and the message format - not the implementation behind it. Teams pick the right tool for their specific job rather than the least-wrong tool for the whole system.

In practice, most teams don't go fully polyglot - operational consistency has real value. But having the *option* to diverge when it genuinely matters is a significant architectural advantage.

### Independent deployability

In a monolith, deploying a one-line bug fix in Billing requires a full application build, test, and release. Every team's changes go out together, which means coordinating release windows, freezing code, and accepting risk from changes that have nothing to do with your fix.

In microservices, the Billing team deploys Billing. The Catalog team deploys Catalog. Releases are smaller, lower-risk, and more frequent. A team that owns a service end-to-end can ship a fix in hours rather than waiting for the next coordinated release cycle.

This maps directly to modern delivery practices: each service has its own CI/CD pipeline, its own test suite, its own deployment cadence. Canary deployments (routing 5% of traffic to the new version), blue/green deployments (keeping the old version live until the new one is verified), and rolling updates all become practical at the service level in ways that are difficult to achieve for a monolith.

### Team autonomy and ownership

Conway's Law states that a system's architecture tends to mirror the communication structure of the organisation that built it. Monoliths encourage - and often require - large, tightly coordinated teams. Every team touches the same codebase, which means merge conflicts, shared test environments, and shared release processes.

Microservices invert this. Each service maps to a small team that owns it end-to-end: design, build, test, deploy, and operate. That team makes technology decisions, sets its own standards, and ships independently. There is no shared codebase bottleneck.

The Amazon "two-pizza team" model is the canonical example: if a team can't be fed by two pizzas, it's too big, and the service it owns is probably too big too. Small teams with tight ownership move faster and take more accountability for the thing they built.

### Maintainability at scale

A 5-year-old monolith tends to become a "big ball of mud" - modules that were supposed to be independent develop implicit dependencies, shared database tables get used across concerns, and the cost of understanding and changing any part of the system grows over time.

Microservices enforce boundaries structurally. If the Catalog service and the Order service have separate databases, it is architecturally impossible for Order to directly query Catalog's schema - any data sharing has to go through the API. This forces good design discipline that a monolith can only enforce by convention.

Each service is also small enough to be fully understood by the team that owns it. Onboarding a new engineer to a 500-line service with a clear domain boundary is a different experience from onboarding them to a 500,000-line monolith.

### Granular observability

When something goes wrong in a monolith, the question is "which module caused it?" and the answer requires reading logs, tracing call stacks, and making inferences. You can observe the whole but not easily the parts.

In microservices, each service emits its own metrics, logs, and traces. You can see exactly which service is slow, which is throwing errors, and which upstream dependency is causing the problem - without guessing. Distributed tracing tools (Micrometer + Zipkin) let you follow a single user request across every service it touched, with timing data for each hop.

This granularity makes debugging faster and makes SLA accountability clearer: you know exactly which service missed its latency target.

---

## What you take on

These benefits come with real costs. Microservices are not the right choice for every system, and they're not free even when they are.

**Distributed system complexity** - network calls fail in ways that in-process calls don't. You need timeouts, retries, circuit breakers, and idempotency handling everywhere a service calls another service. A monolith with a bug is a bug. A microservices system with a misconfigured timeout is a cascading failure.

**No shared transactions** - ACID transactions across service boundaries don't exist. If updating Orders and sending a Notification need to happen atomically, you need the Saga pattern: a sequence of local transactions coordinated by events, with compensating transactions to roll back on failure. This is significantly more complex than a database transaction.

**Operational overhead** - you now have 10 services instead of 1. Each needs its own deployment pipeline, its own health monitoring, its own log aggregation, its own dependency management. The tooling that makes this manageable (Kubernetes, Docker, Prometheus, distributed tracing) is itself a significant investment to learn and operate.

**Testing complexity** - unit testing a service in isolation is easy. Testing the behaviour of 5 services interacting is hard. Contract testing (Spring Cloud Contract) and consumer-driven contracts help, but the test surface area is larger and harder to reason about than a monolith.

**Start with a monolith** - for a new product with unclear domain boundaries, a monolith is usually the right first choice. Extract services when boundaries become clear, when a specific module has scaling needs that differ from the rest, or when team ownership genuinely warrants separation. Premature decomposition into microservices creates all the operational overhead with none of the organizational benefit.

---

## Communication Between Services

Microservices need to talk to each other. For synchronous calls, where the
caller needs an immediate response, Spring Boot uses HTTP clients to make
REST calls directly from one service to another.

### RestClient

`RestClient` is the current Spring HTTP client, introduced in Spring 6.1 and
the recommended choice for new Spring Boot 3.x projects. It replaces the
deprecated `RestTemplate` with a fluent, readable API. Unlike `WebClient`
(which is reactive/non-blocking), `RestClient` is straightforward and
synchronous - the right default for most service-to-service calls.

**Dependency** — no extra dependency needed. `RestClient` is included in
`spring-boot-starter-web`.

**Creating a client**

The simplest approach is to inject a `RestClient.Builder` and build a
pre-configured client in your service class or `@Configuration`:

```java
@Service
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("http://localhost:8082")
            .build();
    }
}
```

If you have Eureka on the classpath, Spring will automatically configure the
builder with a load-balanced `baseUrl` resolver — so `http://catalog-service`
resolves via the service registry rather than a hardcoded host.

**GET — fetch a resource**
```java
public CatalogItem getItem(String itemId) {
    return restClient.get()
        .uri("/items/{id}", itemId)
        .retrieve()
        .body(CatalogItem.class);
}
```

**GET — fetch a list**
```java
public List<CatalogItem> getAllItems() {
    return restClient.get()
        .uri("/items")
        .retrieve()
        .body(new ParameterizedTypeReference<List<CatalogItem>>() {});
}
```

**POST — send a body**
```java
public Order createOrder(OrderRequest request) {
    return restClient.post()
        .uri("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .retrieve()
        .body(Order.class);
}
```

**Using `exchange` — access the full response**

When you need the response status code or headers alongside the body, use
`exchange` instead of `retrieve`:
```java
public CatalogItem getItemWithMeta(String itemId) {
    return restClient.get()
        .uri("/items/{id}", itemId)
        .exchange((request, response) -> {
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.bodyTo(CatalogItem.class);
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ItemNotFoundException("Item not found: " + itemId);
            } else {
                throw new RuntimeException(
                    "Unexpected status: " + response.getStatusCode()
                );
            }
        });
}
```

`exchange` gives you full control — useful when your branching logic depends
on status codes, when you need to read a response header (e.g. `Location`
after a `201 Created`), or when different status codes should produce
different return types.
```java
public URI createOrderAndGetLocation(OrderRequest request) {
    return restClient.post()
        .uri("/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange((request, response) -> {
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getHeaders().getLocation();
            }
            throw new RuntimeException(
                "Order creation failed: " + response.getStatusCode()
            );
        });
}
```

Note that when using `exchange`, Spring does not automatically close the
response body — you are responsible for consuming or closing it inside the
lambda to avoid resource leaks.

**Handling errors**

By default, `RestClient` throws `HttpClientErrorException` (4xx) or
`HttpServerErrorException` (5xx) on error responses. You can handle these
with a `onStatus` block:
```java
public CatalogItem getItem(String itemId) {
    return restClient.get()
        .uri("/items/{id}", itemId)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
            throw new ItemNotFoundException("Item not found: " + itemId);
        })
        .body(CatalogItem.class);
}
```

---

## Core patterns in microservices architecture

### 1. Service discovery

Services don't communicate via hardcoded IP addresses - instances spin up and down dynamically. A service registry solves this.

- Each service registers itself on startup with its host and port
- Callers query the registry to resolve an address before making a request
- Heartbeats detect and evict crashed instances automatically

**What to use: Netflix Eureka** (`@EnableEurekaServer`, `@EnableDiscoveryClient`). Eureka is in maintenance mode upstream at Netflix - no new features are being added - but it remains fully supported in Spring Cloud and is the simplest hands-on option. You will also encounter **HashiCorp Consul** in enterprise environments; it offers more advanced features (multi-datacenter support, DNS interface, built-in key-value config store) but adds operational complexity that isn't necessary at this level. Understand both exist, but we'll be implementing Eureka.

### 2. API gateway

Clients shouldn't know which service lives where. The gateway is the single entry point for all external traffic.

Responsibilities:
- **Routing** - map `/catalog/**` to the Catalog service
- **Authentication** - validate tokens before requests reach services
- **Rate limiting** - protect services from being overwhelmed
- **SSL termination** - handle TLS at the edge, plain HTTP internally

**What to use: Spring Cloud Gateway.** This is the current, actively maintained Spring default - built on Project Reactor and Spring WebFlux (non-blocking). It replaced Netflix Zuul, which has been removed from Spring Cloud entirely and is incompatible with Spring Boot 3. Do not use Zuul for new projects. If you see it in legacy codebases, treat it as a migration candidate.

### 3. Load balancing

With multiple instances of a service running, incoming requests must be distributed. Two levels:

- **Client-side** (Spring Cloud LoadBalancer): the calling service resolves available instances from the registry and picks one. No extra network hop.
- **Server-side** (NGINX, AWS ALB, Kubernetes Service): a proxy sits in front and routes traffic. Simpler for clients, single point of control.

**What to use: Spring Cloud LoadBalancer.** This is the current default, replacing Netflix Ribbon which has been removed from Spring Cloud. If you encounter `ribbon` in configuration files or `@RibbonClient` annotations, they are from legacy projects and should be migrated.

### 4. Circuit breaking

A slow or failing downstream service can cascade failures upstream as threads
pile up waiting for responses. The circuit breaker pattern interrupts this.

States:
- **Closed** - requests flow normally; failures are counted
- **Open** - failure threshold exceeded; requests short-circuit immediately to a fallback (cached response, default value, error message)
- **Half-open** - after a timeout, a probe request is allowed through; if it succeeds the circuit closes again

**What to use: Resilience4j** (`@CircuitBreaker`, `@Retry`, `@Bulkhead`, `@RateLimiter`). Resilience4j is the current default in Spring Cloud Circuit Breaker and is actively maintained. It replaced Netflix Hystrix, which is deprecated and has been removed from Spring Cloud. Do not use Hystrix for new projects. Resilience4j is designed for Java 8+ and functional programming, is lighter weight, and supports more patterns (retry, bulkhead, rate limiter) than Hystrix did out of the box.

### 5. Asynchronous messaging

Not every service call needs an immediate response. Decoupling producers and consumers via a message broker allows services to operate independently of each other's availability.

| Pattern | Description | Use case |
|---|---|---|
| Queue | One producer → one consumer | Order placed → fulfillment picks it up |
| Topic/pub-sub | One producer → many consumers | `OrderShipped` → Notification + Analytics |

**What to use: ActiveMQ Artemis for hands-on JMS work; understand Kafka as the at-scale alternative.**

Artemis is the current, actively maintained next-generation ActiveMQ broker. It supports the JMS standard (`@JmsListener`), and can run as an embedded broker in Spring Boot, and maps directly to enterprise integration patterns. 

Apache Kafka is the dominant event streaming platform in modern microservices at scale. It is designed for extremely high throughput, persistent message retention, and replay - Kafka keeps messages on disk and consumers can re-read them, unlike Artemis which deletes messages once consumed. In practice, Artemis and Kafka are not direct competitors: an enterprise system might use Artemis for internal service-to-service transactional messaging and Kafka for high-volume event streams feeding analytics pipelines. Kafka is worth understanding conceptually; Artemis is the right tool to implement in this curriculum given the JMS foundation already established.

In Spring Boot: `@JmsListener` with ActiveMQ Artemis for queues; `@KafkaListener` with Apache Kafka for event streaming.

### 6. Config server

Hardcoding configuration per service leads to inconsistency and makes environment-specific values (DB URLs, credentials, feature flags) difficult to manage across dozens of services.

A config server externalizes all configuration into a central store - typically a Git repository - and serves it to services on startup and on refresh.

**What to use: Spring Cloud Config Server.** Actively maintained, no deprecation concerns. Services pull config via `spring.config.import=configserver:http://config:8888`. Config can be refreshed at runtime via `/actuator/refresh` without redeployment using `@RefreshScope`.

Note: Consul can also serve as a config store if you are already using it for service discovery, making the Spring Cloud Config Server redundant in that setup. Since the curriculum uses Eureka (not Consul), Spring Cloud Config is the natural pairing.

### 7. Containerization

Each service and its runtime dependencies are packaged into a Docker image. This ensures the service behaves identically in development, CI, and production.

Key concepts:
- **Dockerfile** - recipe for building the image (base image, copy jar, entrypoint)
- **Image** - immutable snapshot of the service and its runtime
- **Container** - a running instance of an image; isolated process
- **Docker Compose** - defines all services, networks, and volumes for local development in a single `docker-compose.yml`

In production, container orchestration (Kubernetes) manages scheduling, health checks, scaling, and rolling deployments across a cluster.

### 8. Monitoring and distributed tracing

In a monolith, a stack trace tells you exactly what happened. In microservices, a single user request may touch 5–10 services - a failure anywhere in the chain is hard to trace without tooling.

**Metrics** (what happened):
- Expose via Spring Boot Actuator → Prometheus scrapes → Grafana visualizes
- Key signals: request rate, error rate, latency (p95/p99), CPU/memory

**Distributed tracing** (where it happened):
- Each request is assigned a trace ID that propagates across service boundaries via HTTP headers
- **Micrometer Tracing** + **Zipkin** or **Jaeger**: visualize the full call tree - which service called which, how long each hop took, where latency or errors originated

**Centralized logging**:
- Services write structured logs (JSON) to stdout
- A log aggregator (ELK stack: Elasticsearch, Logstash, Kibana) or **Grafana Loki** collects, indexes, and makes them searchable
- Correlate logs across services using the trace ID

The goal is **observability**: the ability to ask arbitrary questions about system behavior from the outside, without needing to add new instrumentation after the fact.

---

## Spring Boot tooling map

| Pattern | Supporting Technology | Deprecated Technology |
|---|---|---|
| Service discovery | Spring Cloud Netflix Eureka | - |
| API gateway | Spring Cloud Gateway | ~~Netflix Zuul~~ (removed) |
| Load balancing | Spring Cloud LoadBalancer | ~~Netflix Ribbon~~ (removed) |
| Circuit breaking | Resilience4j | ~~Netflix Hystrix~~ (removed) |
| Messaging (transactional) | Spring JMS + ActiveMQ Artemis | - |
| Messaging (streaming) | Apache Kafka | - |
| Config management | Spring Cloud Config Server | - |
| Containerization | Docker + Docker Compose | - |
| Metrics | Spring Actuator + Prometheus + Grafana | - |
| Distributed tracing | Micrometer + Zipkin | - |
| Centralized logging | ELK stack / Grafana Loki | - |
