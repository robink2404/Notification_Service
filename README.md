# Notification Service

A Spring Boot notification service that supports user management, notification creation, Kafka-driven event processing, Redis caching, rate limiting, retry handling, and PostgreSQL persistence.

## Features

- REST API for user creation and notification submission
- Kafka-based asynchronous notification processing
- Notification retry with exponential backoff and dead-letter queue
- Redis caching for user lookups
- Redis token bucket rate limiting
- PostgreSQL persistence for user and notification data
- `.env` support for sensitive configuration
- Docker Compose support for Redis, Kafka, and Zookeeper

## Tech Stack

- Java 17
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring for Apache Kafka
- Spring Data Redis
- PostgreSQL / H2
- Redis
- Kafka + Zookeeper
- Docker Compose
- Lombok
- java-dotenv

## Setup

1. Create a `.env` file in the project root with the required secrets:

```env
DB_URL=jdbc:postgresql://localhost:5432/notification_db
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
```

2. Start infrastructure:

```bash
docker compose up -d zookeeper kafka redis
```

3. Build and run the application:

```bash
mvn spring-boot:run
```

## Endpoints

### Create user

`POST /api/users/save`

Request body:

```json
{
  "emailAddress": "user@example.com",
  "phoneNumber": "+1234567890"
}
```

### Send notification

`POST /api/notifications/send`

Request body:

```json
{
  "userId": "<user-id>",
  "message": "Hello from notification service",
  "type": "SMS",
  "priority": "HIGH"
}
```

## Notes

- The application loads `.env` values at startup before Spring Boot initialization.
- Kafka topics include `notification-topic`, `notification-retry-topic`, and `notification-dlq-topic`.
- Redis is used for caching user data and implementing rate limiting.
- The service uses centralized exception handling with uniform API responses.

## Project Documentation

See `PROJECT_DOCUMENTATION.md` for full system design, module architecture, and feature details.
