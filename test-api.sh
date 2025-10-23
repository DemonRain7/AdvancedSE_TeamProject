#!/bin/bash

# Coupon Management System - API Test Script
# This script demonstrates all main functionalities with curl commands
# Start the application first: cd CouponSystem && ./mvnw spring-boot:run

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Coupon Management System - API Testing"
echo "=========================================="
echo ""

# Test the index endpoint
echo "1. Testing Index Endpoint..."
curl -s "$BASE_URL/" | head -c 100
echo ""
echo ""

# ==================== SETUP: Create Stores ====================
echo "2. Creating Stores..."
echo "-------------------------------------------"

echo "Creating BookMart..."
STORE1=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "BookMart"}')
echo "$STORE1" | jq '.'
STORE1_ID=$(echo "$STORE1" | jq -r '.id')
echo ""

echo "Creating TechWorld..."
STORE2=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "TechWorld"}')
echo "$STORE2" | jq '.'
STORE2_ID=$(echo "$STORE2" | jq -r '.id')
echo ""

echo "Creating ToyLand..."
STORE3=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "ToyLand"}')
echo "$STORE3" | jq '.'
STORE3_ID=$(echo "$STORE3" | jq -r '.id')
echo ""

# ==================== SETUP: Create Items ====================
echo "3. Creating Items..."
echo "-------------------------------------------"

# BookMart items
echo "Adding items to BookMart..."
ITEM1=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Java Programming\", \"price\": 45.99, \"storeId\": $STORE1_ID, \"category\": \"books\"}")
echo "$ITEM1" | jq '.'
ITEM1_ID=$(echo "$ITEM1" | jq -r '.id')

ITEM2=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Python Basics\", \"price\": 39.99, \"storeId\": $STORE1_ID, \"category\": \"books\"}")
echo "$ITEM2" | jq '.'
ITEM2_ID=$(echo "$ITEM2" | jq -r '.id')

ITEM3=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Data Structures\", \"price\": 55.00, \"storeId\": $STORE1_ID, \"category\": \"books\"}")
echo "$ITEM3" | jq '.'
ITEM3_ID=$(echo "$ITEM3" | jq -r '.id')

ITEM4=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Bookmark Set\", \"price\": 8.50, \"storeId\": $STORE1_ID, \"category\": \"accessories\"}")
echo "$ITEM4" | jq '.'
ITEM4_ID=$(echo "$ITEM4" | jq -r '.id')
echo ""

# TechWorld items
echo "Adding items to TechWorld..."
ITEM5=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Wireless Mouse\", \"price\": 29.99, \"storeId\": $STORE2_ID, \"category\": \"electronics\"}")
echo "$ITEM5" | jq '.'
ITEM5_ID=$(echo "$ITEM5" | jq -r '.id')

ITEM6=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Laptop\", \"price\": 899.99, \"storeId\": $STORE2_ID, \"category\": \"electronics\"}")
echo "$ITEM6" | jq '.'
ITEM6_ID=$(echo "$ITEM6" | jq -r '.id')

ITEM7=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"USB Cable\", \"price\": 12.99, \"storeId\": $STORE2_ID, \"category\": \"accessories\"}")
echo "$ITEM7" | jq '.'
ITEM7_ID=$(echo "$ITEM7" | jq -r '.id')
echo ""

# ToyLand items
echo "Adding items to ToyLand..."
ITEM8=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Building Blocks\", \"price\": 34.99, \"storeId\": $STORE3_ID, \"category\": \"toys\"}")
echo "$ITEM8" | jq '.'
ITEM8_ID=$(echo "$ITEM8" | jq -r '.id')
echo ""

# ==================== SETUP: Create Coupons ====================
echo "4. Creating Coupons..."
echo "-------------------------------------------"

# TotalPriceCoupon for BookMart: 10% off when spending $50+
echo "Creating TotalPriceCoupon for BookMart (10% off $50+)..."
COUPON1=$(curl -s -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE1_ID, \"discountValue\": 10.0, \"isPercentage\": true, \"minimumPurchase\": 50.0}")
echo "$COUPON1" | jq '.'
COUPON1_ID=$(echo "$COUPON1" | jq -r '.id')
echo ""

# CategoryCoupon for BookMart: $5 off books
echo "Creating CategoryCoupon for BookMart (\$5 off books)..."
COUPON2=$(curl -s -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"category\", \"storeId\": $STORE1_ID, \"discountValue\": 5.0, \"isPercentage\": false, \"category\": \"books\"}")
echo "$COUPON2" | jq '.'
COUPON2_ID=$(echo "$COUPON2" | jq -r '.id')
echo ""

# ItemCoupon for BookMart: 15% off Java Programming book
echo "Creating ItemCoupon for BookMart (15% off Java Programming)..."
COUPON3=$(curl -s -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"item\", \"storeId\": $STORE1_ID, \"discountValue\": 15.0, \"isPercentage\": true, \"targetItemId\": $ITEM1_ID}")
echo "$COUPON3" | jq '.'
COUPON3_ID=$(echo "$COUPON3" | jq -r '.id')
echo ""

# TotalPriceCoupon for TechWorld: $50 off when spending $500+
echo "Creating TotalPriceCoupon for TechWorld (\$50 off \$500+)..."
COUPON4=$(curl -s -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE2_ID, \"discountValue\": 50.0, \"isPercentage\": false, \"minimumPurchase\": 500.0}")
echo "$COUPON4" | jq '.'
COUPON4_ID=$(echo "$COUPON4" | jq -r '.id')
echo ""

