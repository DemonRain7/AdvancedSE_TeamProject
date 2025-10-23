# API Testing Guide

This guide provides curl commands to test all functionalities of the Coupon Management System.

## Prerequisites

1. Start the application:
   ```bash
   cd CouponSystem
   ./mvnw spring-boot:run
   ```

2. The application will be available at `http://localhost:8080`

## Quick Test Script

Run the automated test script that covers all functionalities:

```bash
./test-api.sh
```

This script will:
- Create 3 stores
- Add 8 items across stores
- Create 4 different types of coupons
- Test all core functionalities
- Display results in formatted JSON

## Manual Testing Commands

### 1. Setup: Create Stores

```bash
# Create BookMart
curl -X POST http://localhost:8080/store \
  -H "Content-Type: application/json" \
  -d '{"name": "BookMart"}'

# Expected Response: {"id":1,"name":"BookMart"}

# Create TechWorld
curl -X POST http://localhost:8080/store \
  -H "Content-Type: application/json" \
  -d '{"name": "TechWorld"}'

# Expected Response: {"id":2,"name":"TechWorld"}
```

### 2. Setup: Create Items

```bash
# Add Java Programming book to BookMart (storeId: 1)
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Java Programming", "price": 45.99, "storeId": 1, "category": "books"}'

# Expected Response: {"id":1,"name":"Java Programming","price":45.99,"storeId":1,"category":"books"}

# Add Python Basics book to BookMart
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Python Basics", "price": 39.99, "storeId": 1, "category": "books"}'

# Add Data Structures book to BookMart
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Data Structures", "price": 55.00, "storeId": 1, "category": "books"}'

# Add Bookmark Set to BookMart
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Bookmark Set", "price": 8.50, "storeId": 1, "category": "accessories"}'

# Add Laptop to TechWorld (storeId: 2)
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "price": 899.99, "storeId": 2, "category": "electronics"}'
```

### 3. Setup: Create Coupons

```bash
# Create TotalPriceCoupon: 10% off when spending $50+
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"type": "totalprice", "storeId": 1, "discountValue": 10.0, "isPercentage": true, "minimumPurchase": 50.0}'

# Expected Response: {"id":1,"storeId":1,"discountValue":10.0,"percentage":true,"minimumPurchase":50.0}

# Create CategoryCoupon: $5 off books
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"type": "category", "storeId": 1, "discountValue": 5.0, "isPercentage": false, "category": "books"}'

# Create ItemCoupon: 15% off Java Programming (itemId: 1)
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"type": "item", "storeId": 1, "discountValue": 15.0, "isPercentage": true, "targetItemId": 1}'
```

### 4. Core Functionality: Find Optimal Coupon

Test finding the best coupon for a cart of items:

```bash
# Cart with 3 books (total: $140.98)
# Available coupons: 10% off $50+, $5 off books, 15% off Java
# Expected: 10% off (saves ~$14.10) is optimal
curl -X POST http://localhost:8080/cart/optimal-coupon \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [1, 2, 3], "storeId": 1}'

# Expected Response:
# {
#   "coupon": { /* TotalPriceCoupon object */ },
#   "discount": 14.098
# }
```

```bash
# Cart with only Java Programming (price: $45.99)
# Available coupons: 15% off Java (saves ~$6.90), $5 off books (saves $5)
# Expected: 15% off Java is optimal
curl -X POST http://localhost:8080/cart/optimal-coupon \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [1], "storeId": 1}'

# Expected Response:
# {
#   "coupon": { /* ItemCoupon for Java */ },
#   "discount": 6.8985
# }
```

### 5. Core Functionality: Find Optimal Stores

Search for best stores to buy items by category or keyword:

```bash
# Search for stores selling "books" category
curl "http://localhost:8080/stores/optimal?category=books"

# Expected Response: Array of store recommendations sorted by price
# [
#   {
#     "store": {"id":1,"name":"BookMart"},
#     "item": { /* cheapest book */ },
#     "coupon": { /* best applicable coupon */ },
#     "finalPrice": 33.99,  // after discount
#     "discount": 6.0
#   }
# ]
```

```bash
# Search for stores selling items with keyword "laptop"
curl "http://localhost:8080/stores/optimal?keyword=laptop"

# Expected Response: Stores selling laptops, sorted by best price
```

