# Wallet Transaction Service

## Overview

This is a backend service for managing digital wallets. It supports wallet creation, funding, withdrawals, transfers, and transaction management.

---

## Features

* Create wallet
* Fund wallet
* Withdraw funds
* Transfer funds (atomic)
* View wallet balance
* Transaction history (paginated)
* Transaction reversal
* Idempotency support (prevents duplicate transactions)
* Optimistic locking for concurrency safety

---

##  Tech Stack

* Java 17 (LTS)
* Spring Boot 3.5.x
* Spring Data JPA
* PostgreSQL
* Docker
* Swagger (OpenAPI)

---

## Setup Instructions

### 1. Clone repo

```bash
git clone https://github.com/your-username/wallet-service.git
```

### 2. Run application

```bash
mvn clean install
java -jar target/wallet.jar
```

### 3. Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

| Method | Endpoint             | Description         |
| ------ | -------------------- | ------------------- |
| POST   | /api/v1/wallets      | Create wallet       |
| POST   | /{id}/fund           | Fund wallet         |
| POST   | /{id}/withdraw       | Withdraw            |
| POST   | /{id}/transfer       | Transfer            |
| GET    | /{id}/balance        | Get balance         |
| GET    | /{id}/transactions   | Get transactions    |
| POST   | /reverse/{reference} | Reverse transaction |

---

##  Key Engineering Decisions

* **Optimistic Locking** used to prevent concurrent balance updates
* **Idempotency Keys** prevent duplicate transactions
* **Transactional Integrity** ensures atomic transfers
* Clean layered architecture (Controller → Service → Repository)

---

##  Testing

Basic unit tests included using Spring Boot Test.

---

##  Docker

```bash
docker build -t wallet-app .
docker run -p 8080:8080 wallet-app
```

---

## Assumptions

* Wallet ID is generated using UUID
* Transactions are immutable except reversal state
* No authentication implemented (can be added)

---

## Author

Your Name
