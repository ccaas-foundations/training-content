# JNDI - Java Naming and Directory Interface

As you transition toward building distributed microservices, it is critical to understand how enterprise Java applications historically solved the problem of "finding resources." Before modern dependency injection frameworks (like Spring) and service registries (like Eureka) existed, JNDI was the standard API used to locate databases, message queues, and other services across a network. Understanding JNDI provides essential historical context and is still required when deploying applications to traditional Java EE application servers like Tomcat, WildFly, or WebLogic.

## What Is JNDI?

JNDI is a Java API that provides a unified interface for **looking up named resources** from a directory or naming service. The key word is *lookup* - your application never configures these resources itself. Instead, the **application server** (Tomcat, WildFly, WebLogic, etc.) maintains a server-side registry of named objects. Your code asks that registry for a resource by name at runtime, and the server hands back a fully configured object - connection pool, message factory, whatever was bound there by ops. Think of it as a phone book that lives on the server: your code only ever reads from it, and someone else entirely is responsible for keeping the entries up to date.

Common resources bound into JNDI:

- DataSources (database connection pools)
- JMS ConnectionFactories and Destinations
- Mail sessions
- Environment variables and configuration values
- EJB references

JNDI is part of the Java EE / Jakarta EE standard and is natively supported by application servers like Tomcat, WildFly, WebLogic, and WebSphere. It can also be configured in standalone Spring Boot apps with embedded Tomcat.

### Core Terminology

1. **Name:** The identifier used to register and look up an object. This is typically represented as a hierarchical string, often using a URL-like syntax (e.g., `java:comp/env/jdbc/myDataSource`).
2. **Context:** The core interface in JNDI (`javax.naming.Context`). A Context represents a set of name-to-object bindings. It is the starting point (the root) for resolving names. Every JNDI lookup starts by obtaining an `InitialContext`.
3. **Binding:** The association between a Name and an Object. When an administrator configures an application server, they "bind" a DataSource object to a specific name.

---

## The Naming Tree and Context

The JNDI namespace is a hierarchical tree of names, exactly like a filesystem directory. Just as a filesystem has folders nested inside folders with files at the leaves, JNDI has **Contexts** nested inside Contexts with bound resources at the leaves. A filesystem path like `/usr/local/bin/myprogram` maps to a file; a JNDI path like `java:comp/env/jdbc/myDb` maps to a DataSource. And just as you need to open a directory handle before you can read its contents, you need a `Context` object to navigate and query the JNDI tree.

The entry point is always an `InitialContext`, which connects to the server's naming service and gives you a handle to start traversing from:

```java
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/myDb");
```

The name `java:comp/env/jdbc/myDb` is a path through the tree:

- `java:` - the root context for all Java EE JNDI names, provided by the container
- `comp` - the component context, scoped to the current application component (your app)
- `env` - the environment subcontext, the standard location for app-declared resource references
- `jdbc/myDb` - the name you (or ops) chose for this particular DataSource

This scoping matters because `java:comp` is isolated per application - two apps deployed to the same server can each bind a `java:comp/env/jdbc/myDb` without colliding, just as two users on the same machine can each have their own `/home/user/documents/` without stepping on each other. Resources bound higher up the tree (e.g., `java:global/`) are server-wide and accessible to all apps, analogous to files in `/etc` that any process can read.

When you see `java:comp/env/` as a prefix in JNDI lookups, it's not boilerplate - it's the specific context where application-level resource references live by convention.

---

## Binding JNDI Objects

**Binding** is the act of registering an object in the JNDI namespace under a given name. This is what ops (or an app server admin) does - before your application ever starts, they configure the server to associate a name like `java:comp/env/jdbc/myDb` with a fully configured `DataSource` object. Your code never calls `bind()` in production; that's the whole point.

Programmatically, binding looks like this:

```java
Context ctx = new InitialContext();
ctx.bind("java:comp/env/jdbc/myDb", myDataSource);
```

You can also **rebind** to replace an existing binding without error (unlike `bind()`, which throws if the name is already taken):

```java
ctx.rebind("java:comp/env/jdbc/myDb", updatedDataSource);
```

And **unbind** to remove a name entirely:

