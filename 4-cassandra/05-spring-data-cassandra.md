# Spring Data Cassandra Guide

Spring Data Cassandra is Spring’s approach to working with Cassandra using the same familiar repository and entity patterns we’ve used in Spring Data JPA. At first glance, the experience feels very similar—we still define domain objects, map them to tables, and rely on Spring to handle much of the data access layer. The key shift is in how we think about the data itself. Unlike JPA, where we often modeled relationships and leaned on joins, Cassandra pushes us to design around how we query the data. That means we spend more time defining composite primary keys (partition keys and clustering columns), since they control both how data is distributed and how it can be efficiently retrieved. Once we make that mental shift, the transition from JPA is smooth.

---

## application.yml

```yaml
spring:
  cassandra:
    keyspace-name: ecommerce_analytics   # required - the Cassandra keyspace to connect to
    contact-points: 127.0.0.1            # one or more nodes to bootstrap the connection
    port: 9042                           # default Cassandra port
    local-datacenter: datacenter1        # required - must match your Cassandra DC name
    schema-action: NONE                  # controls DDL behavior on startup 
                                         # (NONE, CREATE_IF_NOT_EXISTS, CREATE, RECREATE, RECREATE_DROP_UNUSED)
```

---

## Entity Mapping

A table entity is a plain Java class annotated with `@Table`. Each non-key field maps to a Cassandra column.

```java
@Table("invoice_events")
public class InvoiceEvent {

    @PrimaryKey
    private InvoiceEventKey key;

    @Column("status")    // optional if field name already matches column name
    private String status;

    private String warehouse;  // maps to "warehouse" column automatically
}
```

### Column name conventions

Spring Data Cassandra automatically converts camelCase field names to lowercase column names:

| Java field | CQL column |
|---|---|
| `status` | `status` |
| `invoiceId` | `invoiceid` |
| `placedAt` | `placedat` |
| `itemSkus` | `itemskus` |

If you want to conform to the naming conventions in Cassandra, you should use the `@Column` annotation to provide the snake_case name of the column. This is only required when the desired CQL column name differs from what the convention would produce.

### Supported field types

| Java type | Cassandra type |
|---|---|
| `UUID` | `uuid` |
| `String` | `text` |
| `Instant` | `timestamp` |
| `BigDecimal` | `decimal` |
| `Integer` / `int` | `int` |
| `Long` / `long` | `bigint` |
| `Boolean` / `boolean` | `boolean` |
| `List<T>` | `list<T>` |
| `Set<T>` | `set<T>` |
| `Map<K,V>` | `map<K,V>` |

---

## Composite Primary Keys

When a table has more than one column in its primary key, you need a separate key class.

### The key class

```java
@PrimaryKeyClass
public class InvoiceEventKey implements Serializable {

    @PrimaryKeyColumn(name = "invoice_id", type = PrimaryKeyType.PARTITIONED)
    private UUID invoiceId;

    @PrimaryKeyColumn(name = "event_time", type = PrimaryKeyType.CLUSTERED)
    private Instant eventTime;

    // required: no-arg constructor, equals, hashCode
}
```

### The entity class

```java
@Table("invoice_events")
public class InvoiceEvent {

    @PrimaryKey
    private InvoiceEventKey key;

    // regular columns...
}
```

### Single-column primary key

When you have a single partition key and no clustering columns, you can skip the key class entirely and use `@Id` or `@PrimaryKey` directly on the field:

```java
@Table("invoices")
public class Invoice {

    @Id
    private UUID invoiceId;

    private String status;
}
```

`@Id` (from `org.springframework.data.annotation`) and `@PrimaryKey` (from `org.springframework.data.cassandra.core.mapping`) are interchangeable for single-column keys.

---

## Annotation Reference

### `@Table`

Marks the class as a Cassandra table entity.

```java
@Table("my_table_name")
```

- Required when the class name doesn't match the table name after lowercasing
- If omitted, Spring Data uses the lowercased class name as the table name

---

### `@PrimaryKey`

Marks the field in an entity that holds the composite key object.

```java
@PrimaryKey
private InvoiceEventKey key;
```

- Required when using a `@PrimaryKeyClass`

---

### `@PrimaryKeyClass`

Marks a class as a composite primary key. The class must:
- Implement `Serializable`
- Have a no-arg constructor
- Implement `equals` and `hashCode`

---

### `@PrimaryKeyColumn`

Declares a field within a `@PrimaryKeyClass` as part of the primary key.

```java
@PrimaryKeyColumn(name = "warehouse", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
@PrimaryKeyColumn(name = "status",    ordinal = 1, type = PrimaryKeyType.CLUSTERED)
@PrimaryKeyColumn(name = "invoice_id",ordinal = 2, type = PrimaryKeyType.CLUSTERED)
```

| Attribute | Required | Default | Description |
|---|---|---|---|
| `name` | No | camelCase to snake_case of field name | CQL column name |
| `type` | Yes* | `CLUSTERED` | `PARTITIONED` or `CLUSTERED` |
| `ordinal` | No** | - | Ordering within the same type group |
| `ordering` | No | `ASCENDING` | Sort direction for clustering columns: `ASCENDING` or `DESCENDING` |

\* Effectively required - the default of `CLUSTERED` means you must explicitly set `PARTITIONED` on at least one field or no partition key exists.

\*\* Only meaningful when you have multiple columns of the same type. With one `PARTITIONED` and one `CLUSTERED` column, `ordinal` can be omitted entirely.

#### `ordinal` rules

- `ordinal` ranks columns **within their type group only** - partition columns are ranked separately from clustering columns
- Values only need to be distinct within the same type; you can reuse the same number across types
- Multiple partition key columns (composite partition key) are ordered by `ordinal`
- Multiple clustering columns are ordered by `ordinal` - lower ordinal sorts first

#### Clustering sort order

```java
// newest-first ordering
@PrimaryKeyColumn(name = "placed_at", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
private Instant placedAt;
```

This is enforced at the storage level in Cassandra, not in Java - it affects how data is physically stored and how range queries behave.

---

### `@Column`

Maps a field to a specific CQL column name.

```java
@Column("my_column_name")
private String someField;
```

Optional when the field name already converts correctly via camelCase→snake_case convention.

---

## Repository Queries

Repositories extend `CassandraRepository<Entity, KeyType>`.

```java
@Repository
public interface InvoiceEventRepository extends CassandraRepository<InvoiceEvent, InvoiceEventKey> {

    // derived query - translates to: WHERE invoice_id = ?
    List<InvoiceEvent> findByKeyInvoiceId(UUID invoiceId);
}
```

### Derived query method naming

To query on a field inside the key class, prefix with `key` + the field name in PascalCase:

| Field path | Method name segment |
|---|---|
| `key.invoiceId` | `ByKeyInvoiceId` |
| `key.customerId` | `ByKeyCustomerId` |
| `key.warehouse` | `ByKeyWarehouse` |
| `key.status` | `ByKeyStatus` |

Multiple conditions use `And`:

```java
// WHERE warehouse = ? AND status = ?
List<WarehouseQueueEntry> findByKeyWarehouseAndKeyStatus(String warehouse, String status);
```

### Cassandra query constraints

Unlike JPA, Cassandra restricts what you can query:
- You must include the full partition key in every query
- Clustering columns can be filtered only in order (left to right)
- Filtering on non-key columns requires `ALLOW FILTERING`, which is a full partition scan - avoid it

### Built-in repository methods

`CassandraRepository` provides standard CRUD for free:

```java
repository.save(entity);
repository.findById(key);
repository.deleteById(key);
repository.findAll();           // use with caution - full table scan
```
