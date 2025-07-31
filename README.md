# Store Management API

A Spring Boot REST API for managing store products and users with Kafka event publishing

## 🚀 Features

- **Product Management**: CRUD operations for store products
- **User Management**: User registration and role assignment
- **Event-Driven Architecture**: Kafka event publishing for product operations
- **Security**: HTTP Basic Authentication with role-based access control
- **API Documentation**: Interactive Swagger UI
- **Database**: PostgreSQL with Flyway migrations
- **Monitoring**: Execution time tracking with custom aspects

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Primary database
- **Flyway** - Database migrations
- **Apache Kafka** - Event streaming
- **Swagger/OpenAPI 3** - API documentation
- **Lombok** - Code generation
- **Maven** - Build tool

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL database
- Apache Kafka (with Zookeeper)
- Maven 3.6+

## 🏃‍♂️ Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/ivanrobo/store-management-api.git
cd store-management-api
```

### 2. Database Setup
Create a PostgreSQL database:
```sql
CREATE DATABASE store_management;
```

### 3. Start Kafka (Optional)
If you want to test event publishing, start Kafka using Docker Compose:
```bash
docker-compose up -d
```

### 4. Configure Application
Update `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/store_management
spring.datasource.username=your_username
spring.datasource.password=your_password

# Kafka (optional - set to false to disable)
app.kafka.enabled=true
```

### 5. Run the Application
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📚 API Documentation

Access the interactive Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

### Authentication
The API uses HTTP Basic Authentication. 

## 🔑 User Roles

- **USER**: Basic access (read-only for products)
- **MANAGER**: Can create, update, and delete products
- **ADMIN**: Full access including user role management

## 📊 API Endpoints

### Products
- `GET /api/products` - Get all products (paginated) 📖 *Public*
- `GET /api/products/{id}` - Get product by ID 📖 *Public*
- `POST /api/products` - Create new product 🔒 *MANAGER/ADMIN*
- `PATCH /api/products/{id}` - Update product 🔒 *MANAGER/ADMIN*
- `DELETE /api/products/{id}` - Delete product 🔒 *MANAGER/ADMIN*

### Users
- `POST /api/users` - Create new user 📖 *Public*
- `PATCH /api/users/assign-role` - Assign role to user 🔒 *ADMIN*

## 🎯 Kafka Events

When Kafka is enabled, the following events are published:

- **ProductCreatedEvent** - When a product is created
- **ProductUpdatedEvent** - When a product is updated
- **ProductDeletedEvent** - When a product is deleted

Events are published to the `product-events` topic.

## 🗄 Database Schema

The application uses Flyway for database migrations. Schema includes:
- `products` table - Product information
- `users` table - User accounts
- `roles` table - User roles
- `user_roles` table - User-role relationships