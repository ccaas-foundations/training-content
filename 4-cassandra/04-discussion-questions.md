1. Partition keys determine which node stores the data, and all rows with the same partition key are stored together. What happens to cluster performance if a partition key is poorly chosen - and what does a good partition key look like?

2. We covered that ALLOW FILTERING exists but is a red flag in production. Why does Cassandra require you to provide the partition key in a query at all - what would have to happen under the hood if it didn't?

3. We've said repeatedly that Cassandra is optimized for writes, not updates. Walk through what actually happens at the storage level when you issue an UPDATE - why does that make frequent updates a problem, and how does the append-oriented pattern avoid it?

4. Cassandra has no multi-table transactions, yet we've argued that a well-designed system can still be reliable. What makes an operation idempotent, and why does that property matter so much in an eventually consistent system?

5. Cassandra's consistency is tunable - you can choose between ONE, QUORUM, and ALL when reading and writing. What are you actually trading off when you move along that spectrum, and when would you accept weaker consistency?

6. You're designing a schema for a new feature and realize you need to support three different access patterns against the same data. Walk through how you would approach that - what questions do you ask first, and how does that translate into table design?

7. A colleague comes from a SQL background and proposes adding a secondary index to handle a query their table wasn't designed for. How would you explain the tradeoff, and what would you recommend instead?

8. Look at the three tables from the invoice demo - invoice_events, invoices_by_customer, and warehouse_queue. The same data appears in multiple places. Why is that intentional, and what would break if you tried to collapse them into a single table?

9. TTL, DDM, and UDTs are all ways of encoding logic or constraints directly into the schema rather than handling them in application code. What are the advantages of pushing that responsibility into the database - and are there cases where you'd want to keep it in the application instead?

10. How is a Cassandra materialized view different than what a SQL view is. What does that difference reveal about the fundamental design priorities of the two databases?

11. How does Cassandra delete data? Why would this process have to be handled differently than in a traditional database?