# Managing Cassandra

---

## cassandra.yaml

`cassandra.yaml` is the main configuration file for a Cassandra node. On a standard installation it lives at `/etc/cassandra/cassandra.yaml`. When running in Docker it can be inspected with:

```bash
docker exec -it cassandra cat /etc/cassandra/cassandra.yaml
```

It's a long file, and most of it won't need to be touched. The settings worth understanding:

**Cluster identity:**

```yaml
cluster_name: 'My Cluster'
```

Every node in a cluster must have the same `cluster_name`. If they don't match, a node will refuse to join. Change this only at setup time.

**Network:**

```yaml
listen_address: localhost
rpc_address: localhost
```

`listen_address` is the address other Cassandra nodes use to reach this one (internal cluster traffic). `rpc_address` is the address clients connect to - the application and cqlsh. In a single-node local setup both are `localhost`. In a multi-node deployment these would be the node's actual IP address.

**Seed nodes:**

```yaml
seed_provider:
  - class_name: org.apache.cassandra.locator.SimpleSeedProvider
    parameters:
      - seeds: "127.0.0.1"
```

Seed nodes are how a new node discovers the cluster when it starts up. It contacts the seeds, gets the cluster topology, and joins. In production, two or three stable node IPs are specified here. In a single-node local setup, the node seeds itself.

**Authentication:**

```yaml
authenticator: AllowAllAuthenticator   # default - no auth
authorizer: AllowAllAuthorizer         # default - no authorization

# Change to these to enable auth
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer
```

These are the two lines that enable auth, covered in more detail in the Security section below. A restart is required after changing them.

**Replication and compaction are not configured here** - replication is set per-keyspace in `CREATE KEYSPACE`, and compaction strategy is set per-table in `CREATE TABLE` or `ALTER TABLE`. `cassandra.yaml` controls the node, not the data model.

**Data and commit log directories:**

```yaml
data_file_directories:
  - /var/lib/cassandra/data

commitlog_directory: /var/lib/cassandra/commitlog
```

These are where Cassandra stores data on disk. In Docker, these paths are inside the container - removing the container means losing the data. To persist data across container restarts, mount these directories as Docker volumes.

The goal with `cassandra.yaml` is not memorization - it's knowing where to find it, which settings matter for auth and networking, and that the rest has sensible defaults that should be left alone until there's a specific reason to change them.

---

## Security

By default Cassandra runs with no authentication and no authorization - any client that can reach port 9042 can do anything. This is acceptable on a local dev machine, but any deployed instance - cloud, on-premise, or shared server - should have auth enabled before it touches real data.

**Enabling authentication** - edit `cassandra.yaml` and restart:

```yaml
authenticator: PasswordAuthenticator
authorizer: CassandraAuthorizer
```

After restart, connect with the default superuser credentials:

```bash
cqlsh -u cassandra -p cassandra
```

Change the default password immediately:

```sql
ALTER ROLE cassandra WITH PASSWORD = 'something_stronger';
```

**Creating roles and granting permissions:**

```sql
-- Create a login role
CREATE ROLE etl_user WITH PASSWORD = 'etl_pass' AND LOGIN = true;

-- Create a non-login role used as a permission group, then assign it
CREATE ROLE read_only;
GRANT read_only TO etl_user;
```

Cassandra's permission model has six distinct permissions:

| Permission | What it allows |
|---|---|
| `SELECT` | Read rows from a table |
| `SELECT_MASKED`/`UNMASK` | Determines whether a user can read masked data |
| `SELECT` | Read rows from a table |
| `MODIFY` | INSERT, UPDATE, DELETE, and TRUNCATE |
| `CREATE` | Create keyspaces, tables, roles, indexes, functions |
| `ALTER` | Alter keyspaces, tables, and roles |
| `DROP` | Drop keyspaces, tables, roles, indexes, functions |
| `AUTHORIZE` | Grant or revoke permissions on a resource |

Permissions can be granted at three levels - a specific table, a whole keyspace, or all keyspaces:

```sql
-- Scoped to a single table
GRANT SELECT ON TABLE ecommerce_analytics.invoices_by_customer TO etl_user;

-- Scoped to an entire keyspace
GRANT MODIFY ON KEYSPACE ecommerce_analytics TO etl_user;

-- All permissions on a resource
GRANT ALL ON KEYSPACE ecommerce_analytics TO etl_user;

-- Revoke a permission
REVOKE MODIFY ON TABLE ecommerce_analytics.invoices_by_customer FROM etl_user;

-- Inspect what a role can do
LIST ALL PERMISSIONS OF etl_user;

-- Superuser - bypasses all permission checks
CREATE ROLE admin WITH SUPERUSER = true AND LOGIN = true AND PASSWORD = 'admin_pass';
```

In practice, roles should be designed around job function: a reporting role gets `SELECT` on the keyspace, an ETL service role gets `MODIFY` on the specific tables it writes to, and only a DBA role gets `CREATE`, `ALTER`, and `DROP`. `AUTHORIZE` is the most sensitive permission - a role that holds it can grant permissions to others, so it should be treated with the same caution as superuser access.