### 6. Core Functionality: Suggest Items to Meet Threshold

Find cheapest items to add to cart to qualify for a coupon:

```bash
# Current cart: Python Basics ($39.99)
# Coupon 1 requires $50+ to activate
# Need $10.01 more
curl -X POST http://localhost:8080/cart/suggest-items \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [2], "storeId": 1, "couponId": 1}'

# Expected Response: Array of cheapest items to add
# [
#   {"id":4,"name":"Bookmark Set","price":8.50,"storeId":1,"category":"accessories"}
#   // ... more items until threshold is met
# ]
```

```bash
# Cart that already meets threshold
# Should return empty or message
curl -X POST http://localhost:8080/cart/suggest-items \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [1, 3], "storeId": 1, "couponId": 1}'

# Expected Response: "Either cart already meets threshold, or coupon is invalid."
```

### 7. Additional CRUD Operations

```bash
# Get all stores
curl http://localhost:8080/stores

# Get specific store
curl http://localhost:8080/store/1

# Get all items
curl http://localhost:8080/items

# Get items by store
curl http://localhost:8080/items/store/1

# Search items by keyword
curl "http://localhost:8080/items/search?keyword=java"

# Get items by category
curl http://localhost:8080/items/category/books

# Get all coupons
curl http://localhost:8080/coupons

# Get coupons by store
curl http://localhost:8080/coupons/store/1

# Get specific coupon
curl http://localhost:8080/coupon/1

# Delete an item
curl -X DELETE http://localhost:8080/item/1

# Delete a store
curl -X DELETE http://localhost:8080/store/1

# Delete a coupon
curl -X DELETE http://localhost:8080/coupon/1
```

## Testing Scenarios

### Scenario 1: Budget Shopper
**Goal**: Find the cheapest way to buy books

1. Search for optimal stores selling books:
   ```bash
   curl "http://localhost:8080/stores/optimal?category=books"
   ```

2. Add items to cart and find best coupon:
   ```bash
   curl -X POST http://localhost:8080/cart/optimal-coupon \
     -H "Content-Type: application/json" \
     -d '{"itemIds": [1, 2], "storeId": 1}'
   ```

3. Check if adding more items qualifies for better coupon:
   ```bash
   curl -X POST http://localhost:8080/cart/suggest-items \
     -H "Content-Type: application/json" \
     -d '{"itemIds": [1, 2], "storeId": 1, "couponId": 1}'
   ```

### Scenario 2: Coupon Maximization
**Goal**: Get the biggest discount possible

1. Create cart with multiple items
2. Find optimal coupon:
   ```bash
   curl -X POST http://localhost:8080/cart/optimal-coupon \
     -H "Content-Type: application/json" \
     -d '{"itemIds": [1, 2, 3], "storeId": 1}'
   ```

3. Compare discount amounts from response

### Scenario 3: Cross-Store Shopping
**Goal**: Find best deals across multiple stores

1. Search for electronics:
   ```bash
   curl "http://localhost:8080/stores/optimal?category=electronics"
   ```

2. Search for books:
   ```bash
   curl "http://localhost:8080/stores/optimal?category=books"
   ```

3. Compare final prices across stores

## Tips for Testing

1. **Use jq for formatted output**: Pipe curl output through `jq '.'` for pretty JSON:
   ```bash
   curl http://localhost:8080/stores | jq '.'
   ```

2. **Save IDs for reuse**: When creating entities, save their IDs:
   ```bash
   STORE_ID=$(curl -s -X POST http://localhost:8080/store \
     -H "Content-Type: application/json" \
     -d '{"name": "TestStore"}' | jq -r '.id')
   ```

3. **Check responses**: Always verify the HTTP status code and response body

4. **Run automated script**: For comprehensive testing, use:
   ```bash
   ./test-api.sh
   ```

## Expected Behavior

- **Coupon Selection**: System always picks the coupon with maximum discount
- **No Coupon Stacking**: Only one coupon can be applied per cart
- **Store-Specific**: Coupons only apply to items from their designated store
- **Smart Suggestions**: Item suggestions always choose cheapest options to meet thresholds
- **Sorted Results**: Store recommendations are always sorted by best final price (ascending)

