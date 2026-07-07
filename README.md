<h1 align="center">🔗 Enterprise URL Shortener Service</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=java" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Keycloak-26.0-blue.svg?style=for-the-badge&logo=keycloak&logoColor=white" alt="Keycloak">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
</p>

<p align="center">
  <strong>A robust, production-ready microservice for URL shortening, featuring secure user authentication via Keycloak, comprehensive link management, and click tracking.</strong>
</p>

---

## 📖 Table of Contents
- [Features](#-features)
- [Architecture & Workflow](#-architecture--workflow)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation & Setup](#installation--setup)
- [API Endpoints](#-api-endpoints)
- [Environment Variables](#-environment-variables)
- [Project Structure](#-project-structure)
- [Security Best Practices](#-security-best-practices)

---

## ✨ Features
- **Secure OAuth2 Authentication**: Fully integrated with Keycloak Identity Provider for robust JWT-based user management.
- **URL Shortening Engine**: Convert long, cumbersome URLs into compact, manageable links with customizable parameters.
- **Analytics & Click Tracking**: Track the number of times a link is accessed for data-driven insights.
- **Expiration Management**: Automatically expire shortened URLs using default settings or custom expiration dates.
- **Global Exception Handling**: Centralized error handling using structured error codes and formatted HTTP responses.
- **Containerized Infrastructure**: One-command setup for PostgreSQL and Keycloak databases using Docker Compose.

---

## 🏗 Architecture & Workflow

The application employs a stateless microservice architecture secured by an external Identity Provider:

1. **Authentication (Login)**: The user provides credentials. The Spring Boot backend securely interacts with the Keycloak server to authenticate the user and retrieve a JWT Access Token and Refresh Token.
2. **Authorization (API Request)**: The client attaches the Access Token in the `Authorization: Bearer <token>` header. The Spring Security Resource Server validates the token signature against the Keycloak Issuer URI.
3. **Core Processing**: Authorized requests reach the URL endpoints to perform CRUD operations, redirection, and analytics tracking in the PostgreSQL database.

---

## 💻 Tech Stack
- **Core**: Java 21, Spring Boot (Web, Data JPA, Security)
- **Identity Provider**: Keycloak 26.0 (OAuth2 Resource Server)
- **Database**: PostgreSQL 15
- **Tools**: Lombok, Maven, Docker & Docker Compose

---

## 🚀 Getting Started

### Prerequisites
- [Java 21](https://jdk.java.net/21/) installed.
- [Docker](https://www.docker.com/) and Docker Compose installed.
- Maven (or use the provided `mvnw` wrapper).

### Installation & Setup

1. **Clone the repository** and navigate to the root directory:
   ```bash
   cd url-shortener-app
   ```

2. **Setup Environment Variables**:
   Create a `.env` file based on the provided template:
   ```bash
   cp .env.example .env
   ```
   *Edit the `.env` file to provide your secure database and Keycloak credentials.*

3. **Start the Infrastructure**:
   Spin up the PostgreSQL database and Keycloak identity provider using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   *Keycloak will be available at `http://localhost:8081`.*

4. **Configure Keycloak Realm**:
   - Access the Keycloak Admin Console at `http://localhost:8081`.
   - Login using the admin credentials defined in your `.env` file.
   - Import or create a realm named `Brahim`.
   - Create a client named `url-shortener-client`.
   - Ensure **Direct Access Grants** is enabled for the client.
   - Generate a Client Secret and add it to your `.env` file (`KEYCLOAK_CLIENT_SECRET`).

5. **Run the Application**:
   Navigate to the core service and start the Spring Boot server:
   ```bash
   cd services/url
   ./mvnw spring-boot:run
   ```
   *The application will start on port `8050`.*

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Register a new user in Keycloak |
| `POST` | `/api/v1/auth/login` | Authenticate user & get JWT tokens |
| `POST` | `/api/v1/auth/refresh` | Obtain a new access token using a refresh token |

### URL Management (Requires Authentication)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/urls` | Create a new shortened URL |
| `GET` | `/api/v1/urls/{shortCode}`| Retrieve URL details and analytics |
| `PATCH` | `/api/v1/urls/{shortCode}`| Update URL destination or expiration |
| `DELETE` | `/api/v1/urls/{shortCode}` | Delete a shortened URL permanently |
| `GET` | `/{shortCode}` | (Public) Redirect to the original long URL |

---

## ⚙️ Environment Variables

A standard `.env` configuration requires:
```properties
POSTGRES_DB=url_shortener
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password

KEYCLOAK_URL=http://localhost:8081
KEYCLOAK_CLIENT_ID=url-shortener-client
KEYCLOAK_CLIENT_SECRET=your_client_secret

KEYCLOAK_ADMIN_USER=your_app_admin_username
KEYCLOAK_ADMIN_USER_PASSWORD=your_app_admin_password
```

---

## 📂 Project Structure

```text
url-shortener-app/
├── docker-compose.yml       # Infrastructure definition (Postgres, Keycloak)
├── .env                     # Secure environment variables (Ignored by Git)
├── .env.example             # Template for required environment variables
└── services/
    └── url/                 # Core URL Shortener Spring Boot Service
        ├── pom.xml
        └── src/main/java/com/brahim/shortener/
            ├── keycloak/    # Keycloak integration and HTTP clients
            ├── url/         # Core URL domain (Entities, Controllers, Services)
            ├── user/        # User domain (Auth DTOs, Exceptions)
            └── UrlApplication.java
```

---

## 🛡️ Security Best Practices

- **Never Commit Secrets**: Ensure your `.env` file is listed in `.gitignore`. The `.env.example` file is provided as a safe template.
- **Rotate Compromised Credentials**: If a password or Keycloak secret is accidentally committed to Git history, assume it is compromised. Rotate it immediately in the Keycloak admin console and database.

---

<p align="center">Developed by <a href="https://github.com/BrahimNajiCode">Brahim Naji</a></p>
