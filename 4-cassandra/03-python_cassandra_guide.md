# Python + Cassandra Quick Reference

Once your cassandra driver is able to access your database, you're able to execute statement just as you're able to using cqlsh.

If your keyspace doesn't exist yet, connect without one and create it first:

```python
session = cluster.connect()
session.execute("CREATE KEYSPACE ...")
session.set_keyspace('my_keyspace')    # equivalent of USE in cqlsh
```

---

## Running Queries

### Simple execute

```python
session.execute("SELECT * FROM invoices_by_customer WHERE customer_id = 'cust-001'")
```

Fine for one-off scripts. Never use this with dynamic values — use parameterized queries instead.

There are two ways to pass parameters — the placeholder syntax differs between them.

### Inline parameterized queries — use `%s`

```python
session.execute("""
    SELECT * FROM invoices_by_customer WHERE customer_id = %s
""", ['cust-001'])
```

> **Never use f-strings or string concatenation to build CQL with user data.** Always use `%s` parameters.


`%s` is the placeholder regardless of type. The driver maps Python types to Cassandra types automatically:

| Python | Cassandra |
|---|---|
| `str` | `TEXT` |
| `int` | `INT` / `BIGINT` |
| `float` | `FLOAT` / `DOUBLE` |
| `Decimal` | `DECIMAL` |
| `datetime` | `TIMESTAMP` |
| `uuid.UUID` | `UUID` |
| `list` | `LIST<>` |
| `dict` | `MAP<>` |
| `set` | `SET<>` |


### Prepared statements — use `?`

Prepared statements are parsed by Cassandra once and reused. These are used in production code for any query that runs repeatedly to execute them faster.

```python
stmt = session.prepare("""
    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
    VALUES (?, ?, ?, ?)
""")

# Cassandra parses the query once — execute it as many times as needed
session.execute(stmt, [invoice_id, event_time, 'PLACED', None])
session.execute(stmt, [invoice_id, event_time, 'SHIPPED', 'CHI-1'])
```

The placeholder is `?` instead of `%s` because prepared statements go through a different API path — `session.prepare()` sends the query to Cassandra ahead of time, and `?` is the CQL-native parameter marker.

---

## Reading Results

`session.execute()` returns a result set you can iterate directly. Columns are accessed as attributes using the column name:

```python
rows = session.execute("SELECT * FROM invoices_by_customer WHERE customer_id = %s", ['cust-001'])

for row in rows:
    print(row.customer_id, row.total, row.item_skus)
```

The result set is **paged** — the driver fetches rows from Cassandra in chunks (5000 rows per page by default). As you iterate, it fetches the next page automatically when needed. For small result sets this makes no difference. For large ones, it means you never load more into memory than one page at a time.

If you wrap the result in `list()`, you force all pages to be fetched immediately and held in memory:

```python
rows = list(session.execute(...))  # all rows in memory before you do anything
```

Only do this if you actually need random access or need to know the length upfront. For most reads, iterating directly is fine.

To get a single row:

```python
row = session.execute(...).one()   # returns None if no results
```

---

## Inserts

```python
from uuid import uuid4
from datetime import datetime, timezone
from decimal import Decimal

session.execute("""
    INSERT INTO invoices_by_customer (customer_id, placed_at, invoice_id, total, item_skus)
    VALUES (%s, %s, %s, %s, %s)
""", [
    'cust-001',
    datetime.now(timezone.utc),
    uuid4(),
    Decimal('209.92'),
    ['SHOE-001', 'SOCK-004']
])
```

**There is no INSERT OR UPDATE in Cassandra — every INSERT is an upsert.** If a row with the same primary key already exists, it will be overwritten silently.

---

## Deletes

You must provide the full primary key. You cannot delete by non-key columns.

```python
session.execute("""
    DELETE FROM warehouse_queue
    WHERE warehouse = %s
      AND status = %s
      AND invoice_id = %s
""", ['CHI-1', 'ASSIGNED', invoice_id])
```

---

## Working with UUIDs

```python
import uuid

# Generate a new random UUID
new_id = uuid.uuid4()

# Use a specific UUID (e.g. from a string)
existing_id = uuid.UUID('11111111-1111-1111-1111-111111111111')

# Pass directly as a parameter — the driver handles the rest
session.execute("SELECT * FROM invoice_events WHERE invoice_id = %s", [existing_id])
```

---

## Working with Timestamps

Always use timezone-aware datetimes. Cassandra stores timestamps in UTC.

```python
from datetime import datetime, timezone

now = datetime.now(timezone.utc)

session.execute("""
    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
    VALUES (%s, %s, %s, null)
""", [invoice_id, now, 'PLACED'])
```

---

## Working with Collections

### LIST

```python
# Insert
session.execute("INSERT INTO t (id, tags) VALUES (%s, %s)", [id, ['a', 'b', 'c']])

# Read — comes back as a Python list
row = session.execute("SELECT tags FROM t WHERE id = %s", [id]).one()
print(row.tags)  # ['a', 'b', 'c']
```

### MAP

```python
# Insert
session.execute("INSERT INTO t (id, meta) VALUES (%s, %s)", [id, {'color': 'red', 'size': 'L'}])

# Read — comes back as a Python dict
row = session.execute("SELECT meta FROM t WHERE id = %s", [id]).one()
print(row.meta['color'])  # 'red'
```

---

## NULL values

Pass `None` for null:

```python
session.execute("""
    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
    VALUES (%s, %s, %s, %s)
""", [invoice_id, now, 'PLACED', None])
```

---

## Common Mistakes

**Querying without a partition key**

```python
# BAD — full table scan, Cassandra will warn or reject
session.execute("SELECT * FROM invoices_by_customer")

# GOOD — always include the partition key
session.execute("SELECT * FROM invoices_by_customer WHERE customer_id = %s", ['cust-001'])
```

**Using float for currency**

```python
# BAD — floating point precision issues
total = 209.92  # stored as 209.91999999...

# GOOD
from decimal import Decimal
total = Decimal('209.92')  # pass as string to avoid float conversion
```

**Forgetting to shut down the cluster**

```python
# Always close the connection when done
cluster.shutdown()
```

---

## Quick Example

```python
from cassandra.cluster import Cluster
from decimal import Decimal
from datetime import datetime, timezone
from uuid import uuid4

cluster = Cluster(['127.0.0.1'])
session = cluster.connect('ecommerce_analytics')

invoice_id = uuid4()
now = datetime.now(timezone.utc)

# Insert
session.execute("""
    INSERT INTO invoices_by_customer (customer_id, placed_at, invoice_id, total, item_skus)
    VALUES (%s, %s, %s, %s, %s)
""", ['cust-001', now, invoice_id, Decimal('99.99'), ['SKU-A']])

# Read
rows = session.execute("""
    SELECT * FROM invoices_by_customer WHERE customer_id = %s
""", ['cust-001'])

for row in rows:
    print(row.invoice_id, row.total, row.item_skus)

cluster.shutdown()
```