---

## Backups

Snapshots in Cassandra provide a fast, point-in-time copy of your data, making them a simple and reliable way to create a restore point before changes or as part of a backup strategy. Because snapshots operate at the storage level, they are lightweight and quick to create. In practice, snapshots are managed using Cassandra’s operational tool, `nodetool`, which allows you to create, list, and clear snapshots directly on each node.

**Snapshots** - the primary backup mechanism:

```bash
# Take a snapshot of all keyspaces on this node
nodetool snapshot

# Snapshot a specific keyspace
nodetool snapshot ecommerce_analytics

# Snapshot a specific table
nodetool snapshot ecommerce_analytics -t invoices_by_customer
```

A snapshot creates hard links to the current SSTable files in a `snapshots/` subdirectory under each table's data directory. Hard links are instant and take no extra disk space until the live data diverges from the snapshot. The snapshot captures the state of the data at the moment it was taken.

**Listing and clearing snapshots:**

```bash
nodetool listsnapshots

nodetool clearsnapshot --all
nodetool clearsnapshot ecommerce_analytics
```

**Incremental backups** - Cassandra can be configured to automatically copy new SSTable files to a `backups/` directory as they are flushed from memory:

```yaml
# cassandra.yaml
incremental_backups: true
```

With this enabled, a running stream of new SSTable files accumulates in `backups/` representing changes since the last snapshot. A base snapshot combined with incremental files can restore data to any point in time.

**The distributed reality:** snapshots are per-node. `nodetool snapshot` runs on the node you're connected to. In a multi-node cluster it must be run on every node, with the files collected from each one. There is no built-in centralized backup mechanism - most teams script this with a cron job that triggers a snapshot on each node and uploads the files to object storage.

**Restoration** is also per-node. Copy the SSTable files back into the table's data directory, then run:

```bash
nodetool refresh <keyspace> <table>
```

This tells Cassandra to pick up the new files without a restart. For a full cluster restore, this runs on every node in token order.

The operational overhead of Cassandra backup and restore is one reason many teams move to managed services like DataStax Astra or AWS Keyspaces. Understanding the snapshot mechanism and `nodetool` commands is important for working with self-managed cluster.

---

## Logging

When running in Docker, the quickest way to see what Cassandra is doing is:

```bash
docker logs -f cassandra
```

This streams all output from the container - startup events, errors, schema changes, and warnings. For deeper inspection, `docker exec` can be used to run commands inside the container and access or grep the log files directly. When a specific problem needs more detail, Cassandra supports more verbose logging levels - `DEBUG` and `TRACE` - that surface compaction activity, memtable flushes, and query-level detail.

---

## Monitoring

The primary tool for checking on a running Cassandra node is `nodetool`. It works by connecting to the node's JMX interface - JMX (Java Management Extensions) is a standard Java mechanism for exposing runtime metrics and management operations from a running JVM process. Since Cassandra runs on the JVM, it publishes metrics like read/write latency, thread pool saturation, and compaction state through JMX. `nodetool` is a command-line client that reads those metrics and formats them. Any tool that speaks JMX - Prometheus with the JMX Exporter, JConsole - can pull the same data directly.

**Cluster health:**

```bash
nodetool status
```

Output looks like:

```
Datacenter: datacenter1
=======================
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address    Load       Tokens  Owns    Host ID   Rack
UN  127.0.0.1  95.1 KiB   16      100.0%  abc123    rack1
```

`UN` = Up/Normal. `DN` = Down/Normal - a node that should be there but isn't responding. `UJ` = Up/Joining - a node in the process of joining the cluster. Anything other than `UN` warrants investigation.

**Thread pool saturation:**

```bash
nodetool tpstats
```

Cassandra uses internal thread pools for different types of work - reads, writes, compaction, gossip. `tpstats` shows how many tasks are active, pending, and dropped for each pool. A growing `Pending` count on `ReadStage` or `MutationStage` means the node can't keep up with the load. Dropped counts correspond to the dropped messages visible in the logs.

**Per-table statistics:**

```bash
# tablestats is the current command; cfstats is the legacy alias that still works in most versions
nodetool tablestats ecommerce_analytics.invoices_by_customer
nodetool cfstats ecommerce_analytics.invoices_by_customer
```

Key numbers to look at: `Read Latency` and `Write Latency` (in microseconds), `Live SS Table Count` (high counts indicate compaction isn't keeping up), and `Number of Keys (estimate)`. These are the primary signals that a specific table has a performance problem.

In production, Cassandra's JMX metrics are typically exposed to a time-series monitoring system. The most common setup uses the Prometheus JMX Exporter running as a Java agent alongside Cassandra, scraping metrics and exposing them as a Prometheus endpoint, with Grafana dashboards on top. For day-to-day work with a local or small cluster, `nodetool status`, `tpstats`, and `tablestats` cover the practical toolkit.
