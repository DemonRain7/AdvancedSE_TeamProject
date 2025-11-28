# Coupon Management System - Client Application

This directory contains a demonstration client application for the Coupon Management System. The client simulates a local flower shop that uses the centralized coupon management service.

## What the Client Does

The **FlowerShopClient** is a Java command-line application that demonstrates how a local retail store (in this case, a flower shop) can use the Coupon Management System to:

1. **Register their store** with the service
2. **Add products** to their inventory (flowers, plants, accessories)
3. **Create promotional coupons** (total price discounts, category discounts, item-specific discounts)
4. **Find optimal coupons** for customer shopping carts
5. **Get store recommendations** for customers searching for products
6. **Optimize shopping carts** to meet coupon thresholds

The client demonstrates all major API functionalities in a realistic business scenario.

## Building the Client

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven 3.6+ (or use the Maven Wrapper from the parent project)
- The Coupon Management Service must be running (see main README)

### Build Instructions

Navigate to the Client directory and build using Maven:

```bash
cd Client
mvn clean package
```

This will:
- Download all dependencies
- Compile the source code
- Create an executable JAR file in the `target` directory

## Running the Client

### Step 1: Start the Coupon Management Service

First, ensure the service is running. In a separate terminal:

```bash
cd CouponSystem
./mvnw spring-boot:run
```

Wait until you see: `Started CouponSystemApplication in X seconds`

### Step 2: Run the Client

In the Client directory, run:

```bash
java -jar target/coupon-client-1.0.0-jar-with-dependencies.jar
```


### Connecting to a Different Service URL

By default, the client connects to `http://localhost:8080`. To connect to a different URL:

```bash
java -jar target/coupon-client-1.0.0.jar http://your-service-url:port
```

## How Multiple Client Instances Work

The Coupon Management Service supports multiple client instances running simultaneously by using **store IDs** to distinguish between different clients.

### Client Identification Mechanism

1. **Each client registers a unique store**: When a client starts, it registers its store with the service by calling `POST /store` with a store name.

2. **Service assigns a unique store ID**: The service assigns a unique integer ID to each store and returns it to the client.

3. **All subsequent operations use store ID**: The client includes its store ID in all API calls for items, coupons, and cart operations.

4. **Data isolation by store ID**: The service ensures that:
   - Each client can only see and modify their own store's items and coupons
   - Cart operations only consider coupons from the specified store
   - Store recommendations are calculated independently for each store

### Running Multiple Client Instances

To demonstrate multiple clients running simultaneously:

**Terminal 1 - Start the service:**
```bash
cd CouponSystem
./mvnw spring-boot:run
```

**Terminal 2 - Run first client (Flower Shop):**
```bash
cd Client
java -jar target/coupon-client-1.0.0-jar-with-dependencies.jar
```

**Terminal 3 - Run second client (modify the client to use a different store name):**
```bash
cd Client
java -jar target/coupon-client-1.0.0-jar-with-dependencies.jar
```

Each client instance will:
- Get a unique store ID from the service
- Manage its own inventory and coupons
- Not interfere with other clients' data

### Data Isolation Example

If Client A (Flower Shop, store ID: 1) creates items and coupons, Client B (Electronics Store, store ID: 2) cannot access or modify Client A's data. The service uses the store ID in API endpoints like:

- `GET /items/store/{storeId}` - Only returns items for the specified store
- `GET /coupons/store/{storeId}` - Only returns coupons for the specified store
- `POST /cart/optimal-coupon` with `storeId` - Only considers coupons from that store

This design ensures that multiple independent businesses can safely use the same centralized service without data conflicts.

## Client Architecture

The client consists of two main classes:

### ApiClient.java
A low-level HTTP client wrapper that:
- Handles all HTTP communication with the service
- Provides methods for each API endpoint
- Uses Apache HttpClient 5 for HTTP requests
- Uses Gson for JSON serialization/deserialization

### FlowerShopClient.java
The main application that:
- Implements business logic for a flower shop
- Demonstrates realistic usage scenarios
- Provides a user-friendly demonstration flow
- Shows how to use the ApiClient to interact with the service

## Dependencies

The client uses the following dependencies (managed via Maven):

- **Apache HttpClient 5** (5.2.1) - For HTTP requests
- **Gson** (2.10.1) - For JSON processing
- **JUnit Jupiter** (5.9.3) - For testing

All dependencies are automatically downloaded by Maven during the build process.

## Troubleshooting

### Connection Refused Error
**Problem**: `Connection refused` when running the client.

**Solution**: Ensure the Coupon Management Service is running at `http://localhost:8080`. Check that no firewall is blocking the connection.

### Build Failures
**Problem**: Maven build fails with dependency errors.

**Solution**:
- Ensure you have internet connectivity for Maven to download dependencies
- Try running `mvn clean install` to force a fresh build
- Check that you're using Java 17 or higher: `java -version`

### Service Not Found Errors
**Problem**: Client runs but returns 404 errors.

**Solution**: Verify the service is running and accessible:
```bash
curl http://localhost:8080/
```
You should see: "Welcome to the Coupon Management System!"

## Extending the Client

To create your own client application:

1. Use `ApiClient.java` as a reference for calling the service API
2. Implement your business logic in a new class similar to `FlowerShopClient.java`
3. Configure your store name and products according to your business
4. Build and run using the same Maven commands

## License

This project is for educational purposes only.
