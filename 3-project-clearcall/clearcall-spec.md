# ClearCall - Conversation Analytics Platform
## Project Specification

Project presentations will take place **April 21st 2026**.

---

## Overview

ClearCall is the analytics layer of a contact center platform. Where RouteIQ handles the operational side - receiving and routing live calls - ClearCall answers what happened after: how long did calls take, which agents handled the most volume, how many callers were resolved by the IVR without ever reaching an agent.

The system has four components you will build in sequence, each one building on the last:

- A Python transcript generator that produces synthetic IVR interaction records as JSON files
- A Python ETL pipeline that reads, validates, transforms, and loads those records into Cassandra
- A Cassandra schema designed around query patterns rather than entity relationships
- A Spring Boot reporting service that queries Cassandra and exposes analytics over REST

The transcript JSON schema defined in this document is the data contract for the entire project. All four components must agree on it before any code is written.


---

## Team Structure

You are working on a team with other members of the cohort. Your team shares a single repository. Everyone is expected to contribute their own commits - this means using the git CLI and resolving merge conflicts when they arise.

Before writing any code, align with your team on how you want to manage the repository:

- Will you use feature branches, member branches, or work directly on main?
- How will you coordinate on shared files like the schema and data contract?
- How will you handle the ETL and reporting service if different members own each?

Document your work clearly - either with docstrings in code or in a markdown file in the repo. Make authorship explicit, especially where paired programming was used.

**Project Teams**

- Team 1: Carl, Suny
- Team 2: Zach, Sanjana
- Team 3: Joshua, Daniel
- Team 4: Tristan, Muhammad
- Team 5: David, Jathan
- Team 6: Anurag, Drin, Brian

---

## Provided Materials

The following are provided at the start of the project:

- **Transcript JSON schema** - defined in this document; agree on it before writing code
- **callCategory and agentId values** - defined in the Data Contract section below

---

## Component 1 - Python Transcript Generator

Write a Python script that produces synthetic JSON transcript files. Each file represents one completed IVR interaction. These files are the input to the ETL pipeline and the source of all data in the system.

### Output Format

One JSON file per call:

```json
{
  "callId": "a3f9c2e1-7b44-4d18-9f2c-1a2b3c4d5e6f",
  "startTime": "2026-03-17T09:42:00Z",
  "endTime": "2026-03-17T09:44:35Z",
  "callCategory": "BILLING",
  "ivrContained": true,
  "escalatedToAgent": false,
  "agentId": null,
  "ivrPath": ["WELCOME", "BILLING", "ACCOUNT_LOOKUP", "RESOLVED"]
}
```

When `ivrContained` is false, `agentId` should be populated with one of the agent IDs from the data contract below - this is how ClearCall data links back to RouteIQ.

### Script Requirements

- Generate a configurable number of transcripts (default 50) using `argparse`
- Randomize call categories in proportion: 40% BILLING, 35% TECHNICAL, 15% SALES, 10% GENERAL
- Randomize IVR containment: BILLING calls contained ~60% of the time, TECHNICAL and SALES always escalated
- Randomize `startTime` and `endTime` within realistic bounds per category - duration is not stored here, it is derived by the ETL
- Write all files to a configurable output directory
- Use a `TranscriptGenerator` class and a `CallTranscript` dataclass

---

## Component 2 - Python ETL Pipeline

Write an ETL pipeline that reads the transcript files, validates and transforms each record, and loads the results into Cassandra.

### Project Structure

```
etl/
  main.py           entry point, orchestrates the pipeline
  parser.py         TranscriptParser class
  transformer.py    TranscriptTransformer class
  loader.py         CassandraLoader class
  model.py          CallRecord dataclass
```

### Component Responsibilities

- **TranscriptParser** - reads JSON files from the input directory, validates required fields, logs and skips malformed records. A single bad file must not stop the pipeline.
- **TranscriptTransformer** - converts a raw transcript dict into a `CallRecord` dataclass. Computes `durationSeconds` (the difference between `endTime` and `startTime`) and `callDate` (the date portion of `startTime`, used as the Cassandra partition key). Everything else maps directly from the source transcript.
- **CassandraLoader** - connects to Cassandra and inserts `CallRecord` objects into all three tables. Use the `cassandra-driver` Python package.

### CallRecord Dataclass

```python
@dataclass
class CallRecord:
    call_id: str
    call_date: date
    start_time: datetime
    end_time: datetime
    call_category: str
    ivr_contained: bool
    escalated_to_agent: bool
    agent_id: str | None
    duration_seconds: int
    ivr_path: list[str]
```

### Running the Pipeline

```bash
python main.py --input ./transcripts --host localhost --keyspace clearcall
```

---

## Component 3 - Cassandra Schema

Design three tables - one per query pattern. This is the core Cassandra lesson: there are no joins, so you design one table per access pattern and duplicate data across tables. Each table below serves a specific query the analytics service needs to answer.

### Keyspace

```sql
CREATE KEYSPACE clearcall
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
```

### calls_by_date - all calls on a given day, time-of-day analysis

