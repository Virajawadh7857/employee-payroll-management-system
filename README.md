# Employee Payroll Management System

A backend-focused Employee Payroll Management System built using Java and Spring Boot. The application provides secure REST APIs for employee management, payroll processing, authentication, and role-based authorization.

## Project Overview

The system allows HR and administrators to manage employee information and payroll records through REST APIs.

It includes:

- Employee CRUD operations
- Payroll creation and management
- JWT-based authentication
- Role-based authorization using Spring Security
- Input validation
- Global exception handling
- MySQL database integration
- Swagger/OpenAPI API documentation
- Unit, controller, security, and integration testing

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Hibernate
- Maven

### Database
- MySQL

### Testing
- JUnit 5
- Mockito
- Spring Boot Test
- Integration Testing
- JaCoCo

### API Documentation
- Swagger UI
- OpenAPI

### Tools
- Git
- GitHub
- Postman
- IntelliJ IDEA / Spring Tool Suite
- MySQL

## Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL Database