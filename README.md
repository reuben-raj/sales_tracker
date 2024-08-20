# Sales Tracker API

API for Tracking Sales and Payments

## Prerequisites

- Maven (https://maven.apache.org/download.cgi)
- Java OpenJDK 22 (https://jdk.java.net/22/)
- Docker desktop v4.33.1 (https://www.docker.com/products/docker-desktop/)

## Useful commands

Run and test locally:
-   `./mvnw clean package` builds the application jar file for execution
-   `./mvnw spring-boot:run -D"spring-boot.run.profiles=dev"` runs the application jar with dev configurations for local testing
    - The application will run on http://localhost:8080
-   `docker compose up` spins up a local Docker instance 
    - A PostgreSQL database will be available on localhost:5432

## Postman
Postman collection can be found here: https://www.postman.com/reuben-raj90/workspace/sales-tracker
