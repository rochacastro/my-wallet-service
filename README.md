# My Wallet Service

A simple wallet control REST API built with Spring Boot, allowing users to deposit, withdraw, and transfer funds.
Includes endpoints for user management and wallet balance/history queries.

## Features

- Create users
- Deposit, withdraw, and transfer funds
- Query current and historical wallet balances
- Containerized with Docker and Docker Compose
- PostgreSQL database integration
- OpenAPI (Swagger) documentation

## Technologies Used

- Java 21
- Spring Boot 3.5.3
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
- MapStruct
- OpenAPI (springdoc)

## API Endpoints (Summary)

| Endpoint                                             | Method | Description                    |
|------------------------------------------------------|--------|--------------------------------|
| `/api/user`                                          | POST   | Create a new user              |
| `/api/wallet/v1/deposit`                             | PATCH  | Deposit funds by CPF           |
| `/api/wallet/v1/withdraw`                            | PATCH  | Withdraw funds by CPF          |
| `/api/wallet/v1/transfer`                            | PATCH  | Transfer funds between users   |
| `/api/wallet/v1/balance?cpf=...`                     | GET    | Get current wallet balance     |
| `/api/wallet/v1/historical-balance?cpf=...&date=...` | GET    | Get historical balance by date |

For detailed request/response formats, see the [OpenAPI spec](./apispec-my-wallet-service.yaml) or access Swagger UI
when running the app.

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (for containerized setup)

### Running Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rochacastro/my-wallet-service
   cd my-wallet-service
   ```
2. **Start PostgreSQL (if not using Docker):**
   Ensure a PostgreSQL instance is running on `localhost:5433` with database `demo`, user `postgres`, password
   `postgres`.

3. **Build and run locally:**
   ```bash
   ./mvnw clean spring-boot:run
   ```

4. **Access the API:**
    - API root: [http://localhost:8080](http://localhost:8080)
    - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Running with Docker Compose

1. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```
   This will start:
    - The wallet service (on port 8080)
    - PostgreSQL database (on port 5433)
    - pgAdmin (on port 5050, default login: admin@admin.com / admin)

2. **Access the API:**
    - API root: [http://localhost:8080](http://localhost:8080)
    - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Running Tests

Run all tests with:

```bash
./mvnw test
```

## Configuration

Default configuration is set in `src/main/resources/application.yaml`. No environment variables are required for basic
usage.
