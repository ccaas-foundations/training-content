# ecommerce-spring

A message-driven Spring Boot microservice that processes incoming e-commerce orders from a JMS queue, assigns warehouse fulfillment, and persists order state.

## Architecture Overview

- **`messaging/`** — JMS listener (inbound adapter). Deserializes incoming messages and delegates to the service layer.
- **`services/`** — Business logic. `OrderService` owns the full order lifecycle; `WarehouseAssignmentService` queries for a matching warehouse.
- **`repositories/`** — Spring Data JPA interfaces for persistence.
- **`models/`** — JPA entities and enums (`Order`, `Customer`, `Warehouse`, `OrderStatus`, `ShippingRegion`).

## Order Processing Flow

1. `OrderConsumer` receives a JSON order message from the `order.fulfillment.queue` JMS queue
2. Deserializes the message into an `Order` object and delegates to `OrderService`
3. `OrderService` sets timestamp and initial status (`RECEIVED`), then calls `WarehouseAssignmentService`
4. `WarehouseAssignmentService` queries for a warehouse matching the order's shipping region and SKU
5. `OrderService` applies the result — status becomes `ASSIGNED` (warehouse found) or `CANCELLED` (no match)
6. Order is persisted via `OrderRepository`

## Infrastructure

- **Message broker**: Apache Artemis (localhost:61616)
- **Database**: H2 in-memory (H2 console available at `/h2-console`)
- **Port**: 8084

---

## Changelog

### 2026-03-20 — Post-training refactor

Changes made after the class build to improve layered architecture and reduce side effects.

#### Added `OrderService`
- Created `services/OrderService.java` to own the full order lifecycle: setting initial status/timestamp, coordinating warehouse assignment, and persisting the result.
- Previously this logic was split between `OrderConsumer` (timestamp, status, save) and `WarehouseAssignmentService` (mutation of the order object).

#### Refactored `WarehouseAssignmentService`
- Renamed method from `assignWarehouseToOrder(Order)` to `findWarehouseForOrder(Order)`.
- Return type changed from `Order` to `Optional<Warehouse>`.
- The original method mutated the passed-in order object AND returned it — the caller already had the same reference, making the return value misleading.
- The service's responsibility is now clearly scoped to finding a warehouse; status updates and persistence happen in `OrderService`.

#### Refactored `OrderConsumer`
- Removed direct dependencies on `OrderRepository` and `WarehouseAssignmentService`.
- Now depends only on `OrderService` — the consumer's only job is deserialization and delegation.
- Controllers/listeners should not make repository calls directly; that belongs in the service layer.

---

### Prior commits — built with the class

| Commit | Description |
|--------|-------------|
| `e57ef5c` | updating package structure |
| `cad0bf7` | rest of spring data + jms demo |
| `ec28c2f` | update artemis setup |
| `720e54b` | thursday afternoon spring data jpa example |
| `639c479` | friday morning spring jpa updates |
| `17dc9f6` | adding spring data notes and spring demo starter |
