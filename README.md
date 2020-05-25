# saga_pattern_poc
Simple POC to implement Saga pattern to solve distributed transactions in microservice architecture.
Uses RabbitMQ to communicate between services. All the changing events are logged in a MongoDB database via Eventstore service. The core services are using MySQL (same database for all of them).

* Java 11
* Springboot
* Hibernate
* RabbitMQ
* MySQL
* MongoDB

# Trip Service (Orchestrator)
This service has the purpose to orchestrate the core services. First of all, it validates all the input data. Then, it starts trying to save in the relational database. If something goes wrong, it calls the compensating transactions that needs to be done at the moment. By the end, in the happy flow, it updates the event log status to finished.
http://localhost:8080/trip/swagger-ui.html

# Core Services
* Airline Service: To fail the validation, just use the code 34 in the flightId field.
* Car Service: To fail the validation, just use the code 34 in the carId field.
* Hotel Service: To fail the validation, just use the code 34 in the hotelId field.
* Payment Service: To fail the validation, just use the code 34 in the airlineReservationId field.

* To fail the whole transaction, just use the code 34 in the clientId of the PaymentInput field.

# Eventstore
This service listens to the changing events and saves in a MongoDB. It also has the responsability of retrieving the last state of some entity when it needs to be undone.