```sql
CREATE TABLE clearcall.calls_by_date (
    call_date     date,
    start_time    timestamp,
    call_id       uuid,
    call_category text,
    ivr_contained boolean,
    escalated     boolean,
    agent_id      text,
    duration_sec  int,
    PRIMARY KEY (call_date, start_time, call_id)
) WITH CLUSTERING ORDER BY (start_time DESC);
```

### calls_by_agent - calls handled per agent, agent handle time

```sql
CREATE TABLE clearcall.calls_by_agent (
    agent_id      text,
    start_time    timestamp,
    call_id       uuid,
    call_category text,
    duration_sec  int,
    PRIMARY KEY (agent_id, start_time, call_id)
) WITH CLUSTERING ORDER BY (start_time DESC);
```

### calls_by_category - category breakdown, category trends over time

```sql
CREATE TABLE clearcall.calls_by_category (
    call_category text,
    call_date     date,
    start_time    timestamp,
    call_id       uuid,
    ivr_contained boolean,
    duration_sec  int,
    PRIMARY KEY (call_category, call_date, start_time, call_id)
) WITH CLUSTERING ORDER BY (call_date DESC, start_time DESC);
```

The ETL inserts each `CallRecord` into all three tables. This duplication is intentional and necessary - not a mistake.

We'll be running Cassandra with Docker.

---

## Component 4 - Java Reporting Service

A Spring Boot service that queries Cassandra and exposes analytics via REST. The primary objective is Spring Data Cassandra - you will see the same repository pattern from RouteIQ applied to a NoSQL store. `CassandraRepository` works the same way as `JpaRepository`: derived query methods, `@Table` annotations, `findBy` methods generated at runtime.

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
```

### application.yml

```yaml
spring:
  application:
    name: clearcall-reporting
  cassandra:
    contact-points: localhost
    port: 9042
    keyspace-name: clearcall
    local-datacenter: datacenter1
server:
  port: 8090
```

### Entity Mapping

Map each Cassandra table to a Spring Data Cassandra entity using `@Table`, `@PrimaryKeyColumn`, and `@Column`. The partition key field uses `PrimaryKeyType.PARTITIONED`; clustering columns use `PrimaryKeyType.CLUSTERED`. Create entities for each of the three tables: `CallByDate`, `CallByAgent`, `CallByCategory`.

Extend `CassandraRepository` for each entity:

```java
public interface CallByAgentRepository
        extends CassandraRepository<CallByAgent, CallByAgentKey> {
    List<CallByAgent> findByAgentId(String agentId);
}
```

### Analytics Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/analytics/agents/{agentId}/calls` | All calls handled by an agent |
| GET | `/analytics/agents/{agentId}/handle-time` | Average handle time for an agent |
| GET | `/analytics/categories` | Call count and average duration by category |
| GET | `/analytics/calls?date={date}` | All calls on a given date |

Aggregations like average handle time are computed in Java - Cassandra does not aggregate like SQL. This is an explicit contrast to the Spring Data JPA work in RouteIQ.

```java
public double getAverageHandleTime(String agentId) {
    List<CallByAgent> calls = callByAgentRepository.findByAgentId(agentId);
    return calls.stream()
        .mapToInt(CallByAgent::getDurationSec)
        .average()
        .orElse(0.0);
}
```

---

## Data Contract

These values must be consistent across all components. Agree on them before writing any code.

**callCategory values:** `BILLING`, `TECHNICAL`, `SALES`, `GENERAL`

**agentId values** - use these fixed IDs in the transcript generator so escalated calls reference real agents from the RouteIQ seed data:

```
agent-1, agent-2, agent-3, agent-4, agent-5
```

**File locations:** the generator writes to `./transcripts/` and the ETL reads from the same directory. Both accept this path as a configurable argument.

---

## Acceptance Criteria

### Python ETL
- Transcript generator produces valid JSON files with all required fields
- ETL parses all valid transcripts and skips malformed ones with a logged warning
- All records are inserted into all three Cassandra tables
- Pipeline runs end-to-end: `python main.py --input ./transcripts`

### Cassandra
- Keyspace and all three tables created with correct partition and clustering keys
- Data queryable from `cqlsh` after ETL run
- `SELECT * FROM calls_by_agent WHERE agent_id = 'agent-1';` returns results

### Java Reporting Service
- Service starts and connects to Cassandra successfully
- `GET /analytics/agents/{agentId}/calls` returns a list of calls for that agent
- `GET /analytics/agents/{agentId}/handle-time` returns a numeric average
- `GET /analytics/categories` returns breakdown across all four categories
- `GET /analytics/calls?date=2026-03-17` returns calls from that date

---

## Stretch Goals

1. Add `GET /analytics/ivr/containment` returning IVR containment rate overall and broken down by category
2. Add `GET /analytics/calls/peak-hours` returning call volume by hour of day - requires deriving `hourOfDay` from `startTime` in the transformer, storing it in `calls_by_date`, and grouping results in Java
3. Add Cassandra materialized views for secondary access patterns and query them from the reporting service
4. Add error handling in the Java service for when Cassandra is unavailable - return HTTP 503 rather than a stack trace
