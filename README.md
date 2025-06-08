# News Management System API

A RESTful API for managing news articles, categories, tags, and users with role-based access control, built with Spring Boot and PostgreSQL.

## Features
- User authentication with JWT and 2FA
- Article management (CRUD, scheduling, featuring)
- Category and tag management
- Advanced search and recommendations
- Reporting and analytics
- System administration (settings, backups, audit logs)

## Tech Stack
- **Backend**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: PostgreSQL 16.x
- **Authentication**: JJWT for JWT, TOTP for 2FA
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Caching**: Redis
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Deployment**: Docker, GitHub Actions

## Prerequisites
- Java 17 or 21
- Maven 3.x
- PostgreSQL 16.x
- Redis (optional for caching)
- Git

## Setup Instructions
1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/news-management-system.git
   cd news-management-system