# ==================== CORE FUNCTIONALITY TESTS ====================
echo ""
echo "=========================================="
echo "TESTING CORE FUNCTIONALITIES"
echo "=========================================="
echo ""

# Test 1: Find Optimal Coupon for Cart
echo "5. TEST: Find Optimal Coupon for Cart"
echo "-------------------------------------------"
echo "Cart: Java Programming (\$45.99) + Python Basics (\$39.99) + Data Structures (\$55.00)"
echo "Available coupons:"
echo "  - 10% off \$50+ (total price)"
echo "  - \$5 off books (category)"
echo "  - 15% off Java Programming (item)"
echo ""
curl -s -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID, $ITEM2_ID, $ITEM3_ID], \"storeId\": $STORE1_ID}" | jq '.'
echo ""
echo ""

# Test 2: Find Optimal Coupon (smaller cart)
echo "6. TEST: Find Optimal Coupon for Smaller Cart"
echo "-------------------------------------------"
echo "Cart: Java Programming (\$45.99) only"
echo "Expected: ItemCoupon (15% off Java) should be optimal"
echo ""
curl -s -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID], \"storeId\": $STORE1_ID}" | jq '.'
echo ""
echo ""

# Test 3: Find Optimal Stores by Category
echo "7. TEST: Find Optimal Stores for Category Search"
echo "-------------------------------------------"
echo "Searching for: category='books'"
echo "Should return BookMart with applicable coupons"
echo ""
curl -s "$BASE_URL/stores/optimal?category=books" | jq '.'
echo ""
echo ""

# Test 4: Find Optimal Stores by Keyword
echo "8. TEST: Find Optimal Stores for Keyword Search"
echo "-------------------------------------------"
echo "Searching for: keyword='programming'"
echo "Should return BookMart with Java Programming book"
echo ""
curl -s "$BASE_URL/stores/optimal?keyword=programming" | jq '.'
echo ""
echo ""

# Test 5: Suggest Items to Meet Coupon Threshold
echo "9. TEST: Suggest Items to Meet Coupon Threshold"
echo "-------------------------------------------"
echo "Current cart: Python Basics (\$39.99)"
echo "Coupon: 10% off when spending \$50+"
echo "Need: \$10.01 more to qualify"
echo "Should suggest: Bookmark Set (\$8.50) or other cheap items"
echo ""
curl -s -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM2_ID], \"storeId\": $STORE1_ID, \"couponId\": $COUPON1_ID}" | jq '.'
echo ""
echo ""

# Test 6: Cart already meets threshold
echo "10. TEST: Cart Already Meets Threshold"
echo "-------------------------------------------"
echo "Current cart: Java Programming (\$45.99) + Data Structures (\$55.00) = \$100.99"
echo "Coupon threshold: \$50"
echo "Should return: Empty list or message that threshold is met"
echo ""
curl -s -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID, $ITEM3_ID], \"storeId\": $STORE1_ID, \"couponId\": $COUPON1_ID}" | jq '.'
echo ""
echo ""

# ==================== ADDITIONAL CRUD TESTS ====================
echo ""
echo "=========================================="
echo "TESTING ADDITIONAL CRUD OPERATIONS"
echo "=========================================="
echo ""

# Test: Get all stores
echo "11. TEST: Get All Stores"
echo "-------------------------------------------"
curl -s "$BASE_URL/stores" | jq '.'
echo ""
echo ""

# Test: Get specific store
echo "12. TEST: Get Specific Store (BookMart)"
echo "-------------------------------------------"
curl -s "$BASE_URL/store/$STORE1_ID" | jq '.'
echo ""
echo ""

# Test: Get all items
echo "13. TEST: Get All Items"
echo "-------------------------------------------"
curl -s "$BASE_URL/items" | jq '.'
echo ""
echo ""

# Test: Get items by store
echo "14. TEST: Get Items by Store (BookMart)"
echo "-------------------------------------------"
curl -s "$BASE_URL/items/store/$STORE1_ID" | jq '.'
echo ""
echo ""

# Test: Search items by keyword
echo "15. TEST: Search Items by Keyword ('python')"
echo "-------------------------------------------"
curl -s "$BASE_URL/items/search?keyword=python" | jq '.'
echo ""
echo ""

# Test: Get items by category
echo "16. TEST: Get Items by Category ('electronics')"
echo "-------------------------------------------"
curl -s "$BASE_URL/items/category/electronics" | jq '.'
echo ""
echo ""

# Test: Get all coupons
echo "17. TEST: Get All Coupons"
echo "-------------------------------------------"
curl -s "$BASE_URL/coupons" | jq '.'
echo ""
echo ""

# Test: Get coupons by store
echo "18. TEST: Get Coupons by Store (BookMart)"
echo "-------------------------------------------"
curl -s "$BASE_URL/coupons/store/$STORE1_ID" | jq '.'
echo ""
echo ""

# Test: Get specific coupon
echo "19. TEST: Get Specific Coupon (TotalPriceCoupon)"
echo "-------------------------------------------"
curl -s "$BASE_URL/coupon/$COUPON1_ID" | jq '.'
echo ""
echo ""

echo "=========================================="
echo "All Tests Completed!"
echo "=========================================="

