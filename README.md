# Sales Tracker API

API for Tracking Sales and Payments

## Prerequisites

- Maven (https://maven.apache.org/download.cgi)
- Java OpenJDK 22 (https://jdk.java.net/22/)
- Docker desktop v4.33.1 (https://www.docker.com/products/docker-desktop/)

## Useful commands

Build and test:
-   `./mvnw clean package` builds the application jar file for execution
-   `.\mvnw test` runs unit tests
-   `.\mvnw test jacoco:report` runs unit tests with code coverage report
    - The code coverage report will be generated in the target/site/jacoco directory
    - Open target/site/jacoco/index.html in a web browser to view the report.
Run locally:
-   `docker compose up` spins up a local Docker instance 
    - A PostgreSQL database will be available on localhost:5432
-   `./mvnw spring-boot:run -D"spring-boot.run.profiles=dev"` runs the application jar with dev configurations for local testing
    - The application will run on http://localhost:8080
    - The log files can be found in the target folder of the project

## Postman
Postman collection can be found here: https://www.postman.com/reuben-raj90/workspace/sales-tracker