```java
ctx.unbind("java:comp/env/jdbc/myDb");
```

In practice, developers only need to know `bind()` exists at a conceptual level - understanding it is how the server registers resources. In a traditional app server deployment, bindings are declared in config files like Tomcat's `context.xml`:

```xml
<Resource name="jdbc/myDb"
          auth="Container"
          type="javax.sql.DataSource"
          driverClassName="org.postgresql.Driver"
          url="jdbc:postgresql://localhost:5432/mydb"
          username="appuser"
          password="secret"
          maxTotal="20" />
```

The server reads this at startup and binds the configured `DataSource` into the JNDI tree. The application then looks it up - it never sees the username or password directly.

---

## Looking Up JNDI Objects

**Lookup** is the developer-facing half of the equation. Given a name, `Context.lookup()` traverses the naming tree and returns the bound object. The result is typed as `Object`, so you must cast it to the expected type.

```java
Context ctx = new InitialContext();
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/myDb");
```

You can also **list** the contents of a context to see what names are bound under it - useful for debugging:

```java
NamingEnumeration<NameClassPair> list = ctx.list("java:comp/env/jdbc");
while (list.hasMore()) {
    System.out.println(list.next().getName());
}
```

In Spring, you rarely call `lookup()` directly. Spring wraps it in `JndiTemplate` or `JndiObjectFactoryBean`, which handle the `InitialContext` lifecycle and exception translation for you. But under the hood, it's always the same `Context.lookup()` call.

---

## Common Exceptions

JNDI operations throw checked exceptions from the `javax.naming` package. Knowing what each one means saves significant debugging time.

**`NamingException`** is the base class for all JNDI exceptions. If you see a subclass you don't recognize, check its parent - it's always a `NamingException`.

**`NameNotFoundException`** is the most common one in practice. It means the name you passed to `lookup()` doesn't exist in the naming tree. This almost always means one of three things: the resource was never bound on the server, the JNDI name in your code doesn't match what ops configured, or the application hasn't been restarted since the binding was added.

```
javax.naming.NameNotFoundException: jdbc/myDb not found
```

**`NoInitialContextException`** means JNDI couldn't establish a connection to a naming service at all. In a Spring Boot embedded Tomcat app, this typically means JNDI support wasn't enabled. In a WAR deployment, it usually means the app isn't running inside an app server context.

```
javax.naming.NoInitialContextException: Need to specify class name in environment
    or system property, or in an application resource file: java.naming.factory.initial
```

**`CommunicationException`** indicates a network-level failure connecting to a remote naming service (relevant when using LDAP or remote JNDI providers).

**`AuthenticationException`** means the credentials provided to connect to the naming service were rejected.

**`NamingSecurityException`** is thrown when a lookup succeeds in finding the name, but the caller doesn't have permission to access it.

A practical tip: when you get a `NameNotFoundException`, before digging into code, verify what's actually bound on the server. On Tomcat, check `context.xml`. On WildFly, the admin console's JNDI tree view shows every bound name. Confirming the name exists - and matches exactly, including case - eliminates the most common source of JNDI bugs.

---

## The Core Idea: Ops Configures, Devs Consume

### How the split works

| Responsibility | Who Owns It | Where It Lives |
|---|---|---|
| Binding resources (DB URL, credentials, pool settings) | **Operations / Platform team** | Server config, container env, deployment pipeline |
| Looking up resources by name | **Developers** | Application source code |
| Changing a credential or connection string | **Ops** | Server config only - no code change required |

A developer writes:

```java
DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/myDb");
```

They have no idea what the database URL is, what the password is, or even which database vendor is being used. That's the point.

### Why we want this separation of concerns

**1. Secrets never touch source code**

Credentials committed to a repository - even a private one - are a significant security risk. With JNDI, the connection string and password live only in server configuration, which can be tightly access-controlled and audited independently of the codebase.

**2. Environment promotion without code changes**

The same WAR or JAR can be deployed to dev, QA, staging, and production. Ops binds different DataSources to the same JNDI name at each tier. The application code is identical across all environments.

**3. Clear audit boundary**

