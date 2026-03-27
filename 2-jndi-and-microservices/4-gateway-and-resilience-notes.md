# Core Patterns: API Gateway, Load Balancing & Resilience

This section covers the routing and resilience layer of a Spring Cloud microservices architecture — how external traffic enters the system, how requests are distributed across instances, and how services protect themselves when downstream dependencies fail.

### 2. API gateway

Clients shouldn't know which service lives where. The gateway is the single entry point for all external traffic. Without a gateway, every external client (mobile app, frontend, third-party) must know the addresses of all services, handle auth independently, and manage versioning per-service. The gateway centralizes all of that at the edge.

**What to use: Spring Cloud Gateway.** This is the current, actively maintained Spring default - built on Project Reactor and Spring WebFlux (non-blocking). It replaced Netflix Zuul, which has been removed from Spring Cloud entirely and is incompatible with Spring Boot 3. Do not use Zuul for new projects. If you see it in legacy codebases, treat it as a migration candidate.

#### Benefits of the gateway pattern

**Single entry point** — external clients talk to one host and one port. The internal topology (which services exist, how many instances, which ports) is an implementation detail that clients never see. You can refactor, split, or rename services internally without changing the client's API.

**Security boundary** — authentication and authorization happen once at the gateway before any request reaches a downstream service. Internal services can trust that if a request arrived, it has already been validated. This avoids duplicating auth logic across every service.

**Cross-cutting concerns in one place** — rate limiting, request logging, CORS headers, SSL termination, and response compression are implemented once at the gateway rather than in every service individually.

**Protocol translation** — external clients speak HTTP/REST; internal services might speak gRPC, WebSocket, or expose different path conventions. The gateway can translate between them.

**API composition** — some gateways (including Spring Cloud Gateway with custom filters) can aggregate responses from multiple services into a single response, reducing the number of round trips a client must make.

**Traffic management** — canary deployments and A/B testing become a gateway-level concern. You can route 5% of traffic to a new version of a service without the client knowing.

#### How Spring Cloud Gateway works

Spring Cloud Gateway is built on **Project Reactor** and **Spring WebFlux**, which means it is fully non-blocking and reactive. Every request is handled on a small thread pool using an event loop rather than the traditional one-thread-per-request model. This makes it efficient under high concurrency - the gateway can proxy thousands of simultaneous connections without proportionally scaling threads.

Because of this, Gateway uses **WebFlux**, not the standard servlet stack. This is why you must not include `spring-boot-starter-web` in the gateway's `pom.xml` - the two stacks conflict at the dependency level.

**The three parts of a route**

Every route has three components:

```
Route = ID + Predicate(s) + URI  [+ optional Filters]
```

- **ID** — a unique name used in logs and actuator output
- **Predicate** — the condition that must be true for this route to match the incoming request
- **URI** — where to forward the request if the predicate matches
- **Filters** — optional transformations applied before forwarding (pre-filters) or after receiving the response (post-filters)

**Predicates**

Predicates are evaluated against the incoming `ServerWebExchange`. The most common is `Path`, but there are many built-in options:

```yaml
predicates:
  - Path=/orders/**            # URL path pattern
  - Method=GET,POST            # HTTP method
  - Header=X-Request-Id, \d+  # header name + regex value
  - Query=region, WEST         # query param name + value
  - Cookie=session, abc123     # cookie name + value
  - After=2025-01-01T00:00:00Z # only match after a date (canary scheduling)
```

Multiple predicates on the same route are AND'd together - all must match.

**Filters**

Filters are applied in a chain, similar to servlet filters.

```yaml
routes:
  - id: order-service
    uri: lb://order-service
    predicates:
      - Path=/orders/**
    filters:
      - AddRequestHeader=X-Source, gateway       # add header before forwarding
      - AddResponseHeader=X-Served-By, gateway   # add header to response
      - StripPrefix=1                            # strip /api from /api/orders/5
```

Spring Cloud Gateway also supports **global filters** that apply to every route without needing to declare them per-route - useful for logging, auth token validation, and correlation ID propagation.

**The `lb://` protocol and Eureka integration**

When the URI starts with `lb://`, Spring Cloud Gateway hands resolution off to **Spring Cloud LoadBalancer**:

