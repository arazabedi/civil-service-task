# Task Management API

[![Build Status](https://img.shields.io/badge/job%20application%20task-8A2BE2)](https://example.com/build)

## Overview

The **Task Management API** is a backend application built with Spring Boot. It provides a RESTful interface for creating, retrieving, updating, and deleting tasks. The API is designed to be consumed by frontend applications, such as those built with Next.js, and it stores data in a PostgreSQL database.

I have included a frontend directory for my Next.js app in this repository. This consumes the API and displays the database data in the form of a kanban board that allows the user to drag and drop a task onto another status column in order to update the status on the backend. To run the app use:

```bash
next dev
```

---
<img width="1611" alt="image" src="https://github.com/user-attachments/assets/16f78446-30ef-4987-b5be-04bc923099de" />



## Technology Stack

- Java 17+
- Spring Boot
- Spring Web
- Jakarta Bean Validation
- Lombok
- PostgreSQL

---

## Getting Started

### Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK) 17+
- Apache Maven 3.6.0+
- A running PostgreSQL instance

### Configuration

Application settings are defined in `application.yaml`. Example:

```yaml
server:
  port: 4000
  shutdown: graceful

management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      base-path: /
      exposure:
        include: info

springdoc:
  packagesToScan: uk.gov.hmcts.reform.dev.controllers
  writer-with-order-by-keys: true

spring:
  config:
    import: optional:configtree:/mnt/secrets/test/
  application:
    name: Dev Test
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}${DB_OPTIONS:}
    username: ${DB_USERNAME}
    properties:
      charSet: UTF-8
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        jdbc:
          lob:
            non_contextual_creation: true
```

Set the following environment variables accordingly:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD` (handled securely, e.g., via secrets manager)

### Installation & Execution

Clone the repository:

```bash
git clone <YOUR_REPOSITORY_URL>
cd <YOUR_SPRINGBOOT_API_DIRECTORY>
```

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/<YOUR_APPLICATION_NAME>.jar
```

Access the API at: `http://localhost:4000`

---

## API Documentation

### Create a Task

**POST** `/tasks`

**Request Body:**
```json
{
  "title": "Task Title",
  "description": "Task description",
  "status": "TO_DO",
  "dueDateTime": "2025-05-01T12:00:00"
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "title": "Task Title",
  "description": "Task description",
  "status": "TO_DO",
  "dueDateTime": "2025-05-01T12:00:00.000"
}
```

---

### Get All Tasks

**GET** `/tasks`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid1",
    "title": "Task 1",
    "status": "IN_PROGRESS"
  },
  {
    "id": "uuid2",
    "title": "Task 2",
    "status": "DONE"
  }
]
```

---

### Get Task by ID

**GET** `/tasks/{id}`

**Response:** `200 OK`
```json
{
  "id": "valid-uuid",
  "title": "Task Title",
  "description": "...",
  "status": "TO_DO",
  "dueDateTime": "2025-05-01T12:00:00.000"
}
```

**Errors:**
- `404 Not Found`: Task not found

---

### Update Task Status

**PATCH** `/tasks/{id}`

**Request Body:**
```json
{
  "status": "DONE"
}

```

**Response:** `200 OK`
```json
{
  "id": "valid-uuid",
  "title": "Task Title",
  "status": "DONE"
}
```

**Errors:**
- `400 Bad Request`: Invalid status
- `404 Not Found`: Task not found

---

### Delete a Task

**DELETE** `/tasks/{id}`

**Response:** `204 No Content`

**Errors:**
- `404 Not Found`: Task not found

---

## Frontend Integration

This API is designed to be consumed by a frontend client (e.g. built with React or Next.js). Ensure the client points to the correct base URL (default: `http://localhost:4000`). Additionally check CorsConfig.java if there are CORS errors.

---

## Error Handling

The API uses standard HTTP status codes:

- `400 Bad Request`: Invalid input
- `404 Not Found`: Resource not found
- `204 No Content`: Successful deletion

---

