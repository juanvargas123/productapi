# Product Management API

A RESTful API for managing products built with Spring Boot and PostgreSQL.

## Features

- CRUD operations for products
- Input validation
- Pagination support
- Global exception handling
- Docker support

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Data JPA
- PostgreSQL
- Docker
- Lombok

## Architecture

The application follows a layered architecture:

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Manages database operations
- **Model Layer**: Defines the data structure

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/products` | Create a new product |
| GET | `/products` | List all products (paginated) |
| GET | `/products/{id}` | Get a product by ID |
| PUT | `/products/{id}` | Update a product |
| DELETE | `/products/{id}` | Delete a product |

## Running the Application

### Using Docker Compose

1. Make sure you have Docker and Docker Compose installed
2. Run the following command:
   ```bash
   docker-compose up
   ```
3. The API will be available at `http://localhost:8080`

### Development Setup

1. Clone the repository
2. Make sure you have Java 17 and Maven installed
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Docker Configuration

The application uses a multi-stage Docker build:
- First stage: Builds the application using Maven
- Second stage: Creates a lightweight runtime image

The `docker-compose.yml` file sets up:
- The application service
- PostgreSQL database
- Persistent volume for database data

## Git Workflow

The project follows these Git practices:
- Feature branches for new features
- Conventional commits
- Pull requests for code review
- Main branch protection

## Testing the API

You can use the following curl commands to test the API:

```bash
# Create a product
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Test Description","price":10.99}'

# Get all products
curl http://localhost:8080/products

# Get a specific product
curl http://localhost:8080/products/1

# Update a product
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Product","description":"Updated Description","price":15.99}'

# Delete a product
curl -X DELETE http://localhost:8080/products/1
``` 