1. Gateway receives a request matching the route
2. It looks up `order-service` in the load balancer's service instance list
3. The load balancer queries its local Eureka registry cache for all healthy instances of `order-service`
4. It picks one (round-robin by default) and returns the actual host and port
5. Gateway forwards the request to that resolved address

This means the gateway itself benefits from the same Eureka heartbeat and registry-fetch mechanism described above - it maintains a local cache of instances and resolves them without a registry round-trip on every request.

**Request flow**

```
External client
    └─→ GET :8080/orders/5
         │
         ▼
    Gateway (port 8080)
    ├─ Predicate check: Path=/orders/** ✓
    ├─ Pre-filters run (auth, logging, rate limit)
    ├─ lb://order-service → resolve via Eureka → :8084
    ├─ Forward: GET :8084/orders/5
    ├─ Receive response from order-service
    └─ Post-filters run (add headers, transform body)
         │
         ▼
External client receives response
```

**Configuration in `application.yml`**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service       # lb:// triggers load-balanced resolution
          predicates:
            - Path=/orders/**           # matches /orders, /orders/5, /orders/status/RECEIVED
        - id: warehouse-service
          uri: lb://warehouse-service
          predicates:
            - Path=/warehouses/**
```

Routes are evaluated in order - the first matching route wins. If no route matches, the gateway returns a 404.

### 3. Load balancing

With multiple instances of a service running, incoming requests must be distributed across them. There are two broad approaches:

- **Client-side** (Spring Cloud LoadBalancer): the calling service itself resolves available instances from the registry and picks one. No extra network hop, no proxy in the critical path.
- **Server-side** (NGINX, AWS ALB, Kubernetes Service): a dedicated proxy sits in front of the service instances and routes traffic. Simpler for clients, but adds a network hop and a component to manage.

**What to use: Spring Cloud LoadBalancer.** This is the current default, replacing Netflix Ribbon which has been removed from Spring Cloud. If you encounter `ribbon` in configuration files or `@RibbonClient` annotations, they are from legacy projects and should be migrated.

#### How Spring Cloud LoadBalancer works

**Instance list and local cache**

Spring Cloud LoadBalancer does not query Eureka on every request - that would add a network round-trip to every service call. Instead it maintains a local cache of instances for each service, populated from Eureka's registry. This cache is refreshed on the same 30-second interval as the Eureka client's registry fetch. When a load-balancing decision is needed, the cache is consulted in memory.

**Selecting an instance**

The default algorithm is **round-robin**: requests cycle through the available instances in order.

```
Instance list: [order-service:8084, order-service:8085, order-service:8086]

Request 1 → :8084
Request 2 → :8085
Request 3 → :8086
Request 4 → :8084  (wraps around)
```

Spring Cloud LoadBalancer also ships a **random** algorithm. You can switch it or provide your own `ReactorServiceInstanceLoadBalancer` bean to implement custom logic (e.g. weighted routing, zone-awareness, sticky sessions).


**The `@LoadBalanced` annotation**

When you annotate a `RestClient.Builder` or `WebClient.Builder` bean with `@LoadBalanced`, Spring wraps it with a load-balancing interceptor. This interceptor intercepts calls to logical service names (e.g. `http://order-service/orders`) and replaces them with real resolved addresses before the request goes out.

```java
@Bean
@LoadBalanced
public RestClient.Builder restClientBuilder() {
    return RestClient.builder().baseUrl("http://warehouse-service");
}
```

Without `@LoadBalanced`, the URL `http://warehouse-service` would fail DNS resolution because `warehouse-service` is not a real hostname. With it, the interceptor replaces it with a real `host:port` pulled from the load balancer cache.

In practice, most Spring Cloud microservices use client-side load balancing internally between services (via `@LoadBalanced` + Eureka) and server-side load balancing at the edge (the gateway or an external load balancer in front of the gateway).

### 4. Resilience strategies

When a downstream service is slow or failing, the default behavior in a microservice is bad: threads pile up waiting for responses, timeouts cascade, and the calling service eventually runs out of resources. Resilience4j provides a set of composable strategies to prevent this.

**What to use: Resilience4j** (`@CircuitBreaker`, `@Retry`, `@Bulkhead`, `@RateLimiter`). Resilience4j is the current default in Spring Cloud Circuit Breaker and is actively maintained. It replaced Netflix Hystrix, which is deprecated and has been removed from Spring Cloud. Do not use Hystrix for new projects.

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

#### Retry

Retry handles **transient failures** — brief network blips or a service instance that was briefly restarting. Instead of immediately surfacing the error to the caller, the same request is attempted again after a short wait.

```java
@Retry(name = "warehouseService")
public Optional<WarehouseSummary> findWarehouseForOrder(Order order) { ... }
```

```yaml
resilience4j:
  retry:
    instances:
      warehouseService:
        max-attempts: 3       # 1 original attempt + 2 retries
        wait-duration: 500ms  # pause between each attempt
```

Retry is appropriate when:
- The failure is likely temporary (network hiccup, brief pod restart)
- The operation is **idempotent** — calling it multiple times has the same effect as calling it once. A `GET` is always safe to retry. A `POST` that creates a resource is not, unless the downstream service handles duplicates.

Retry is **not** appropriate for sustained outages — blindly retrying a service that has been down for minutes just adds load and delays the fallback.

#### Circuit breaker

The circuit breaker prevents retry storms and thread exhaustion during sustained outages. It monitors the outcome of calls and, after enough failures, **opens** the circuit — subsequent calls short-circuit immediately to a fallback without attempting the real call at all.

**States:**

```
          too many failures
[Closed] ─────────────────→ [Open]
                               │
                    wait-duration-in-open-state
                               │
                               ↓
                           [Half-Open]  ← probe calls allowed through
                           /        \
                    success          failure
                       │                │
                       ↓                ↓
                   [Closed]          [Open]
```

- **Closed** — normal operation; failures are counted in a sliding window
- **Open** — failure threshold exceeded; all calls short-circuit to the fallback immediately
- **Half-open** — after the open wait period, a small number of probe calls are allowed through to test if the service has recovered

```java
@CircuitBreaker(name = "warehouseService", fallbackMethod = "warehouseFallback")
@Retry(name = "warehouseService")
public Optional<WarehouseSummary> findWarehouseForOrder(Order order) { ... }

// Invoked by Resilience4j via reflection when all retries fail or circuit is open.
// Signature must match the original method plus a Throwable as the last parameter.
@SuppressWarnings("unused")
private Optional<WarehouseSummary> warehouseFallback(Order order, Throwable t) {
    System.out.println("Warehouse service unavailable: " + t.getMessage());
    return Optional.empty();  // order-service will cancel the order gracefully
}
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      warehouseService:
        sliding-window-size: 5               # evaluate the last 5 call outcomes
        failure-rate-threshold: 60           # open if ≥60% of those calls failed
        wait-duration-in-open-state: 10s     # stay open for 10s before probing
        permitted-number-of-calls-in-half-open-state: 2  # probe with 2 calls
```

#### How retry and circuit breaker compose

When both annotations are on the same method, they wrap each other:

```
Request
  └─→ CircuitBreaker  (is circuit open? if yes → fallback immediately)
        └─→ Retry  (attempt up to max-attempts times)
              └─→ actual service call
```

`@CircuitBreaker` must be the **outer** decorator and `@Retry` the **inner** one. This means:
- Retry fires first and handles transient failures invisibly
- Only after all retries are exhausted does the circuit breaker record a failure
- 3 retries all failing = **1 failure** counted by the circuit breaker, not 3

If you reversed the order, the circuit breaker would count each individual retry attempt as a failure, and the circuit would open far too aggressively.

#### The fallback

The fallback method is the graceful degradation path — what the service does when the downstream is genuinely unavailable. It must:
- Have the **same return type** as the original method
- Accept the **same parameters** as the original method
- Have a **`Throwable`** as the final parameter (Resilience4j passes the cause)

In this codebase, `warehouseFallback` returns `Optional.empty()`, which causes `OrderService` to set the order status to `CANCELLED` rather than throwing. The system degrades gracefully — orders are acknowledged and recorded, just not fulfilled until the warehouse service recovers.
