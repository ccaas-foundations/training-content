# JNDI - Java Naming and Directory Interface

## What Is JNDI?

JNDI is a Java API that provides a unified interface for **looking up named resources** from a directory or naming service. The key word is *lookup* - your application never configures these resources itself. Instead, the **application server** (Tomcat, WildFly, WebLogic, etc.) maintains a server-side registry of named objects. Your code asks that registry for a resource by name at runtime, and the server hands back a fully configured object - connection pool, message factory, whatever was bound there by ops. Think of it as a phone book that lives on the server: your code only ever reads from it, and someone else entirely is responsible for keeping the entries up to date.

Common resources bound into JNDI:

- DataSources (database connection pools)
- JMS ConnectionFactories and Destinations
- Mail sessions
- Environment variables and configuration values
- EJB references

JNDI is part of the Java EE / Jakarta EE standard and is natively supported by application servers like Tomcat, WildFly, WebLogic, and WebSphere. It can also be configured in standalone Spring Boot apps with embedded Tomcat.

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

## Security Note: JNDI and Log4Shell

Worth knowing if you're teaching this: the Log4Shell vulnerability (CVE-2021-44228, December 2021) exploited JNDI's ability to load remote objects via LDAP when attacker-controlled strings were logged by Log4j 2. This was not a flaw in JNDI's design for credential management - it was a flaw in allowing arbitrary remote classloading through JNDI lookups in a logging library. Modern JDKs (8u191+, 11.0.1+) disabled remote class loading via JNDI by default. Still, it's a useful reminder that JNDI is a powerful mechanism and its scope should be deliberately locked down in production.

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

JNDI's limitations make it a poor fit outside of traditional app server deployments. Here are the leading alternatives, roughly in order of capability.

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
