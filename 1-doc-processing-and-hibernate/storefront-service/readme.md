## storefront-service 

- we want this application to be a producer and produce messages that carry order information to our artemis broker so that another application can consume these messages and process our orders
- we can create a controller that begins the workflow where new orders are received 
- an Order class can represent the data that is received and sent to our broker to be processed/fulfilled by another service
- we'll need a class to send messages to our broker