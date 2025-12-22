# SmartLogiDMS – Smart Delivery Management System  
*(DDD-inspired Spring Boot API for parcel logistics)*

---

## Description
SmartLogiDMS is a **logistics management system** for **SmartLogi**, a Moroccan parcel delivery company. It automates operations, ensures **full traceability**, reduces errors, and optimizes delivery routes.

### Key Objectives
- Centralize management of **clients, recipients, parcels, drivers, zones**
- Track **full parcel lifecycle**: Collect → Stock → Transit → Deliver
- Enable **precise search & tracking** by city, zone, status, priority
- Provide **complete audit history**
- Optimize routes & support **decision-making**

Built with **Spring Boot**, **PostgreSQL**, **JPA**, **MapStruct**, **Liquibase**, **Swagger**, and **AOP logging**.

---

## DDD Architecture (Concise)

| DDD Concept         | Implementation |
|---------------------|----------------|
| **Bounded Contexts** | `delivery` (colis, product) • `masterdata` (client, driver, zone) |
| **Aggregate**        | `Colis` (root) owns `ColisProduit`, controls lifecycle & status |
| **Domain Service**   | `ColisServiceImpl` → status transitions, validation |
| **Value Object**     | `ColisProduit`, `Adresse` |
| **Ubiquitous Language** | `colis`, `expediteur`, `livreur`, `zone`, `statut` |
| **Layering**         | `domain` → model • `service` → rules • `api` → DTOs |

---

## Type-Safe & Generic Design

### ID Strategies

```java
StringBaseEntity → "acc0db6b-..." (UUID strings)
UuidBaseEntity   → java.util.UUID
LongBaseEntity   → Auto-increment Long

```

### Delete Strategies
- **Soft Delete**: `deleted = true` + `@SQLRestriction("deleted = false")`
- **Hard Delete**: Direct `DELETE`

### Generic CRUD
```java
StringCrudServiceImpl<Colis, ColisRequestDTO, ColisResponseDTO>
```
→ Reusable, **type-safe**, zero duplication.

---

## Dynamic Search & Filter (JPA Specifications)

```bash
?filter=statut:eq:IN_TRANSIT&poids:gt:5&reference:like:Casablanca
?search=Laptop
```

→ `GenericSpecification<T>` + `FilterParser`  
→ Reusable across **all aggregates**

---

## Key Endpoints

| Method | Endpoint | Description |
|-------|--------|-------------|
| `POST` | `/api/colis` | Create parcel with products |
| `GET`  | `/api/colis/{id}/products` | Paginated, sorted, filtered products |
| `PUT`  | `/api/colis/{id}/status` | Update status (validated transition) |
| `GET`  | `/api/colis/synthese/zone` | Count parcels by zone |

**Swagger**: `http://localhost:8080/swagger-ui.html`

---

## Tech Stack
- **Spring Boot 3.5+** • **PostgreSQL**
- **JPA + Specifications** • **MapStruct**
- **Liquibase** • **Swagger/OpenAPI**
- **AOP Logging** • **Jakarta Validation**

---

## Security Implementation

### Authentication Flow
1. **Login**: POST `/auth/login` with credentials
2. **JWT Generation**: Server validates and returns a signed JWT token
3. **API Access**: Include `Authorization: Bearer <token>` in headers
4. **Stateless**: No server-side sessions - JWT contains all necessary claims

### Security Features
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Three primary roles with granular permissions
- **CORS Protection**: Configured for internal frontends only
- **Password Encryption**: BCrypt password hashing
- **CSRF Protection**: Enabled for state-changing operations

### User Roles & Permissions

| Role | Code | Access Scope |
|------|------|--------------|
| **Manager** | `ROLE_MANAGER` | Full system access - all CRUD operations |
| **Driver** | `ROLE_DRIVER` | View/update assigned parcels only |
| **Client** | `ROLE_CLIENT` | Create and track own parcels |

