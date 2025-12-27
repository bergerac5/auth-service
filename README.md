Auth Service – Online Voting Microservices
==========================================
Overview
--------
The Auth Service is responsible for authentication and authorization in the Online Voting Microservice Architecture.
It handles user login, validates credentials, and issues JWT tokens used to secure communication across all microservices via the API Gateway.
This service is a standalone Spring Boot microservice, registered with Eureka Service Discovery.

🧱 Responsibilities
********************
1. User authentication (login)
2. JWT token generation
3. Role assignment (ADMIN, VOTER)
4. Central identity provider for the system

🛠️ Tech Stack
--------------
- Java 21
- Spring Boot 3.1.8
- Spring Security
- Spring Cloud Netflix Eureka Client
- JWT (JJWT)
- Maven

📂 Project Structure
---------------------
auth-service/
├── src/main/java/com/online/voting/auth
│   ├── controller
│   ├── service
│   ├── security
│   ├── model
│   └── dto
├── src/main/resources
│   └── application.yml
├── pom.xml
└── README.md

🚀 Running the Service
-----------------------
Prerequisites
*************
- Java 21
- Maven
- Eureka Server running on http://localhost:8761

Run locally
***********
mvn spring-boot:run

🧩 Integration
==================

- This service integrates with:

- API Gateway → JWT validation

- Eureka Server → Service discovery

- All business services → Identity & roles
