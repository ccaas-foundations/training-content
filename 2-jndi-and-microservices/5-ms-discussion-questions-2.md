Asynchronous Messaging

1. In the order system, HTTP is used to check warehouse stock synchronously. Can you think of a scenario where that decision might change - where you'd want to make that call asynchronous instead?
2. If the Fulfillment service is down for two hours and 500 orders pile up in the queue, what happens when it comes back online? Is that a problem? How might you handle it?
3. What's the difference between a queue and a topic in practical terms? If you needed both Fulfillment and Notification to react to an order.placed event, which would you use, and why?
4. Kafka retains events and allows replay. What's a scenario in a contact center context where being able to replay past events would be valuable?
   
Config Server

1. The config server itself needs to be configured - its port is hardcoded and its URL is known in advance. Why can't the config server be looked up via Eureka the way other services are?
2. If a developer accidentally pushes a prod database URL into the order-service.yml (non-environment-specific) file in the config repo, what's the blast radius? How does the file structure help prevent this?
3. @RefreshScope lets you update a config value without redeploying. What kinds of values would you not want to hot-refresh at runtime, even if you technically could?

Containerization

1. Docker Compose uses depends_on to define startup order, but a service might start before its dependency is ready (e.g., Postgres is up but still initializing). How would you handle that in a real system?
2. The notes say "every developer runs the same environment." What problems does this solve? Are there any downsides to that level of uniformity?
3.  What's the conceptual difference between Docker Compose and Kubernetes? When would you reach for one vs. the other?
   
Observability

1.  You get a Grafana alert that Order Service error rate is elevated. Walk through how you'd use metrics, traces, and logs together to find the root cause.
2.  What's the difference between p95 latency and average latency? Why does it matter which one you're alerting on?
3.  The traceId appears in logs, traces, and metrics. What breaks if a service fails to forward the trace ID in an outbound HTTP call?
4.  Loki only indexes log labels rather than full text. What's the tradeoff there - what can ELK do that Loki can't, and vice versa?