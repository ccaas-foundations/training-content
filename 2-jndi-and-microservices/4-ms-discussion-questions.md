## Microservices Discussion Questions

1. What problem does independent scaling solve that you can't solve with a monolith? Can you point to a specific service in this project where that would matter?
2. Each service in this project has its own database. Why does that matter — what would go wrong if `order-service` just queried `warehouse-service`'s database directly?
3. The notes say "start with a monolith." Looking at the setup required for this project — Eureka, a gateway, circuit breakers, a message broker — when would all of this overhead actually be worth it?
4. The `fulfillment-service` was planned but never implemented, so `order-service` absorbed its responsibilities. What does this suggest about the difficulty of getting service boundaries right?
5. Eureka's registry cache means a crashed instance can take up to 90 seconds to disappear. What could go wrong in that window, and what does Eureka do to avoid making things worse during a network partition?
6. The gateway uses `lb://order-service` instead of a hardcoded address. What would break if you hardcoded it, and what does the gateway give you beyond just routing?
7. In `WarehouseAssignmentService`, `@CircuitBreaker` wraps `@Retry`. Why does the order matter — what would happen if they were swapped?
8. What's the difference between client-side and server-side load balancing? Where does each show up in this project?
9. Standing up the Eureka server only required one annotation. What is it actually doing for the rest of the services at runtime?
