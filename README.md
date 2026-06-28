# 💳 Digital Banking System

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

A **RESTful digital banking system** built with Spring Boot that provides secure user authentication, digital wallet management, and transaction processing. Designed as a lightweight, file-based (JSON) banking API — no database setup required.

---

## ✨ Features

- **User Authentication** — Register, login, and logout with session-based security
- **Role-Based Access Control** — User and Admin roles with Spring Security
- **Digital Wallet** — Each user gets a unique wallet with a wallet code
- **Transactions** — Deposit, withdraw, and transfer funds between wallets
- **Admin Dashboard** — View all users, transactions, and perform bank transfers
- **File-Based Storage** — Uses JSON files instead of a database for simplicity
- **Password Encryption** — BCrypt hashing for secure password storage
- **Global Exception Handling** — Consistent error responses across all endpoints

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                  REST Clients                    │
│              (Postman / cURL / UI)               │
└──────────────────┬──────────────────────────────┘
                   │ HTTP
┌──────────────────▼──────────────────────────────┐
│              Controllers Layer                   │
│  AuthController · WalletController · AdminCtrl   │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│               Service Layer                      │
│        AuthService · WalletService               │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│             Repository Layer                     │
│          DataRepository (JSON Files)             │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│           data/ (JSON Storage)                   │
│    users.json · wallets.json · transactions.json │
└─────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Technology       | Purpose                    |
| ---------------- | -------------------------- |
| Java 21          | Programming language       |
| Spring Boot 4.0  | Application framework      |
| Spring Security  | Authentication & authorization |
| Jackson          | JSON serialization         |
| BCrypt           | Password hashing           |
| Maven            | Build & dependency management |

---

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.9+** (or use the included Maven wrapper)

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Adityamtl/Java-Project.git
cd Java-Project/digital-banking-system/digital-banking-system
```

### 2. Configure the admin secret key (optional)

Edit `src/main/resources/application.properties` and change the admin master key:

```properties
app.admin.master-key=${ADMIN_MASTER_KEY:CHANGE_ME_IN_PRODUCTION}
```

Or set it via environment variable:

```bash
export ADMIN_MASTER_KEY=your-secure-key-here
```

### 3. Build and run

```bash
# Using Maven wrapper (no Maven installation needed)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The server starts at **http://localhost:8080**

---

## 📡 API Reference

### Authentication

| Method | Endpoint             | Description        | Auth Required |
| ------ | -------------------- | ------------------ | ------------- |
| POST   | `/api/auth/register` | Register new user  | No            |
| POST   | `/api/auth/login`    | Login              | No            |
| POST   | `/api/auth/logout`   | Logout             | No            |

### Wallet Operations

| Method | Endpoint              | Description              | Auth Required |
| ------ | --------------------- | ------------------------ | ------------- |
| GET    | `/api/wallet/balance` | Get wallet balance       | Yes (USER)    |
| POST   | `/api/wallet/deposit` | Deposit funds            | Yes (USER)    |
| POST   | `/api/wallet/withdraw`| Withdraw funds           | Yes (USER)    |
| POST   | `/api/wallet/transfer`| Transfer to another user | Yes (USER)    |
| GET    | `/api/wallet/history` | Get transaction history  | Yes (USER)    |

### Admin Operations

| Method | Endpoint                  | Description           | Auth Required |
| ------ | ------------------------- | --------------------- | ------------- |
| GET    | `/api/admin/transactions` | Get all transactions  | Yes (ADMIN)   |
| GET    | `/api/admin/users`        | Get all users         | Yes (ADMIN)   |
| POST   | `/api/admin/bank-transfer`| Bank-initiated transfer| Yes (ADMIN)  |

---

## 📝 Usage Examples

### Register a new user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "message": "User registered successfully",
  "username": "john_doe",
  "role": "USER",
  "walletCode": "WAL-A1B2C3D4"
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "john_doe",
    "password": "securePassword123"
  }'
```

### Deposit funds

```bash
curl -X POST http://localhost:8080/api/wallet/deposit \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"amount": 1000.00}'
```

### Transfer funds

```bash
curl -X POST http://localhost:8080/api/wallet/transfer \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "targetWalletCode": "WAL-X9Y8Z7W6",
    "amount": 250.00
  }'
```

### Register as Admin

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_user",
    "email": "admin@bank.com",
    "password": "adminPass123",
    "role": "ADMIN",
    "masterSecretKey": "CHANGE_ME_IN_PRODUCTION"
  }'
```

---

## 📁 Project Structure

```
digital-banking-system/
├── data/                          # Runtime JSON data storage
│   ├── users.json
│   ├── wallets.json
│   └── transactions.json
├── src/
│   ├── main/
│   │   ├── java/com/spring_project/digital_banking_system/
│   │   │   ├── config/            # Security & filter configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── SessionAuthenticationFilter.java
│   │   │   ├── controller/        # REST API endpoints
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   └── WalletController.java
│   │   │   ├── exception/         # Global exception handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── model/             # Data models & enums
│   │   │   │   ├── Role.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── TransactionStatus.java
│   │   │   │   ├── TransactionType.java
│   │   │   │   ├── User.java
│   │   │   │   └── Wallet.java
│   │   │   ├── repository/        # Data access layer
│   │   │   │   └── DataRepository.java
│   │   │   ├── service/           # Business logic
│   │   │   │   ├── AuthService.java
│   │   │   │   └── WalletService.java
│   │   │   └── DigitalBankingSystemApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── README.md
├── LICENSE
└── CONTRIBUTING.md
```

---

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👥 Team Members & Contributions

| Name | Roll Number | Module | Description |
| ---- | ----------- | ------ | ----------- |
| **Sasipreetam Sajja** | BT2024124 | Model | Designed and implemented data models including `User`, `Transaction`, `Role`, `TransactionType`, and `TransactionStatus` |
| **Laksh Chovatiya** | BT2024056 | AuthService | Implemented authentication service with user registration, login, logout, and session management |
| **Srijan Gupta** | BT2024098 | Exception Handling | Created global exception handler with custom `ApiError` responses and security exception handling |
| **Kkshiteej N Tiwari** | BT2024154 | Repository | Developed JSON file-based data repository for persistent storage of users and transactions |
| **Aditya Mittal** | BT2024188 | Controller | Built REST controllers for authentication and wallet endpoints |
| **Ridwan Umar** | BT2024217 | WalletService | Implemented wallet operations including balance inquiry, deposits, and withdrawals |
