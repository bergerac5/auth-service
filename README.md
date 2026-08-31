# Auth Service

## Overview

The auth service is the identity and authorization component of the online-voting system. It handles user registration, login, JWT issuance, role-based access checks, and event-driven integration with the rest of the microservices.

This service is currently configured to run on Java 25 with Spring Boot 3.5.0 and Spring Cloud 2025.0.0.

## Responsibilities

- Register and manage users
- Authenticate users with username and password
- Issue JWT access tokens for downstream service access
- Enforce role-based access control via Spring Security
- Publish and consume Kafka events for voter and candidate lifecycle updates
- Expose admin-only user management endpoints
- Support candidate authorization flows for candidate-related operations

## Tech Stack

- Java 25
- Spring Boot 3.5.0
- Spring Security
- Spring Data JPA
- Spring Cloud Stream with Kafka
- PostgreSQL
- JWT via JJWT
- Maven
- Spring Boot Actuator

## Runtime Configuration

The service listens on:
- `http://localhost:8081`

The active application configuration is in `src/main/resources/application.yaml` and contains the database, Kafka, and actuator settings.

### Database

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vote_authdb
    username: "add yours"
    password: "add yours"
    driver-class-name: org.postgresql.Driver
```

### Kafka / Spring Cloud Stream

The service uses Kafka bindings for events like:
- `user-created`
- `user-updated`
- `user-deleted`
- `voter-creation-succeeded`
- `voter-creation-failed`
- `candidate-created`
- `candidate-updated`
- `candidate-creation-succeeded`
- `candidate-creation-failed`

Kafka broker configuration:

```yaml
spring:
  cloud:
    stream:
      kafka:
        binder:
          brokers: localhost:9092
          auto-create-topics: true
```

## Security Model

The service secures endpoints with Spring Security and a custom JWT filter.

### Public endpoints

These endpoints are intentionally open for registration and login activity:
- `/auth/register`
- `/auth/login`
- `/auth/health`
- `/api/kafka-test/**`
- `/actuator/**`

### Protected endpoints

- `/auth/update/**` requires `ADMIN`
- `/auth/delete/**` requires `ADMIN`
- `/auth/users/**` requires `ADMIN`
- `/auth/candidates/**` allows `ADMIN` and `CANDIDATE`
- All other requests require authentication

## API Endpoints

### Authentication endpoints

#### Register a user
- `POST /auth/register`

Request body example:

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "StrongPassword123!",
  "role": "USER"
}
```

#### Login
- `POST /auth/login`

Request body:

```json
{
  "username": "john",
  "password": "StrongPassword123!"
}
```

Returns the user information and JWT token.

#### Update user
- `PATCH /auth/update/{id}`

#### Delete user
- `DELETE /auth/delete/{id}`

#### List users
- `GET /auth/users`

#### Search a user by username
- `GET /auth/users/{username}`

### Candidate auth endpoints

- `POST /auth/candidates`
- `PATCH /auth/candidates/{userId}`

These endpoints are used to register or update candidate-related auth records.

### Kafka test controller

The project also includes a Kafka testing controller under:
- `/api/kafka-test/**`

This is useful for verifying that the event-driven pipeline is operating correctly during local development.

## Project Structure

```text
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/online/voting/auth_service/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   ├── util/
│   │   │   └── AuthServiceApplication.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── HELP.md
├── README.md
└── target/
```

## Prerequisites

Before running the service, make sure the following are available:

- JDK 25 installed and active
- Maven wrapper available in the module
- PostgreSQL running with a database named `vote_authdb`
- Kafka broker available at `localhost:9092`
- The `common-events` module built and available to the auth service dependency graph

## Run Locally

From the project root or inside the module:

```powershell
cd d:\spring boot\online-voting-system\auth-service
$env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.4.1'
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
.\mvnw clean test
.\mvnw spring-boot:run
```

The service starts on:
- `http://localhost:8081`

## Notes

- The database credentials and secret values in the example YAML are local development values and should be externalized in production.
- The service is designed to operate as part of a larger microservice architecture and relies on Kafka event streams and JWT-based security to integrate with the rest of the voting platform.
- The current implementation includes both direct user management endpoints and event-driven candidate/voter synchronization.

## Author / Project

This service is part of the Online Voting Microservice System.
