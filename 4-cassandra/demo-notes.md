### General Data Workflow

We have two invoices from the same customer. We want to place invoice 1. Then we want to assign, ship, deliver invoice 1. We want to then place invoice 2.


### 3 Query Based Tables
- invoice event table
- invoices by customer table
- warehouse queue table


### Table Data Workflow

1. place invoice 1
   - inserting invoice event to invoice event table (invoice id, placed at, status="PLACED", etc.)
   - inserting record to the invoices by customer table (cust-8821 + invoice info)

2. assign invoice 1
   - insert a record into the warehouse queue table (warehouse, status="ASSIGNED")
   - insert record to my invoice event table (invoice id, placed at, status="ASSIGNED", etc.)
  
   
3. ship invoice 1
   - insert record into invoice event table (invoice id, placed at, status="SHIPPED", etc.)
   - remove record from warehouse queue (invoice that was assigned is now shipped, so it can be removed from the warehouse queue)
   
4. delivery invoice 1 
   - insert record into invoice event table (status="DELIVERED")
   
5. place invoice 2
   - insert record into the invoice event table (status="PLACED")
   - inserting record to the invoices by customer table (cust-8821 + invoice info)

