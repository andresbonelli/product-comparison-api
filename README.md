# Product Comparison API

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A robust RESTful API designed for **educational purposes**. This project serves as a "real-world" backend for frontend developers to practice CRUD operations, complex filtering, and secure API consumption.

## 🚀 Features

* **Product Management**: Full CRUD operations with standardized JSON responses.
* **Advanced Discovery**: Paginated search with filters for name, rating, and price ranges.
* **Comparison Engine**: Compare multiple products simultaneously via unique IDs.
* **Production-Ready Security**:
    * **API Key Protection** for sensitive operations.
    * **Rate Limiting** (via Bucket4j) to prevent service abuse.
* **Database Reliability**: Automated schema versioning with **Flyway**.
* **Interactive Documentation**: Fully interactive **Swagger UI** for testing endpoints.

---

## 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.2.x |
| **Persistence** | Spring Data JPA + MySQL (Prod) / H2 (Test) |
| **Security** | Bucket4j (Rate Limiting) |
| **Migration** | Flyway |
| **Documentation** | SpringDoc OpenAPI (Swagger UI) |
| **Infrastructure** | Docker & Docker Compose |

---

## 🏗️ Architecture & Design Patterns

The application follows a **Clean Layered Architecture** to ensure maintainability and separation of concerns.



### Layered Responsibility
* **Presentation Layer**: REST Controllers handling HTTP logic and DTO mapping.
* **Business Layer**: Service layer encapsulating logic, validations, and rate-limiting rules.
* **Persistence Layer**: Domain entities and Repositories using the **Repository Pattern**.

### Patterns Used
* **DTO Pattern**: Decouples the internal database schema from the external API contract.
* **Global Exception Handling**: Centralized `@ControllerAdvice` ensures consistent, helpful JSON error structures.
* **Dependency Injection**: Decoupled components managed by the Spring IoC container.
* **Builder Pattern**: Simplified object construction leveraging Lombok.

---

## 📋 Quick Start

### Option 1: Docker (Recommended)
Launch the entire stack (API + MySQL) with a single command:
```bash
docker compose up -d
```

### Option 2: Manual Run
1. Ensure you have JDK 21 and Maven installed.

2. Run the application:
```bash
mvn spring-boot:run
```
---

## 📖 API Documentation
- Swagger UI:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI spec:
[http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## 📚 Endpoint description 
**Important:** Ensure your API Key is always included in the request headers as X-API-KEY.
```javascript
headers: {
  'X-API-KEY': '************',
  'Content-Type': 'application/json'
}
```
- Get all products (with pagination data)
```http
GET /api/products
```
```json
{
   "products": [
      {
         "id": 1,
         "name": "Laptop Dell XPS 15",
         "imageUrl": "https://example.com/images/dell-xps-15.jpg",
         "description": "High-performance premium p1 powered by an 11th Gen Intel Core i7 processor.",
         "price": 1299.99,
         "rating": 4.5,
         "specifications": "Processor: Intel Core i7-11800H, RAM: 16GB DDR4, Storage: 512GB NVMe SSD, Display: 15.6\" 4K OLED (3840x2160), Graphics Card: NVIDIA GeForce RTX 3050 Ti 4GB, Weight: 2.0 kg, Battery: up to 8 hours"
      }
   ],
   "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalElements": 1,
      "totalPages": 1,
      "isLast": true
   }
}
```
- Search products (with filters, order, and pagination)
```http
GET /api/products/advancedSearch?page=1&size=10&sortDir=desc&sortBy=price
```
```json
{
   "products": [
      {
         "id": 1,
         "name": "Laptop Dell XPS 15",
         "imageUrl": "https://example.com/images/dell-xps-15.jpg",
         "description": "High-performance premium p1 powered by an 11th Gen Intel Core i7 processor.",
         "price": 1299.99,
         "rating": 4.5,
         "specifications": "Processor: Intel Core i7-11800H, RAM: 16GB DDR4, Storage: 512GB NVMe SSD, Display: 15.6\" 4K OLED (3840x2160), Graphics Card: NVIDIA GeForce RTX 3050 Ti 4GB, Weight: 2.0 kg, Battery: up to 8 hours"
      }
   ],
   "pagination": {
      "currentPage": 1,
      "pageSize": 10,
      "totalElements": 1,
      "totalPages": 1,
      "isLast": true
   }
}
```
-   Get one by ID
```http
GET /api/products/{id}
```

```json
[
   {
      "id": 1,
      "name": "Laptop Dell XPS 15",
      "imageUrl": "https://example.com/images/dell-xps-15.jpg",
      "descripcion": "Premium laptop...",
      "price": 1299.99,
      "rating": 4.5,
      "specifications": "Processor: Intel Core i7-11800H..."
  }
]
```

- Compare by ids  
```http
GET /api/products/compare?ids=1,2,3
```

```json
[
   {
      "id": 1,
      "name": "Laptop Dell XPS 15",
      "price": 1299.99,
      "rating": 4.5,
      ...
   },
   {
      "id": 2,
      "name": "Samsung Galaxy S23 Ultra",
      "price": 1199.99,
      "rating": 4.8,
      ...
   },
      {
      "id": 3,
      "name": "Sony WH-1000XM5",
      "price": 399.99,
      "rating": 4.7,
      ...
  }
]
```



- Create new product
```http 
POST /api/products
```

```json
[
   {
      "name": "New Product",
      "imageUrl": "https://example.com/image.jpg",
      "descripcion": "Description...",
      "price": 599.99,
      "rating": 4.0,
      "specifications": "Tech specs..."
   }
]
   ```

- Update existing product
```http 
PUT /api/products
```

```json
[
   {
      "id": 4,
      "name": "Updated Product",
      "imageUrl": "https://example.com/updated-image.jpg",
      "descripcion": "Updated description...",
      "price": 699.99,
      "rating": 4.5,
      "specifications": "Updated tech specs..."
   }
]
   ```

- Permanently remove a product
```http 
DELETE /api/products/{id}
```
---
## 🧪 Testing
The project includes tests for both the service and controller layers:

- **Unit Tests:** Focused on `ProductService` logic and `ProductMapper` accuracy.

- **Web Layer Tests:** Verifying `ProductController` status codes and JSON payloads.

- **Integration Tests:** End-to-end flows covering API, Database, and Security filters.

### Run all tests:
```bash
mvn test
mvn surefire-report:report
````
## 🚀 Initial data loading:
This application automatically loads 3 example products at startup:

- Laptop Dell XPS 15 ($1,299.99 - Rating 4.5)
- Samsung Galaxy S23 Ultra ($1,199.99 - Rating 4.8)
- Sony WH-1000XM5 ($399.99 - Rating 4.7)
---
## 🐛 Troubleshooting
### Compilation error:
```bash
mvn clean install -U
```
### Port 8080 in use:
Modify application.properties:
`server.port=8081`

### Lombok not working in IDE:

- IntelliJ IDEA: Install Lombok plugin and enable "Annotation Processing"

File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors

Mark "Enable annotation processing"


- Eclipse: Excecute `java -jar lombok.jar` and install


### Error "Failed to load ApplicationContext":
Verify all dependencies loaded successfully:
```bash
mvn dependency:purge-local-repository
mvn clean install
```