#### Role Details:
- **Manager**: Complete administrative access to all modules
- **Driver**: Can only access parcels assigned to them, update status
- **Client**: Can create parcels and view their own parcel history

### Security Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| `POST` | `/auth/login` | Authenticate and receive JWT | Public |
| `GET`  | `/api/admin/permissions` | List all permissions | Manager only |
| `POST` | `/api/admin/roles` | Create new role | Manager only |

### Security Configuration
- **JWT Secret**: Configurable via `jwt.secret` property
- **Token Expiration**: 24 hours (configurable)
- **CORS Allowed Origins**: `localhost:4200`, `localhost:3000`, `localhost:8080`
- **Password Policy**: BCrypt 

### Project Structure (Security)
```
src/main/java/io/github/tawdi/security/
├── auth/                          # Authentication layer
│   ├── AuthenticationController.java
│   ├── AuthenticationService.java
│   └── DTOs (Request/Response)
├── jwt/                           # JWT utilities
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtConfig.java
├── user/                          # User management
│   ├── UserAccount.java
│   ├── UserAccountRepository.java
│   └── UserDetailsServiceImpl.java
├── permission/                    # RBAC management
│   ├── domain/ (Role, Permission entities)
│   ├── repository/
│   ├── service/
│   └── api/ (Admin endpoints)
└── config/SecurityConfig.java     # Main security config
```

### Database Security Schema
- **User Accounts**: Secure credential storage with BCrypt hashing
- **Roles & Permissions**: Flexible RBAC system with admin management
- **Audit Logging**: Track authentication attempts and security events

### Usage Examples

#### 1. Authentication
```bash

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"password"}'
```

#### 2. Access Protected Endpoint
```bash

curl -X GET http://localhost:8080/api/colis \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

#### 3. Role-Specific Access
```java
// Controller annotations
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class ColisController { 
    /* ... */
    @PreAuthorize("hasAuthority('PACKAGE:UPDATE_STATUS')")
    public ResponseEntity<ApiResponseDTO<ColisResponseDTO>> updateStatus(/**/){/**/}
}


```



---
### Test Framework
- **JUnit 5** - Unit testing
- **Mockito** - Mocking dependencies
- **Spring Boot Test** - Integration testing
- **JaCoCo** - Code coverage analysis
- **SonarQube** - Code quality gate

### Test Coverage
- **Unit Tests**: All service layers, controllers, domain logic
- **Integration Tests**: REST endpoints, database interactions
- **Exception Handling**: Comprehensive error scenario testing

### Coverage Goals
- **Target**: >90% code coverage
- **Current**: 83%
- **Quality Gate**: SonarQube passing with zero issues

### Running Tests
```bash
# Run all tests
mvn clean test

# Run with coverage report
mvn clean test jacoco:report

# Run SonarQube analysis
mvn sonar:sonar
````
---

## Test Reports
- 
- **JaCoCo Report**: target/site/jacoco/index.html
  ![ JaCoCo Report ](docs/jacoco.png)

- **Surefire Reports**: target/surefire-reports/
- **SonarQube Dashboard** : image below

![SonarQube Dashboard (project report)](docs/sonar_0.png)
![SonarQube Dashboard (project report)](docs/sonar_1.png)
---

## Setup

```bash
# 1. Clone
git clone <repo>

# 2. DB
createdb smartlogi_dms

# 3. Migrate
mvn liquibase:update

# 4. Run
mvn spring-boot:run
```

**API**: `http://localhost:8080`  
**Swagger**: `http://localhost:8080/swagger-ui.html`

---

## Build
```bash
mvn clean package
```

---

## Project Structure
```
src/main/java/com/smartlogi/smartlogidms/
├── common/        → generics, specs, AOP, base CRUD
├── delivery/      → colis, product, historique
├── masterdata/    → client, driver, recipient, zone
└── config/        → OpenAPI
```
---

##  Diagram UML

The following diagram illustrates the main entities:

![Application UML Diagram](docs/smartlogi.d.class.2.drawio.png)


---


## License
**MIT** – Free to use, modify, distribute.

---
