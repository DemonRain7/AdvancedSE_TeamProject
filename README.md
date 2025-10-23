# Coupon Management System

A centralized coupon management system built with Spring Boot that allows stores to define and manage various types of coupons, and provides intelligent coupon recommendation and cart optimization features.

**Team:** Null Pointers

## Table of Contents

- [Features](#features)
- [System Architecture](#system-architecture)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [API Documentation](#api-documentation)
  - [Store Endpoints](#store-endpoints)
  - [Item Endpoints](#item-endpoints)
  - [Coupon Endpoints](#coupon-endpoints)
  - [Core Functionality Endpoints](#core-functionality-endpoints)
- [Error Codes](#error-codes)
- [Usage Examples](#usage-examples)
- [Project Structure](#project-structure)
- [Third-Party Dependencies](#third-party-dependencies)
- [AI Tools Usage](#ai-tools-usage)

## Features

### Data Models

- **Store**: Represents retail stores with unique IDs and names
- **Item**: Products available for purchase with name, price, store association, and category
- **Coupons**: Three types of discount coupons:
  - **TotalPriceCoupon**: Discount when cart total exceeds a threshold
  - **CategoryCoupon**: Discount on specific category items
  - **ItemCoupon**: Discount on a specific item

### Core Functionalities

1. **Optimal Coupon Selection**: Given a cart of items and store, identifies the coupon that provides maximum discount
2. **Store Recommendations**: Given an item keyword or category, returns optimal stores sorted by best price (including applicable coupons)
3. **Cart Optimization**: Given a cart and a TotalPriceCoupon, suggests cheapest items to add to meet the coupon threshold

### Additional Features

- Full CRUD operations for stores, items, and coupons
- Search items by keyword or category
- Filter items and coupons by store
- In-memory data storage for easy testing

## System Architecture

The application follows a layered architecture:

- **Controller Layer** (`RouteController`): REST API endpoints
- **Service Layer** (`CouponService`, `DataService`): Business logic
- **Model Layer**: Data entities (Item, Store, Coupon types)

All components are loosely coupled using dependency injection via Spring Boot.

## Technologies Used

- **Java**: 17
- **Framework**: Spring Boot 3.4.4
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito
- **Code Coverage**: JaCoCo
- **Style Checker**: CheckStyle (Google Style)
- **Static Analysis**: PMD
- **Version Control**: Git/GitHub

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+ (or use the included Maven Wrapper)
- Git (for version control)

## Building the Project

### Using Maven Wrapper (Recommended)

On Unix/macOS:
```bash
cd CouponSystem
./mvnw clean install
```

On Windows:
```cmd
cd CouponSystem
mvnw.cmd clean install
```

### Using System Maven

```bash
cd CouponSystem
mvn clean install
```

This will compile the code, run all tests, generate test coverage reports, and perform style checking and static analysis.

## Running the Application

### Using Maven

```bash
cd CouponSystem
./mvnw spring-boot:run
```

### Using Java

```bash
cd CouponSystem
./mvnw clean package
java -jar target/couponsystem-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Running Tests

### Run All Tests

```bash
cd CouponSystem
./mvnw test
```

### Run Tests with Coverage Report

```bash
cd CouponSystem
./mvnw clean test jacoco:report
```

Coverage reports will be generated in `target/site/jacoco/index.html`

### Run Style Checks

```bash
cd CouponSystem
./mvnw checkstyle:check
```

### Run Static Analysis

```bash
cd CouponSystem
./mvnw pmd:check
```

## API Documentation

All endpoints return JSON responses unless otherwise specified.

### Store Endpoints

#### Create Store
- **Endpoint**: `POST /store`
- **Request Body**:
  ```json
  {
    "name": "Tech Store"
  }
  ```
- **Success Response**: 
  - **Code**: 201 CREATED
  - **Body**: Created Store object with assigned ID
- **Error Response**:
  - **Code**: 400 BAD REQUEST
  - **Body**: Error message string

#### Get Store
- **Endpoint**: `GET /store/{id}`
- **URL Parameters**: `id` (integer) - Store ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Store object
- **Error Response**:
  - **Code**: 404 NOT FOUND
  - **Body**: "Store not found."

#### Get All Stores
- **Endpoint**: `GET /stores`
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Store objects

#### Delete Store
- **Endpoint**: `DELETE /store/{id}`
- **URL Parameters**: `id` (integer) - Store ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: "Store deleted successfully."
- **Error Response**:
  - **Code**: 404 NOT FOUND
  - **Body**: "Store not found."

---

### Item Endpoints

#### Create Item
- **Endpoint**: `POST /item`
- **Request Body**:
  ```json
  {
    "name": "Laptop",
    "price": 999.99,
    "storeId": 1,
    "category": "electronics"
  }
  ```
- **Success Response**: 
  - **Code**: 201 CREATED
  - **Body**: Created Item object with assigned ID
- **Error Response**:
  - **Code**: 400 BAD REQUEST
  - **Body**: "Store does not exist." or error message

#### Get Item
- **Endpoint**: `GET /item/{id}`
- **URL Parameters**: `id` (integer) - Item ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Item object
- **Error Response**:
  - **Code**: 404 NOT FOUND
  - **Body**: "Item not found."

#### Get All Items
- **Endpoint**: `GET /items`
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Item objects

#### Get Items by Store
- **Endpoint**: `GET /items/store/{storeId}`
- **URL Parameters**: `storeId` (integer) - Store ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Item objects from the specified store

#### Search Items by Keyword
- **Endpoint**: `GET /items/search?keyword={keyword}`
- **Query Parameters**: `keyword` (string) - Search term
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Item objects containing the keyword

#### Get Items by Category
- **Endpoint**: `GET /items/category/{category}`
- **URL Parameters**: `category` (string) - Category name
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Item objects in the specified category

#### Delete Item
- **Endpoint**: `DELETE /item/{id}`
- **URL Parameters**: `id` (integer) - Item ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: "Item deleted successfully."
- **Error Response**:
  - **Code**: 404 NOT FOUND
  - **Body**: "Item not found."

---

### Coupon Endpoints

#### Create Coupon
- **Endpoint**: `POST /coupon`
- **Request Body** (varies by type):

  **TotalPriceCoupon**:
  ```json
  {
    "type": "totalprice",
    "storeId": 1,
    "discountValue": 10.0,
    "isPercentage": true,
    "minimumPurchase": 50.0
  }
  ```

  **CategoryCoupon**:
  ```json
  {
    "type": "category",
    "storeId": 1,
    "discountValue": 5.0,
    "isPercentage": false,
    "category": "books"
  }
  ```

  **ItemCoupon**:
  ```json
  {
    "type": "item",
    "storeId": 1,
    "discountValue": 15.0,
    "isPercentage": true,
    "targetItemId": 1
  }
  ```
- **Success Response**: 
  - **Code**: 201 CREATED
  - **Body**: Created Coupon object with assigned ID
- **Error Response**:
  - **Code**: 400 BAD REQUEST
  - **Body**: "Invalid coupon type." or error message

#### Get Coupon
- **Endpoint**: `GET /coupon/{id}`
- **URL Parameters**: `id` (integer) - Coupon ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Coupon object
- **Error Response**:
  - **Code**: 404 NOT FOUND
  - **Body**: "Coupon not found."

#### Get All Coupons
- **Endpoint**: `GET /coupons`
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Coupon objects

#### Get Coupons by Store
- **Endpoint**: `GET /coupons/store/{storeId}`
- **URL Parameters**: `storeId` (integer) - Store ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of Coupon objects from the specified store

#### Delete Coupon
- **Endpoint**: `DELETE /coupon/{id}`
- **URL Parameters**: `id` (integer) - Coupon ID
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: "Coupon deleted successfully."
- **Error Response**:
  - **Code**: 404 NOT FOUND
  - **Body**: "Coupon not found."

---

### Core Functionality Endpoints

#### Find Optimal Coupon for Cart
- **Endpoint**: `POST /cart/optimal-coupon`
- **Description**: Identifies the coupon that provides maximum discount for a given cart
- **Request Body**:
  ```json
  {
    "itemIds": [1, 2, 3],
    "storeId": 1
  }
  ```
- **Success Response**: 
  - **Code**: 200 OK
  - **Body** (when coupon found):
    ```json
    {
      "coupon": { /* Coupon object */ },
      "discount": 15.50
    }
    ```
  - **Body** (when no coupon applies):
    ```json
    "No applicable coupon found."
    ```
- **Error Response**:
  - **Code**: 400 BAD REQUEST
  - **Body**: Error message

**Important Notes**:
- All items must exist in the system
- Only coupons from the specified store are considered
- Only one coupon can be applied at a time

#### Find Optimal Stores for Item Search
- **Endpoint**: `GET /stores/optimal?keyword={keyword}&category={category}`
- **Description**: Returns stores sorted by best price (including coupons) for items matching search criteria
- **Query Parameters**: 
  - `keyword` (string, optional) - Search term for item names
  - `category` (string, optional) - Category filter
  - **Note**: At least one parameter must be provided
- **Success Response**: 
  - **Code**: 200 OK
  - **Body**: Array of StoreRecommendation objects:
    ```json
    [
      {
        "store": { /* Store object */ },
        "item": { /* Cheapest matching item */ },
        "coupon": { /* Best applicable coupon or null */ },
        "finalPrice": 45.50,
        "discount": 5.00
      }
    ]
    ```
- **Error Response**:
  - **Code**: 400 BAD REQUEST
  - **Body**: "Either keyword or category must be provided."

**Important Notes**:
- Results are sorted by `finalPrice` in ascending order
- Assumes intent to buy one of any matching items
- If no applicable coupon exists, `coupon` will be null and `discount` will be 0

#### Suggest Items to Meet Coupon Threshold
- **Endpoint**: `POST /cart/suggest-items`
- **Description**: Suggests cheapest items to add to cart to meet a TotalPriceCoupon's minimum purchase requirement
- **Request Body**:
  ```json
  {
    "itemIds": [1, 2],
    "storeId": 1,
    "couponId": 1
  }
  ```
- **Success Response**: 
  - **Code**: 200 OK
  - **Body** (when suggestions available):
    ```json
    [
      { /* Item object */ },
      { /* Item object */ }
    ]
    ```
  - **Body** (when threshold already met or coupon invalid):
    ```json
    "Either cart already meets threshold, or coupon is invalid."
    ```
- **Error Response**:
  - **Code**: 400 BAD REQUEST
  - **Body**: Error message

**Important Notes**:
- Only works with TotalPriceCoupon type
- Coupon must belong to the specified store
- Returns cheapest combination of items to reach threshold
- If cart already meets threshold, returns empty list

---

## Error Codes

| HTTP Code | Description |
|-----------|-------------|
| 200 OK | Request successful |
| 201 CREATED | Resource created successfully |
| 400 BAD REQUEST | Invalid request data or business rule violation |
| 404 NOT FOUND | Requested resource does not exist |
| 500 INTERNAL SERVER ERROR | Unexpected server error |

## Usage Examples

### Example 1: Create Store and Add Items

```bash
# Create a store
curl -X POST http://localhost:8080/store \
  -H "Content-Type: application/json" \
  -d '{"name": "BookMart"}'

# Response: {"id": 1, "name": "BookMart"}

# Add items
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Java Programming",
    "price": 45.99,
    "storeId": 1,
    "category": "books"
  }'
```

### Example 2: Create and Use Coupons

```bash
# Create a total price coupon (10% off when spending $50+)
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{
    "type": "totalprice",
    "storeId": 1,
    "discountValue": 10.0,
    "isPercentage": true,
    "minimumPurchase": 50.0
  }'

# Find optimal coupon for cart
curl -X POST http://localhost:8080/cart/optimal-coupon \
  -H "Content-Type: application/json" \
  -d '{
    "itemIds": [1, 2, 3],
    "storeId": 1
  }'
```

### Example 3: Find Best Stores

```bash
# Search for best stores selling books
curl "http://localhost:8080/stores/optimal?category=books"
```

### Example 4: Get Items to Meet Coupon Threshold

```bash
# Get suggestions to reach $50 minimum
curl -X POST http://localhost:8080/cart/suggest-items \
  -H "Content-Type: application/json" \
  -d '{
    "itemIds": [1],
    "storeId": 1,
    "couponId": 1
  }'
```

## Project Structure

```
CouponSystem/
├── src/
│   ├── main/
│   │   ├── java/org/nullpointers/couponsystem/
│   │   │   ├── CouponSystemApplication.java    # Main application class
│   │   │   ├── controller/
│   │   │   │   └── RouteController.java        # REST API endpoints
│   │   │   ├── model/
│   │   │   │   ├── Item.java                   # Item entity
│   │   │   │   ├── Store.java                  # Store entity
│   │   │   │   ├── Coupon.java                 # Abstract coupon base
│   │   │   │   ├── TotalPriceCoupon.java       # Total price coupon
│   │   │   │   ├── CategoryCoupon.java         # Category coupon
│   │   │   │   └── ItemCoupon.java             # Item-specific coupon
│   │   │   └── service/
│   │   │       ├── DataService.java            # Data management service
│   │   │       └── CouponService.java          # Business logic service
│   │   └── resources/
│   │       └── application.properties          # Application configuration
│   └── test/
│       └── java/org/nullpointers/couponsystem/
│           ├── model/                           # Model unit tests
│           ├── service/                         # Service unit tests (with mocking)
│           └── controller/                      # Controller unit tests
├── pom.xml                                      # Maven configuration
└── README.md                                    # This file
```

## Third-Party Dependencies

All third-party dependencies are managed via Maven and specified in `pom.xml`. Key dependencies include:

- **Spring Boot Starter Web** (org.springframework.boot:spring-boot-starter-web:3.4.4)
  - Source: Maven Central
  - Purpose: Web application framework and REST API support
  
- **Spring Boot Starter Test** (org.springframework.boot:spring-boot-starter-test:3.4.4)
  - Source: Maven Central
  - Purpose: Testing framework including JUnit 5 and Mockito

- **JaCoCo Maven Plugin** (org.jacoco:jacoco-maven-plugin:0.8.11)
  - Source: Maven Central
  - Purpose: Code coverage reporting

- **Maven Checkstyle Plugin** (org.apache.maven.plugins:maven-checkstyle-plugin:3.2.0)
  - Source: Maven Central
  - Purpose: Code style checking (Google Java Style)

- **Maven PMD Plugin** (org.apache.maven.plugins:maven-pmd-plugin:3.21.2)
  - Source: Maven Central
  - Purpose: Static code analysis

No third-party code is directly included in the repository. All dependencies are fetched automatically by Maven during the build process.

## Important Notes

### Coupon Usage Rules

1. **No Stacking**: Only one coupon can be applied to a cart at a time
2. **Store-Specific**: Coupons only apply to items from their designated store
3. **Discount Limits**: Fixed-amount discounts never exceed the applicable subtotal

### API Call Order

- **Stores must be created before items**: Items require a valid `storeId`
- **Items must exist before creating item-specific coupons**: ItemCoupon requires a valid `targetItemId`
- **Core functionality endpoints require existing data**: Ensure stores, items, and coupons are created first

### Data Persistence

- Data is stored **in-memory only**
- All data is lost when the application restarts
- Suitable for development and testing; production use would require a database

## AI Tools Usage

Our team utilized AI-assisted development tools to enhance productivity and code quality throughout the project development process.

### AI Tools Used

**Claude AI Assistant (via Cursor IDE)**
- **Source**: Free AI assistant integrated with Cursor IDE
- **Usage**: Code generation, debugging, documentation, test-case coverage improvements and architectural guidance
- **Cost**: No cost - provided free through educational Cursor IDE access
- **Primary Use Cases**:
  - Initial project structure setup and Spring Boot configuration
  - Code review and optimization suggestions
  - Documentation generation and improvement
  - Test case development and debugging assistance

### AI-Generated Code Sections

The following sections of code were generated with AI assistance:

**Model Classes** (`src/main/java/org/nullpointers/couponsystem/model/`)
- Structures of models, and interactions between the models

**Service Layer** (`src/main/java/org/nullpointers/couponsystem/service/`)
- `DataService.java` - CRUD operations with AI-generated error handling
- `CouponService.java` - Business logic algorithms with AI-assisted optimization

**Controller Layer** (`src/main/java/org/nullpointers/couponsystem/controller/`)
- `RouteController.java` - REST endpoint implementations with AI-generated response handling

**Test Suite** (`src/test/java/org/nullpointers/couponsystem/`)
- Exploration of tricky test classes, i.o.w, all those edge scenarios, helping us improve branch coverage

### AI-Assisted Documentation

- README.md structure and content organization
- API documentation and usage examples
- Code comments and inline documentation
- Team collaboration guide development

### Development Process

1. **Initial Setup**: AI assisted with Spring Boot project structure and Maven configuration
2. **Code Development**: AI provided code templates and implementation suggestions
3. **Testing**: AI helped generate comprehensive test cases and edge case scenarios
4. **Documentation**: AI assisted with documentation structure and content creation
5. **Code Review**: AI provided suggestions for code optimization and best practices

### Quality Assurance

All AI-generated code was thoroughly reviewed by team members, tested extensively, and manually validated to ensure:
- Code quality and adherence to Java best practices
- Proper error handling and edge case coverage
- Compliance with project requirements and specifications
- Integration with existing codebase architecture

### Educational Value

The use of AI tools enhanced our learning experience by:
- Providing immediate feedback on code quality
- Suggesting alternative implementation approaches
- Helping identify potential bugs and edge cases
- Accelerating development while maintaining code quality standards

## Contributing

This is a course project for the Null Pointers team. All code follows standard Java conventions with comprehensive documentation and testing. AI assistance was used throughout the development process to enhance productivity and code quality.

## License

This project is for educational purposes only.

