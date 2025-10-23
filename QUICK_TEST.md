# Quick Test Commands

Quick reference for testing the Coupon Management System API.

## Start the Application

```bash
cd CouponSystem && ./mvnw spring-boot:run
```

Wait for: `Started CouponSystemApplication in X seconds`

## Run Full Test Suite

```bash
./test-api.sh
```

## Quick Manual Test Sequence

Copy and paste these commands in order:

### 1. Create a Store
```bash
curl -X POST http://localhost:8080/store \
  -H "Content-Type: application/json" \
  -d '{"name": "BookMart"}'
```

### 2. Create Items (use storeId from step 1, likely 1)
```bash
curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Java Programming", "price": 45.99, "storeId": 1, "category": "books"}'

curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Python Basics", "price": 39.99, "storeId": 1, "category": "books"}'

curl -X POST http://localhost:8080/item \
  -H "Content-Type: application/json" \
  -d '{"name": "Data Structures", "price": 55.00, "storeId": 1, "category": "books"}'
```

### 3. Create Coupons
```bash
# 10% off when spending $50+
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"type": "totalprice", "storeId": 1, "discountValue": 10.0, "isPercentage": true, "minimumPurchase": 50.0}'

# $5 off books
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"type": "category", "storeId": 1, "discountValue": 5.0, "isPercentage": false, "category": "books"}'

# 15% off item 1 (Java Programming)
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{"type": "item", "storeId": 1, "discountValue": 15.0, "isPercentage": true, "targetItemId": 1}'
```

### 4. Test Core Functions

**Find Best Coupon for Cart:**
```bash
curl -X POST http://localhost:8080/cart/optimal-coupon \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [1, 2, 3], "storeId": 1}'
```

**Find Optimal Stores for Books:**
```bash
curl "http://localhost:8080/stores/optimal?category=books"
```

**Get Items to Meet Coupon Threshold:**
```bash
curl -X POST http://localhost:8080/cart/suggest-items \
  -H "Content-Type: application/json" \
  -d '{"itemIds": [2], "storeId": 1, "couponId": 1}'
```

## View All Data

```bash
# All stores
curl http://localhost:8080/stores

# All items
curl http://localhost:8080/items

# All coupons
curl http://localhost:8080/coupons
```

## Pretty Print with jq

Add `| jq '.'` to any command for formatted output:

```bash
curl http://localhost:8080/stores | jq '.'
```

## Test Results Explained

### Optimal Coupon Response
```json
{
  "coupon": { /* Coupon object with details */ },
  "discount": 14.098  // Amount you save
}
```

### Store Recommendation Response
```json
[
  {
    "store": {"id": 1, "name": "BookMart"},
    "item": {"id": 2, "name": "Python Basics", "price": 39.99, ...},
    "coupon": { /* Best coupon or null */ },
    "finalPrice": 34.99,  // Price after discount
    "discount": 5.00      // Amount saved
  }
]
```

### Suggest Items Response
```json
[
  {"id": 4, "name": "Bookmark", "price": 8.50, ...}
  // Items to add to reach threshold
]
```

