# API Pedidos

REST API for managing clients, categories, products, and orders with stock control.

## Requirements

- Java 21
- Docker and Docker Compose
- Maven Wrapper (`./mvnw`)

## How to run

1. Start PostgreSQL:

```bash
docker compose up -d
```

2. Start the application:

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## How to test

Run the full test suite:

```bash
./mvnw test
```

Run only unit tests for a specific class:

```bash
./mvnw -Dtest=ClientServiceTest test
```

Run the integration test:

```bash
./mvnw -Dtest=ApiPedidosApplicationTests test
```

## Main endpoints

### Categories

- `POST /categories` - Create a category
- `GET /categories` - List categories
- `GET /categories/{id}` - Get category by id
- `PATCH /categories/{id}` - Update a category
- `DELETE /categories/{id}` - Delete a category

### Clients

- `POST /clients` - Create a client
- `GET /clients` - List clients
- `GET /clients/{id}` - Get client by id
- `PATCH /clients/{id}` - Update a client
- `DELETE /clients/{id}` - Delete a client

### Products

- `POST /products` - Create a product
- `GET /products` - List products
- `GET /products/{id}` - Get product by id
- `PATCH /products/{id}` - Update a product
- `DELETE /products/{id}` - Delete a product

### Orders

- `POST /orders` - Create an order
- `GET /orders` - List orders
- `GET /orders/{id}` - Get order by id
- `PATCH /orders/{id}/status` - Update order status
- `DELETE /orders/{id}` - Delete an order

## Database

Local development uses PostgreSQL configured in `docker-compose.yml`:

- host: `localhost`
- port: `2345`
- database: `api_pedidos`
- user: `root`
- password: `admin123`

## Notes

- Database migrations are handled by Flyway.
- Integration tests use Testcontainers and require Docker.
