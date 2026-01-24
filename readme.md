# Inventory & Order Management System

This project demonstrates a real-world inventory and order processing system,
designed with clean architecture and enterprise best practices commonly used in
 software development teams.

The focus is on maintainability, correctness, and clear business logic rather
than UI complexity.

---

## Key Features

- Product management with SKU
- Inventory handling with FIFO logic based on expiry dates
- Support for the same product with multiple expiry batches
- Order creation and cancellation
- Transaction-safe stock reservation and rollback
- Scheduled check for expired inventory batches

---

## Architecture

The application follows a layered architecture:

Controller → Service → Repository → Database

### Package Structure

com.gt.warehouse  
├── controller  
├── service  
├── repository  
├── domain  
├── dto  
└── config

### Design Decisions

- Business logic is isolated in the service layer
- FIFO stock allocation is based on earliest expiry date
- Order operations are transactional to ensure data consistency
- DTOs separate API contracts from domain models

---

## Tech Stack

Backend:
- Java 17 (compatible with Java 11)
- Spring Boot
- Spring Data JPA
- Maven
- Postgres database 


---

## How to Run the Application

Prerequisites:
- Java 11 or higher
- Git

Run locally:

./mvnw spring-boot:run

The application will start at:

http://localhost:8080

---

## Testing

Basic service-level tests demonstrate:
- FIFO stock allocation logic
- Inventory rollback on order cancellation

The goal is to validate business rules and data consistency rather than full test coverage.

---

## Project Purpose

This project was created to demonstrate:
- Clean Java & Spring Boot code
- Realistic inventory and order processing logic
- Enterprise-style structure
- Readable, maintainable, and testable code

It reflects systems commonly developed in logistics, retail, and enterprise
environments in Germany.

---

## Author

Dawit Tekle Gebreyohanes  
Senior Full-Stack Software Engineer  
Java | Spring Boot | React | Node.js | AWS | Azure

---

## License

This project is provided for demonstration and evaluation purposes only.
