## What do I need to do to decompose our monolith into different services

- we want 2 services: order-service, warehouse-service
  - each will need their own application.yml files for their respective configuration
  - each will need their own pom.xml for their respective dependencies
  - we'll need to manage two projects within intellij (can import as additional project modules)
  - our two services will have their own h2 databases
    - order-service: order + customer data
    - warehouse-service: warehouse data
    - we have to rethink our entities/repositories
      - warehouse-service contains a warehouse entity
      - order-service contains order and customer entity
    - we may have to introduce some dtos to represent objects we don't want persisted
      - if I'm requested data that is managed by another service, I may want a dto to represent what I get back from the service
      - order-service can use a WarehouseSummary to represent data received from our warehouse-service
  - they will have to start on two different ports

- our services will need to communicate via HTTP
  - we'll use RestClient to help us prepare HTTP Requests and process the HTTP Response we get back