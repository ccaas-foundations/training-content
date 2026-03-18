# Introduction to Java Messaging Service (JMS)

## Learning Objectives

- Understand asynchronous messaging and why it matters in distributed systems.
- Describe the JMS API, its core components, and messaging models.
- Differentiate between point-to-point (queues) and publish/subscribe (topics) models.
- Use Spring's JmsTemplate to send and receive messages in a Spring Boot application.

## Why This Matters

Document processing and data persistence (the focus of this week) often occur as part of larger distributed workflows. A user uploads an XML document, a service processes it, and the result is persisted via Hibernate. JMS provides the asynchronous communication layer that decouples these steps, allowing each component to operate independently and at its own pace. Understanding JMS is foundational for the microservices architecture you will encounter in Week 2.

## The Concept

### What Is Messaging?

Messaging is a method of communication between software components where a **producer** sends a message to an intermediary (the **message broker**), and a **consumer** retrieves it. The producer and consumer do not need to be available at the same time -- this is what makes messaging **asynchronous**.

Key benefits:

- **Loose coupling** -- The sender does not know (or care) who receives the message.
- **Reliability** -- Messages can be persisted by the broker, surviving application crashes.
- **Scalability** -- Multiple consumers can process messages in parallel.
- **Buffering** -- The broker absorbs traffic spikes, preventing downstream overload.

### The JMS API

Java Messaging Service (JMS) is a Java API specification (part of Jakarta EE) that defines a standard interface for messaging. It does not implement a broker itself -- rather, it provides a set of interfaces that broker vendors implement.

Common JMS providers:

| Provider         | Description                                      |
|------------------|--------------------------------------------------|
| Apache ActiveMQ  | Open-source, widely used, embeddable in Spring.  |
| RabbitMQ         | Supports JMS via a plugin; primarily AMQP-based. |
| IBM MQ           | Enterprise-grade, common in banking/finance.     |
| Amazon SQS       | Cloud-native, with JMS client libraries.         |

### Core JMS Components

| Component             | Role                                                                |
|-----------------------|---------------------------------------------------------------------|
| `ConnectionFactory`   | Creates connections to the JMS broker.                              |
| `Connection`          | An active connection to the broker.                                 |
| `Session`             | A single-threaded context for producing and consuming messages.     |
| `MessageProducer`     | Sends messages to a destination (queue or topic).                   |
| `MessageConsumer`     | Receives messages from a destination.                               |
| `Destination`         | The target -- either a **Queue** or a **Topic**.                    |
| `Message`             | The data payload (TextMessage, ObjectMessage, MapMessage, etc.).    |

### Messaging Models

**Point-to-Point (Queue):**

```
Producer  -->  [Queue]  -->  Consumer
```

- Each message is consumed by **exactly one** consumer.
- Messages remain in the queue until consumed or expired.
- Use case: task distribution, work queues, order processing.

**Publish/Subscribe (Topic):**

```
Publisher  -->  [Topic]  -->  Subscriber A
                         -->  Subscriber B
                         -->  Subscriber C
```

- Each message is delivered to **all active subscribers**.
- Subscribers must be active at the time of publication (unless using durable subscriptions).
- Use case: event broadcasting, notifications, real-time updates.

### JMS in Spring

Spring provides first-class JMS support through the `spring-jms` module (included via `spring-boot-starter-activemq` or `spring-boot-starter-artemis`).

The key abstraction is `JmsTemplate`, which simplifies sending and receiving messages by handling connection management, session creation, and resource cleanup automatically.

### JmsTemplate

`JmsTemplate` follows the same template pattern found throughout Spring (like `JdbcTemplate` and `RestTemplate`). It eliminates boilerplate code and provides:

- Automatic connection/session management.
- Message conversion (converting Java objects to JMS `Message` types and back).
- Exception translation (JMS-specific exceptions are translated to Spring's `JmsException` hierarchy).

**Sending a Message:**

```java
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final JmsTemplate jmsTemplate;

    public OrderProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendOrder(String orderDetails) {
        jmsTemplate.convertAndSend("order-queue", orderDetails);
        System.out.println("Order sent to queue: " + orderDetails);
    }
}
```

**Receiving a Message (Listener):**

```java
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @JmsListener(destination = "order-queue")
    public void receiveOrder(String orderDetails) {
        System.out.println("Order received: " + orderDetails);
    }
}
```

The `@JmsListener` annotation tells Spring to register this method as an asynchronous message consumer. Spring automatically polls the queue and invokes the method when a message arrives.

### Spring Boot Configuration

To use an embedded ActiveMQ broker in Spring Boot, add the dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
```

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-artemis</artifactId>
</dependency>
```


And configure in `application.properties`:

```properties
spring.activemq.broker-url=vm://localhost?broker.persistent=false
spring.activemq.in-memory=true
spring.jms.pub-sub-domain=false
```

Setting `pub-sub-domain=false` uses queues (point-to-point). Set it to `true` for topics (publish/subscribe).


### Setting up your Artemis Broker

- download Apache Artemis: https://artemis.apache.org/components/artemis/download/

- create your artemis broker

```
./artemis create ecommerce-broker --user admin --password admin --require-login
```

- navigate into the directory of our broker
- start/run the broker

```
./artemis.cmd run 
##runs with logs in our terminal
```

```
./artemis-service start 
##runs in the background as a service
```

- open the console and login at localhost:8161/console


### Message Types

JMS defines several message types:

| Type              | Payload                                      |
|-------------------|----------------------------------------------|
| `TextMessage`     | A `String` (most commonly used with JSON/XML payloads). |
| `ObjectMessage`   | A serializable Java object.                  |
| `MapMessage`      | A set of key-value pairs.                    |
| `BytesMessage`    | A stream of raw bytes.                       |
| `StreamMessage`   | A stream of Java primitive values.           |

When using `jmsTemplate.convertAndSend()`, Spring's `MessageConverter` handles the conversion. The default is `SimpleMessageConverter`, which maps `String` to `TextMessage` and `Serializable` to `ObjectMessage`.

## Code Example

A complete minimal setup:

```java
@SpringBootApplication
@EnableJms
public class JmsApp {
    public static void main(String[] args) {
        SpringApplication.run(JmsApp.class, args);
    }
}
```

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer producer;

    public OrderController(OrderProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public String placeOrder(@RequestBody String order) {
        producer.sendOrder(order);
        return "Order placed successfully";
    }
}
```

Posting JSON to `/api/orders` places the message on the queue, where the `OrderConsumer` listener processes it asynchronously.

## Summary

- JMS defines a standard API for asynchronous messaging between Java components.
- Queues (point-to-point) deliver each message to one consumer; topics (pub/sub) broadcast to all subscribers.
- Spring's `JmsTemplate` eliminates boilerplate and integrates seamlessly with Spring Boot.
- `@JmsListener` provides annotation-driven message consumption.
- We will explore JMS client patterns and advanced message types in more depth on Wednesday.

## Additional Resources

- [Spring JMS Reference Documentation](https://docs.spring.io/spring-framework/reference/integration/jms.html)
- [Baeldung -- Spring JMS](https://www.baeldung.com/spring-jms)
- [Jakarta Messaging Specification](https://jakarta.ee/specifications/messaging/)
