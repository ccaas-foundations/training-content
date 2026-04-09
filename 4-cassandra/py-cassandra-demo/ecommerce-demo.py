from cassandra.io.twistedreactor import TwistedConnection
from cassandra.cluster import Cluster
from uuid import uuid4
from datetime import datetime
from decimal import Decimal

class Invoice:
    def __init__(self, customer_id: str, total: Decimal, item_skus: list[str]):
        self.invoice_id = uuid4()
        self.customer_id = customer_id
        self.placed_at = datetime.now()
        self.warehouse = None
        self.status = "PLACED"
        self.total = total
        self.item_skus = item_skus

def place(session, invoice):
    session.execute("""
                    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
                    VALUES (%s, %s, %s, null)                
""", [invoice.invoice_id, invoice.placed_at, invoice.status])
    
    session.execute("""
                    INSERT INTO invoices_by_customer (customer_id, placed_at, invoice_id, total, item_skus)
                    VALUES (%s, %s, %s, %s, %s)
""", [invoice.customer_id, invoice.placed_at, invoice.invoice_id, invoice.total, invoice.item_skus])


def assign(session, invoice, warehouse):
    # add a new invoice event to represent the assignment to a warehouse
    invoice.warehouse = warehouse
    invoice.status = "ASSIGNED"
    session.execute("""
                    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
                    VALUES (%s, %s, %s, %s)                
""", [invoice.invoice_id, datetime.now(), invoice.status, invoice.warehouse])
    
    # add a new record to our warehouse queue
    session.execute("""
                    INSERT INTO warehouse_queue (warehouse, status, invoice_id, customer_id, placed_at, total)
                    VALUES (%s, %s, %s, %s, %s, %s)
""", [invoice.warehouse, invoice.status, invoice.invoice_id, invoice.customer_id, invoice.placed_at, invoice.total])

def ship(session, invoice):
    invoice.status = "SHIPPED"

    # add a new invoice event to represent the order shipping
    session.execute("""
                    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
                    VALUES (%s, %s, %s, %s)                
""", [invoice.invoice_id, datetime.now(), invoice.status, invoice.warehouse])

    # remove the invoice from the warehouse queue
    session.execute("""
                    DELETE FROM warehouse_queue
                    WHERE warehouse = %s AND status = 'ASSIGNED' AND invoice_id = %s 
""",[invoice.warehouse, invoice.invoice_id])
    

def deliver(session, invoice):
    # insert a new invoice event for the delivery
    invoice.status = "DELIVERED"

    # add a new invoice event to represent the order shipping
    session.execute("""
                    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
                    VALUES (%s, %s, %s, %s)                
""", [invoice.invoice_id, datetime.now(), invoice.status, invoice.warehouse])




if __name__ == "__main__":
    cluster = Cluster(['127.0.0.1'],port=9042,connection_class=TwistedConnection)
    session = cluster.connect("ecommerce_analytics")
    # we can specify the keyspace when we connect or explicitly after
    # session.set_keyspace("ecommerce_analytics")
    print("connected!")

    session.execute("TRUNCATE TABLE invoice_events")   
    session.execute("TRUNCATE TABLE invoices_by_customer") 
    session.execute("TRUNCATE TABLE warehouse_queue")     

    invoice1 = Invoice("cust-8821",Decimal(209.92),["SHOE-001", "SOCK-004"])

    # start by inserting a new record in one of our tables 
    # this represents placing an order into invoice event table
    place(session, invoice1)

    assign(session, invoice1, "CHI-001")

    ship(session, invoice1)

    deliver(session, invoice1)

    invoice2 = Invoice("cust-8821",Decimal(134.99), ["JKTS-002"])

    place(session, invoice2)
    
    assign(session, invoice2, "CHI-001")

    rows = session.execute("""
                    SELECT JSON * FROM invoice_events
""")
    
    customer_rows = session.execute("""
                    SELECT JSON * FROM invoices_by_customer WHERE customer_id = %s
""",[invoice1.customer_id])
    
    warehouse_rows = session.execute("""
                    SELECT JSON * FROM warehouse_queue WHERE warehouse = %s AND status = 'ASSIGNED'
""",[invoice1.warehouse])
    
    [print(row) for row in rows]
    
    [print(row) for row in warehouse_rows]

    [print(row) for row in customer_rows]

    # invoice id, event time, status and warehouse
    # for row in rows:
    #     print(f"[INVOICE EVENT]: invoice_id={row.invoice_id}, event_time={row.event_time}, status={row.status}, warehouse={row.warehouse}")

    # for row in customer_rows:
    #     print(f"[INVOICES BY CUSTOMER]: customer_id={row.customer_id}, placed_at={row.placed_at}, invoice_id={row.invoice_id}, total={row.total}, item_skus={row.item_skus}")

    # for row in warehouse_rows:
    #     print(f"[WAREHOUSE QUEUE: warehouse={row.warehouse}, status={row.status}, invoice_id={row.invoice_id}, customer_id={row.customer_id}, placed_at={row.placed_at}, total={row.total}")
    
    cluster.shutdown()