When a database password rotates, Ops makes one change in one place. There is no need to grep the codebase, update a properties file, redeploy from a new branch, or coordinate with the dev team. Audit logs for secret access are ops-layer logs, not buried in app commits.

**4. Principle of least privilege**

Developers genuinely cannot leak what they never see. A dev who has read access to the repository cannot retrieve production credentials, because those credentials are not in the repository.

**5. Centralized pool management**

Connection pool tuning (min/max connections, timeouts, validation queries) is handled at the server level by ops, not scattered across individual application configs.

---

## Using JNDI with Spring Boot (WAR Deployment Only)

### JAR vs WAR - a quick background

Spring Boot apps are packaged as **JARs** by default - a single self-contained executable that includes an embedded Tomcat server baked right in. You run it with `java -jar myapp.jar` and the whole stack starts up together. This is the modern, cloud-friendly model: the app owns its server, and there's nothing external to configure.

The older model, still common in enterprise environments, uses a **WAR** file. A WAR ships only the application code - no embedded server. Instead, it gets dropped into a running **external app server** (a standalone Tomcat installation, WildFly, WebLogic, etc.) that ops manages independently. The server was there before the application arrived, and it'll keep running after the application is swapped out. Ops controls the server's configuration - including its JNDI registry - and developers never touch it.

That distinction is why JNDI and Spring Boot have an awkward relationship. With an embedded JAR, the server lives inside your artifact, so any JNDI bindings end up in application code or `application.properties` - which developers own and can read, and the ops/dev separation collapses. If you do want to use JNDI with a Spring Boot application, the right approach is to package it as a WAR and deploy it to an external app server, letting ops configure the JNDI bindings in the server's own config files (`context.xml`, admin console, etc.) entirely outside the artifact. Here's how that looks from the application side:

### The application.properties shortcut

The simplest approach - tell Spring Boot the JNDI name and it resolves the rest automatically:

```properties
spring.datasource.jndi-name=java:comp/env/jdbc/myDb
```

Spring Boot will look up the DataSource from the server's registry at startup and inject it wherever a `DataSource` is needed. No Java config required.

The same works for JMS:

```properties
spring.jms.jndi-name=java:comp/env/jms/myConnectionFactory
```

### Explicit lookup with JndiObjectFactoryBean

If you need more control - deferred lookup, specifying a proxy interface - declare it as a `@Bean`:

```java
@Configuration
public class JndiDataSourceConfig {

    @Bean
    public DataSource dataSource() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/myDb");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false); // defer until first use
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }
}
```

Either way, the rest of your application just injects `DataSource` normally - nothing in the service or repository layer needs to know JNDI is involved.


---

## JNDI Limitations

Understanding where JNDI breaks down is just as important as knowing how to use it.

**Tightly coupled to the app server.** JNDI depends on a container's naming service. There's no meaningful JNDI story for non-servlet runtimes, making it nearly useless in cloud-native deployments where apps run as standalone JARs or containers without a full app server.

**The ops/dev separation breaks down with embedded Tomcat.** The clean separation - ops configures the server registry, devs only look up from it - only holds when you're deploying a WAR to an *external* app server (Tomcat, WildFly, WebLogic), where ops manages JNDI bindings in server config files like `context.xml` entirely outside the artifact. With Spring Boot's default embedded Tomcat model, the Tomcat instance boots *inside* your JAR, meaning the JNDI registry lives in-process with the application. Bindings end up configured in application code or `application.properties` - which developers own and can read. This is another reason JNDI is a poor fit for standard Spring Boot development: the core promise of the pattern doesn't survive the embedded server model.

**No secret rotation support.** JNDI binds a resource at startup. If a password rotates mid-run, you either restart the application or implement your own reconnection logic. There is no built-in mechanism to pick up a new credential without a restart.

**No audit trail for secret access.** JNDI tells you nothing about when a secret was read or by what process. App server logs may capture binding events, but there is no structured audit log comparable to what dedicated secret managers provide.

**Local development is painful.** Developers need to replicate JNDI bindings locally - usually via `SimpleNamingContextBuilder` (deprecated as of Spring 5.2) or test config hacks - just to run the app. This friction often leads to developers hardcoding fallback values in local profiles, which undermines the whole point.

