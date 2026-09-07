# Pensionat Customer Service

Customer management API for the Pensionat Booking System, built with Java and Spring Boot.

The application handles customer registration, account management, login and database persistence. It communicates with the Booking Service before deleting a customer account.

---

## Related Repositories

**Booking Backend:** [pensionat-booking-backend](https://github.com/igor-gomes-academic/pensionat-booking-backend)

**Frontend:** [pensionat-booking-frontend](https://github.com/igor-gomes-academic/pensionat-booking-frontend)

---

## Architecture Overview

```text
User
  ↓
Frontend (Next.js / React)
  ↓ REST API requests
Customer Service (Spring Boot)
  ↓ Spring Data JPA / Hibernate
Customer MySQL Database

Customer Service
  ↓ REST API request before account deletion
Booking Service
```

Each service owns its own database. The Customer Service never reads from or writes directly to the Booking Service database.

- The Frontend provides the user interface
- The Booking Service manages rooms and bookings
- The Customer Service manages customer accounts

---

## Technologies

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Dotenv
- Bean Validation
- Spring Security Crypto
- JUnit
- Docker
- Docker Compose

---

## Project Structure

The Customer Service is organized into separate layers:

- **Controller layer:** Handles API requests and responses
- **Service layer:** Contains customer business logic and validation
- **Repository layer:** Handles database access through Spring Data JPA
- **Client layer:** Handles REST communication with the Booking Service
- **Entity classes:** Represent database tables
- **DTOs:** Define request and response data
- **Exceptions:** Provide clear error handling and HTTP status codes
- **Configuration:** Provides CORS and password encoding configuration

---

## Functionality

The Customer Service supports:

1. Registering customers
2. Retrieving a customer by ID
3. Retrieving all customers
4. Updating customer information
5. Deleting customer accounts
6. Customer login
7. Hashing customer passwords before persistence
8. Validating customer input
9. Checking for active bookings before deleting a customer
10. Handling Booking Service availability errors
11. Returning JSON responses with appropriate HTTP status codes

---

## Database

The Customer Service uses its own MySQL database.

The main entity is:

- **CustomerEntity**

Customer data is stored only in the Customer Service database. The Booking Service stores only the customer ID associated with each booking.

---

## Business Rules

- Customer information must pass validation before it is stored
- Passwords are hashed before database persistence
- A customer must exist before their information can be retrieved, updated or deleted
- A customer cannot be deleted while active bookings exist
- The Customer Service asks the Booking Service about active bookings through a REST request
- If the Booking Service is unavailable, the Customer Service returns a clear service unavailable response

---

## API Communication

The frontend sends customer requests to the Customer Service on port `8081`.

Before deleting a customer, the Customer Service sends a REST request to the Booking Service on port `8080`:

```text
GET /api/bookings/customer/{customerId}/has-active
```

The response determines whether the customer account can be deleted.

---

## Testing

The Customer Service includes an integration test that starts the Spring Boot application on a random port and performs a real HTTP request against the customer API.

The shared Docker Compose environment also provides isolated test databases and dedicated test runners for both services.

From the shared environment directory, run:

```console
docker compose --profile test up --build booking-tests customer-tests
```

After the tests finish, stop and remove the test containers and network:

```console
docker compose --profile test down
```

> The test databases are separate from the normal application databases.

---

## Running the Complete System with Docker Compose

This repository contains a template with the shared environment files:

```text
pensionat-local-environment-template/
├── .env.example
└── docker-compose.yaml
```

The template directory is only used to distribute these two files. It is not the directory where the repositories should be placed.

Create a new local parent directory. The examples below use the name `pensionat`, but the directory can have any name. Clone or move all three repositories into this directory, including the Customer Service repository:

```text
pensionat/
├── pensionat-booking-frontend/
├── pensionat-booking-backend/
└── pensionat-customer-service/
```

Copy `docker-compose.yaml` and `.env.example` from `pensionat-customer-service/pensionat-local-environment-template` into the new parent directory. Keep the template directory in the Customer Service repository as the versioned source of the shared environment configuration.

Rename the copied `.env.example` file to `.env`. Review the configuration and replace the example passwords with your own secure values before starting the environment.

The final structure should be:

```text
pensionat/
├── .env
├── docker-compose.yaml
├── pensionat-booking-frontend/
├── pensionat-booking-backend/
└── pensionat-customer-service/
    └── pensionat-local-environment-template/
        ├── .env.example
        └── docker-compose.yaml
```

Start the complete system with one command:

```console
docker compose up --build -d
```

The complete local environment provides:

```text
Frontend:          http://localhost:3000
Booking Service:   http://localhost:8080
Customer Service:  http://localhost:8081
Booking Database:  localhost:3308
Customer Database: localhost:3307
```

Stop the environment with:

```console
docker compose down
```

---

## Team

- Patric Westman
- Daniel Lyytikäinen
- Niklas Dahlström
- Igor Gomes
