# Python + Cassandra Setup Guide

## Prerequisites

- Docker Desktop installed and running
- Cassandra running in Docker on port 9042

---

## Windows Setup

### Step 1 — Install Python 3.11

Python 3.13 has a known compatibility issue with cassandra-driver on Windows. You need Python 3.11.

1. Open the **Microsoft Store**
2. Search for **Python 3.11**
3. Install it

You can also do this using Windows Package Manager, by running in your command prompt:

```cmd
winget install Python.Python.3.11
```

Verify it's available:
```cmd
py --list
```

You should see `-3.11` in the list.

### Step 2 — Create a virtual environment

> **Important: Use Command Prompt, not Git Bash**, for all Python/venv work on Windows.

```cmd
py -3.11 -m venv venv311
venv311\Scripts\activate
```

Your prompt should now show `(venv311)`.

### Step 3 — Install dependencies

```cmd
pip install cassandra-driver twisted
```

### Step 4 — Connect to Cassandra

```python
from cassandra.io.twistedreactor import TwistedConnection
from cassandra.cluster import Cluster

cluster = Cluster(['127.0.0.1'], port=9042, connection_class=TwistedConnection)
session = cluster.connect()
print("Connected!")
```

---

## Mac / Linux Setup

### Step 1 — Create a virtual environment

```bash
python3 -m venv venv
source venv/bin/activate
```

### Step 2 — Install dependencies

```bash
pip install cassandra-driver
```

### Step 3 — Connect to Cassandra

```python
from cassandra.cluster import Cluster

cluster = Cluster(['127.0.0.1'], port=9042)
session = cluster.connect()
print("Connected!")
```

If this times out, try explicitly using the asyncio reactor:

```python
from cassandra.io.asyncioreactor import AsyncioConnection
from cassandra.cluster import Cluster

cluster = Cluster(['127.0.0.1'], port=9042, connection_class=AsyncioConnection)
session = cluster.connect()
print("Connected!")
```

---

## Troubleshooting

**`DependencyException: Unable to load a default connection class`**
You're on Python 3.12+ and the default asyncore event loop is gone. On Windows, follow the Windows setup above. On Mac/Linux, use `AsyncioConnection` as shown above.

**`NoHostAvailable: Unable to connect to any servers`**
Cassandra isn't running or isn't reachable. Check that your Docker container is up:
```cmd
docker ps
```
You should see a Cassandra container with port `0.0.0.0:9042->9042/tcp`.

**Venv not activating on Windows**
Make sure you're in Command Prompt, not Git Bash. Git Bash mangles Windows paths and breaks venv activation.

**`where python` shows system Python instead of venv**
The venv isn't activated. Run `venv311\Scripts\activate` in Command Prompt and check again.
