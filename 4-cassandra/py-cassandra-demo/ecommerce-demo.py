from cassandra.io.twistedreactor import TwistedConnection
from cassandra.cluster import Cluster
from uuid import uuid4
from datetime import datetime


if __name__ == "__main__":
    cluster = Cluster(['127.0.0.1'],port=9042,connection_class=TwistedConnection)
    session = cluster.connect("ecommerce_analytics")
    # we can specify the keyspace when we connect or explicitly after
    # session.set_keyspace("ecommerce_analytics")
    print("connected!")

    # start by inserting a new record in one of our tables 
    # this represents placing an order into invoice event table
    session.execute("""
                    INSERT INTO invoice_events (invoice_id, event_time, status, warehouse)
                    VALUES (%s, %s, %s, null)                
""", [uuid4(), datetime.now(), "PLACED"])
    
    cluster.shutdown()