**Not portable across environments or teams.** A JNDI binding configured in WildFly works differently than one in Tomcat, and differs again in WebLogic. Naming conventions, scope (`java:comp/env` vs `java:/` vs `java:jboss/`), and resource types vary across vendors. This makes infra a moving target if you ever change app servers.

**No support for dynamic or short-lived credentials.** All credentials bound via JNDI are long-lived by design. There's no concept of a lease, TTL, or auto-expiring credential - all of which are table stakes for modern secret management in regulated environments.

---

## Better Strategies for Managing Secrets

JNDI's limitations make it a poor fit outside of traditional app server deployments. Here are some alternatives:

### 1. Environment Variables

**The simplest and most portable approach.** Environment variables can be referenced in your `application.properties` or `application.yml` with the following notation:

```properties
# application.properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

Ops sets the environment variables in the deployment environment (shell, Docker, Kubernetes manifest, CI/CD pipeline). No secrets in code or config files.

**Pros:** Zero dependencies, universally supported, easy to reason about.  
**Cons:** Environment variables are visible to all processes in a container, can appear in logs and process listings, and have no built-in rotation or auditing.

---

### 2. Externalized Config with Spring Cloud Config Server

Spring Cloud Config Server externalizes configuration to a central Git repository (or Vault, S3, etc.). The app fetches its config at startup over HTTP.

```yaml
# bootstrap.yml
spring:
  cloud:
    config:
      uri: https://config-server.internal.corp.com
      label: production
```

**Pros:** Centralized, environment-specific config; supports Git history and rollback; integrates with Spring natively.  
**Cons:** Secrets still live in a Git repo (even a private one) unless you layer in Vault or encryption at rest. Config server itself becomes a critical single point of failure.

---

For more robust secret management, other widely-used strategies include **HashiCorp Vault** (encrypted storage, dynamic short-lived credentials, full audit trail - the enterprise standard via `spring-cloud-starter-vault-config`), **Kubernetes Secrets with the External Secrets Operator** (syncs secrets from Vault or cloud providers into K8s-native Secret objects, fits GitOps workflows), and **cloud-native managers** like AWS Secrets Manager, GCP Secret Manager, and Azure Key Vault (fully managed, automatic rotation, native IAM - at the cost of vendor lock-in).

---

## Summary

JNDI solves a real problem - keeping credentials out of application code and giving ops a clean, auditable boundary for secret management - but it is fundamentally an app-server-era solution. In modern microservices and cloud deployments, Vault or a cloud-native secret manager (often paired with Kubernetes External Secrets Operator) gives you the same separation of concerns with the addition of dynamic secrets, automatic rotation, and first-class audit trails. For Spring Boot specifically, the `spring.datasource.jndi-name` shortcut is the lowest-friction path into JNDI, while `spring-cloud-starter-vault-config` is the lowest-friction path into production-grade secret management.

---

## Discussion Questions

1. **JNDI as a concept.** JNDI is described as a "naming and directory interface." In your own words, what problem does it solve? What would a developer have to do instead if JNDI didn't exist?

2. **Names and the naming tree.** Why does JNDI use a hierarchical name like `java:comp/env/jdbc/myDb` rather than a simple flat key like `myDb`? What does each segment of that path give you that a flat key wouldn't?

3. **Context and scope.** Two different applications are deployed to the same Tomcat server. Both define a resource under `java:comp/env/jdbc/appDb`. Will they conflict? Explain why or why not using your understanding of how Context scoping works.

4. **Binding vs lookup.** What is the difference between binding and looking up a JNDI object? Which side does the developer own, and which side does ops own? Why is that split valuable?

5. **Common exceptions.** You deploy a new WAR and the application fails to start with a `NameNotFoundException`. Walk through the steps you'd take to diagnose and fix it. What are the most likely causes?

6. **JNDI and Spring Boot.** A teammate suggests using JNDI to manage the database credentials in your new Spring Boot microservice. What questions would you ask them before agreeing? Under what conditions would JNDI actually be the right call?

7. **Alternatives.** What are the main weaknesses of environment variables as a secret management strategy compared to JNDI? What do both approaches still fail to provide that something like HashiCorp Vault does?
