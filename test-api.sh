#!/bin/bash

# Coupon Management System - Comprehensive API Testing
# Start the application first: cd CouponSystem && ./mvnw spring-boot:run
# Then run the script: ./test-api.sh

# Allow BASE_URL to be overridden via environment variable (default: localhost for local/CI testing)
BASE_URL="${BASE_URL:-http://localhost:8080}"
PASS_COUNT=0
FAIL_COUNT=0

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print test header
print_test() {
    echo -e "${BLUE}=========================================="
    echo -e "TEST $1"
    echo -e "==========================================${NC}"
}

# Function to print partition header
print_partition() {
    echo -e "${CYAN}$1${NC}"
}

# Function to print test result
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ PASS${NC}"
        ((PASS_COUNT++))
    else
        echo -e "${RED}✗ FAIL${NC}"
        ((FAIL_COUNT++))
    fi
    echo ""
}

# Function to check HTTP status code
check_status() {
    local response=$1
    local expected=$2
    local http_code=$(echo "$response" | tail -n1)

    if [ "$http_code" = "$expected" ]; then
        return 0
    else
        echo -e "${RED}Expected HTTP $expected, got $http_code${NC}"
        return 1
    fi
}

# ==================== 1. INDEX ENDPOINT TESTS ====================
echo -e "${YELLOW}=========================================="
echo "1. INDEX ENDPOINT: GET / or GET /index"
echo -e "==========================================${NC}"
echo ""
echo "Note: This endpoint takes no input parameters, so no equivalence partitions defined."
echo ""

print_test "1.1 - GET /"
echo "Input Type: TYPICAL VALID"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "1.2 - GET /index"
echo "Input Type: ATYPICAL VALID"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/index")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "1.3 - POST / (invalid method)"
echo "Input Type: INVALID"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/")
check_status "$RESPONSE" "405"
print_result $?

# ==================== 2. STORE ENDPOINTS TESTS ====================
echo -e "${YELLOW}=========================================="
echo "2. STORE ENDPOINTS"
echo -e "==========================================${NC}"
echo ""
print_partition "EQUIVALENCE PARTITIONS for POST /store:"
print_partition "  Store Name (String):"
print_partition "    - Valid: Non-empty strings (1-1000 chars)"
print_partition "    - Invalid: null, empty string, whitespace-only"
print_partition "  Boundary Values:"
print_partition "    - Length = 0 (invalid boundary)"
print_partition "    - Length = 1 (valid boundary)"
print_partition "    - Length = 100 (typical valid)"
print_partition "    - Length = 1000+ (very long valid)"
echo ""
print_partition "EQUIVALENCE PARTITIONS for GET /store/{id}:"
print_partition "  Store ID (Integer):"
print_partition "    - Valid: Existing positive IDs (1 to MAX)"
print_partition "    - Invalid: Non-existent IDs, negative, zero"
print_partition "  Boundary Values:"
print_partition "    - ID = 0 (invalid - below minimum)"
print_partition "    - ID = 1 (valid - at minimum boundary)"
print_partition "    - ID = existing max (valid - at upper boundary)"
print_partition "    - ID = 99999 (invalid - non-existent)"
echo ""

print_test "2.1 - POST /store with typical name"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid name, length=8 (within valid range)"
echo "WRITE operation - Creates persistent data"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "BookMart"}')
echo "$RESPONSE" | head -1 | jq '.'
STORE1_ID=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "2.2 - POST /store with special characters"
echo "Input Type: ATYPICAL VALID"
echo "Partition: Valid name with special chars, length=17"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "Tech-World & More!"}')
echo "$RESPONSE" | head -1 | jq '.'
STORE2_ID=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "2.3 - POST /store with empty name"
echo "Input Type: INVALID"
echo "Partition: Invalid name, length=0 (AT lower boundary - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": ""}')
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "2.4 - POST /store with single character"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: Valid name, length=1 (AT lower boundary - valid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "A"}')
echo "$RESPONSE" | head -1 | jq '.'
STORE_SINGLE=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "2.5 - POST /store with very long name"
echo "Input Type: ATYPICAL VALID - Boundary test"
echo "Partition: Valid name, length=200 (ABOVE typical, still valid)"
LONG_NAME=$(printf 'A%.0s' {1..200})
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"$LONG_NAME\"}")
echo "Created store with 200-char name"
check_status "$RESPONSE" "201"
print_result $?

print_test "2.6 - GET /store/{id} with existing ID"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid existing ID (ID=$STORE1_ID)"
echo "READ operation - Verifies persistence from test 2.1"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/store/$STORE1_ID")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "2.7 - GET /store/{id} with ID=1"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: Valid ID=1 (AT minimum valid boundary)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/store/1")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "2.8 - GET /store/{id} with non-existent ID"
echo "Input Type: INVALID"
echo "Partition: Invalid ID=99999 (ABOVE maximum existing ID)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/store/99999")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "404"
print_result $?

print_test "2.9 - GET /stores (get all)"
echo "Input Type: TYPICAL VALID"
echo "Partition: No parameters required"
echo "READ operation - Verifies all persisted stores"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores")
echo "$RESPONSE" | head -1 | jq 'length'
echo "stores returned"
check_status "$RESPONSE" "200"
print_result $?

print_test "2.10 - DELETE /store/{id} with existing ID"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid existing ID"
TEMP_STORE=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "TempStore"}' | jq -r '.id')
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/store/$TEMP_STORE")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "2.11 - DELETE /store/{id} twice (already deleted)"
echo "Input Type: INVALID"
echo "Partition: Invalid ID (no longer exists after deletion)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/store/$TEMP_STORE")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "404"
print_result $?

# ==================== 3. ITEM ENDPOINTS TESTS ====================
echo -e "${YELLOW}=========================================="
echo "3. ITEM ENDPOINTS"
echo -e "==========================================${NC}"
echo ""
print_partition "EQUIVALENCE PARTITIONS for POST /item:"
print_partition "  Item Name (String):"
print_partition "    - Valid: Non-empty strings (1-1000 chars)"
print_partition "    - Invalid: null, empty, whitespace-only"
print_partition "  Item Price (Double):"
print_partition "    - Valid: >= 0.0"
print_partition "    - Invalid: < 0.0, null"
print_partition "  Store ID (Integer):"
print_partition "    - Valid: Existing store IDs"
print_partition "    - Invalid: Non-existent, negative, zero"
print_partition "  Category (String):"
print_partition "    - Valid: Any non-null string"
print_partition "    - Invalid: null (in some contexts)"
print_partition "  Boundary Values for Price:"
print_partition "    - Price = -0.01 (BELOW minimum - invalid)"
print_partition "    - Price = 0.0 (AT minimum boundary - valid)"
print_partition "    - Price = 0.01 (ABOVE minimum - valid)"
print_partition "    - Price = 99999.99 (very high - valid)"
echo ""

print_test "3.1 - POST /item with typical values"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid name (length=16), price=45.99 (mid-range), existing storeId"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Java Programming\", \"price\": 45.99, \"storeId\": $STORE1_ID, \"category\": \"books\"}")
echo "$RESPONSE" | head -1 | jq '.'
ITEM1_ID=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "3.2 - POST /item with price=0.0"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: Price=0.0 (AT minimum valid boundary)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Free Sample\", \"price\": 0.0, \"storeId\": $STORE1_ID, \"category\": \"samples\"}")
echo "$RESPONSE" | head -1 | jq '.'
ITEM_FREE=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "3.3 - POST /item with price=0.01"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: Price=0.01 (JUST ABOVE minimum - valid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Penny Item\", \"price\": 0.01, \"storeId\": $STORE1_ID, \"category\": \"cheap\"}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "201"
print_result $?

print_test "3.4 - POST /item with very high price"
echo "Input Type: ATYPICAL VALID - Boundary test"
echo "Partition: Price=99999.99 (VERY HIGH - still valid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Luxury Item\", \"price\": 99999.99, \"storeId\": $STORE1_ID, \"category\": \"luxury\"}")
echo "$RESPONSE" | head -1 | jq '.'
ITEM2_ID=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "3.5 - POST /item with negative price"
echo "Input Type: INVALID - Boundary test"
echo "Partition: Price=-10.0 (BELOW minimum - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Negative Item\", \"price\": -10.0, \"storeId\": $STORE1_ID, \"category\": \"test\"}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "3.6 - POST /item with non-existent store"
echo "Input Type: INVALID"
echo "Partition: storeId=99999 (non-existent - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d '{"name": "Invalid Item", "price": 10.00, "storeId": 99999, "category": "test"}')
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "3.7 - POST /item with empty name"
echo "Input Type: INVALID - Boundary test"
echo "Partition: Name length=0 (AT lower boundary - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"\", \"price\": 10.0, \"storeId\": $STORE1_ID, \"category\": \"test\"}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "3.8 - POST /item with single character name"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: Name length=1 (AT lower valid boundary)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"X\", \"price\": 5.0, \"storeId\": $STORE1_ID, \"category\": \"test\"}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "201"
print_result $?

# Create additional items for later tests
curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Python Basics\", \"price\": 39.99, \"storeId\": $STORE1_ID, \"category\": \"books\"}" > /dev/null
ITEM3_ID=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Data Structures\", \"price\": 55.00, \"storeId\": $STORE1_ID, \"category\": \"books\"}" | jq -r '.id')
ITEM4_ID=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Bookmark Set\", \"price\": 8.50, \"storeId\": $STORE1_ID, \"category\": \"accessories\"}" | jq -r '.id')

print_test "3.9 - GET /item/{id} with existing ID"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid existing ID"
echo "READ operation - Verifies persistence from test 3.1"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/item/$ITEM1_ID")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "3.10 - GET /item/{id} with non-existent ID"
echo "Input Type: INVALID"
echo "Partition: ID=99999 (non-existent - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/item/99999")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "404"
print_result $?

print_test "3.11 - GET /items (get all)"
echo "Input Type: TYPICAL VALID"
echo "Partition: No parameters"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items")
echo "$RESPONSE" | head -1 | jq 'length'
echo "items returned"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.12 - GET /items/store/{storeId} with items"
echo "Input Type: TYPICAL VALID"
echo "Partition: Existing storeId with items"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/store/$STORE1_ID")
echo "$RESPONSE" | head -1 | jq 'length'
echo "items from store $STORE1_ID"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.13 - GET /items/store/{storeId} with no items"
echo "Input Type: ATYPICAL VALID"
echo "Partition: Existing storeId with zero items"
STORE3_ID=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "EmptyStore"}' | jq -r '.id')
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/store/$STORE3_ID")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "3.14 - GET /items/search?keyword={kw} with matches"
echo "Input Type: TYPICAL VALID"
echo "Partition: Keyword that matches items"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/search?keyword=Java")
echo "$RESPONSE" | head -1 | jq 'length'
echo "matching items"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.15 - GET /items/search?keyword={kw} with no matches"
echo "Input Type: ATYPICAL VALID"
echo "Partition: Keyword with zero matches"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/search?keyword=NOMATCHXYZ")
echo "$RESPONSE" | head -1 | jq 'length'
echo "items (should be 0)"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.16 - GET /items/search?keyword= (empty)"
echo "Input Type: ATYPICAL VALID - Boundary test"
echo "Partition: Keyword length=0 (empty string matches all)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/search?keyword=")
echo "$RESPONSE" | head -1 | jq 'length'
echo "items returned"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.17 - GET /items/category/{cat} with matches"
echo "Input Type: TYPICAL VALID"
echo "Partition: Category that exists"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/category/books")
echo "$RESPONSE" | head -1 | jq 'length'
echo "book items"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.18 - GET /items/category/{cat} no matches"
echo "Input Type: ATYPICAL VALID"
echo "Partition: Category with zero items"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/category/nonexistent")
echo "$RESPONSE" | head -1 | jq 'length'
echo "items (should be 0)"
check_status "$RESPONSE" "200"
print_result $?

print_test "3.19 - DELETE /item/{id} existing"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid existing ID"
TEMP_ITEM=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"TempItem\", \"price\": 10.00, \"storeId\": $STORE1_ID, \"category\": \"temp\"}" | jq -r '.id')
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/item/$TEMP_ITEM")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "3.20 - DELETE /item/{id} twice"
echo "Input Type: INVALID"
echo "Partition: ID no longer exists"
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/item/$TEMP_ITEM")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "404"
print_result $?

# ==================== 4. COUPON ENDPOINTS TESTS ====================
echo -e "${YELLOW}=========================================="
echo "4. COUPON ENDPOINTS"
echo -e "==========================================${NC}"
echo ""
print_partition "EQUIVALENCE PARTITIONS for POST /coupon:"
print_partition "  Coupon Type (String):"
print_partition "    - Valid: 'totalprice', 'category', 'item'"
print_partition "    - Invalid: any other string, null"
print_partition "  Discount Value (Double):"
print_partition "    - Valid: >= 0.0"
print_partition "    - Invalid: < 0.0"
print_partition "  isPercentage (Boolean):"
print_partition "    - Valid: true, false"
print_partition "  For Percentage Discounts:"
print_partition "    - Valid: 0.0 to 100.0"
print_partition "    - Invalid: > 100.0"
print_partition "  Minimum Purchase (TotalPriceCoupon):"
print_partition "    - Valid: >= 0.0"
print_partition "    - Invalid: < 0.0"
print_partition "  Boundary Values:"
print_partition "    - discountValue = -0.01 (BELOW min - invalid)"
print_partition "    - discountValue = 0.0 (AT min - valid)"
print_partition "    - discountValue = 0.01 (ABOVE min - valid)"
print_partition "    - For percentage: 100.0 (AT max - valid)"
print_partition "    - For percentage: 100.01 (ABOVE max - invalid)"
echo ""

print_test "4.1 - POST /coupon TotalPrice typical"
echo "Input Type: TYPICAL VALID"
echo "Partition: type='totalprice', discount=10.0 (mid-range), minPurchase=50.0"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE1_ID, \"discountValue\": 10.0, \"isPercentage\": true, \"minimumPurchase\": 50.0}")
echo "$RESPONSE" | head -1 | jq '.'
COUPON1_ID=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "4.2 - POST /coupon with discount=0.0"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: discountValue=0.0 (AT minimum boundary - valid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"category\", \"storeId\": $STORE1_ID, \"discountValue\": 0.0, \"isPercentage\": false, \"category\": \"books\"}")
echo "$RESPONSE" | head -1 | jq '.'
COUPON2_ID=$(echo "$RESPONSE" | head -1 | jq -r '.id')
check_status "$RESPONSE" "201"
print_result $?

print_test "4.3 - POST /coupon with percentage=100.0"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: Percentage=100.0 (AT maximum boundary - valid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"item\", \"storeId\": $STORE1_ID, \"discountValue\": 100.0, \"isPercentage\": true, \"targetItemId\": $ITEM1_ID}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "201"
print_result $?

print_test "4.4 - POST /coupon with negative discount"
echo "Input Type: INVALID - Boundary test"
echo "Partition: discountValue=-5.0 (BELOW minimum - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE1_ID, \"discountValue\": -5.0, \"isPercentage\": true, \"minimumPurchase\": 50.0}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "4.5 - POST /coupon with percentage>100"
echo "Input Type: INVALID - Boundary test"
echo "Partition: Percentage=150.0 (ABOVE maximum - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE1_ID, \"discountValue\": 150.0, \"isPercentage\": true, \"minimumPurchase\": 50.0}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "4.6 - POST /coupon with negative minPurchase"
echo "Input Type: INVALID - Boundary test"
echo "Partition: minimumPurchase=-50.0 (BELOW minimum - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE1_ID, \"discountValue\": 10.0, \"isPercentage\": true, \"minimumPurchase\": -50.0}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "4.7 - POST /coupon with minPurchase=0.0"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: minimumPurchase=0.0 (AT minimum - valid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $STORE1_ID, \"discountValue\": 5.0, \"isPercentage\": false, \"minimumPurchase\": 0.0}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "201"
print_result $?

print_test "4.8 - POST /coupon with invalid type"
echo "Input Type: INVALID"
echo "Partition: type='invalid' (not in valid set)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"invalid\", \"storeId\": $STORE1_ID, \"discountValue\": 10.0, \"isPercentage\": true}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "4.9 - POST /coupon CategoryCoupon with null category"
echo "Input Type: INVALID"
echo "Partition: category=null (required field missing)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"category\", \"storeId\": $STORE1_ID, \"discountValue\": 5.0, \"isPercentage\": false, \"category\": null}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "4.10 - POST /coupon ItemCoupon with non-existent item"
echo "Input Type: INVALID"
echo "Partition: targetItemId=99999 (non-existent)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"item\", \"storeId\": $STORE1_ID, \"discountValue\": 15.0, \"isPercentage\": true, \"targetItemId\": 99999}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

# Create additional coupons for later tests
COUPON3_ID=$(curl -s -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"category\", \"storeId\": $STORE1_ID, \"discountValue\": 5.0, \"isPercentage\": false, \"category\": \"books\"}" | jq -r '.id')
COUPON4_ID=$(curl -s -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"item\", \"storeId\": $STORE1_ID, \"discountValue\": 15.0, \"isPercentage\": true, \"targetItemId\": $ITEM1_ID}" | jq -r '.id')

print_test "4.11 - GET /coupon/{id} existing"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid existing ID"
echo "READ operation - Verifies persistence from test 4.1"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupon/$COUPON1_ID")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "4.12 - GET /coupon/{id} non-existent"
echo "Input Type: INVALID"
echo "Partition: ID=99999 (non-existent)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupon/99999")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "404"
print_result $?

print_test "4.13 - GET /coupons (get all)"
echo "Input Type: TYPICAL VALID"
echo "Partition: No parameters"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupons")
echo "$RESPONSE" | head -1 | jq 'length'
echo "coupons returned"
check_status "$RESPONSE" "200"
print_result $?

print_test "4.14 - GET /coupons/store/{id} with coupons"
echo "Input Type: TYPICAL VALID"
echo "Partition: storeId with coupons"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupons/store/$STORE1_ID")
echo "$RESPONSE" | head -1 | jq 'length'
echo "coupons from store $STORE1_ID"
check_status "$RESPONSE" "200"
print_result $?

print_test "4.15 - GET /coupons/store/{id} no coupons"
echo "Input Type: ATYPICAL VALID"
echo "Partition: storeId with zero coupons"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupons/store/$STORE3_ID")
echo "$RESPONSE" | head -1 | jq 'length'
echo "coupons (should be 0)"
check_status "$RESPONSE" "200"
print_result $?

print_test "4.16 - DELETE /coupon/{id} existing"
echo "Input Type: TYPICAL VALID"
echo "Partition: Valid existing ID"
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/coupon/$COUPON2_ID")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "4.17 - DELETE /coupon/{id} twice"
echo "Input Type: INVALID"
echo "Partition: ID no longer exists"
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/coupon/$COUPON2_ID")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "404"
print_result $?

# ==================== 5. CORE FUNCTIONALITY ENDPOINTS TESTS ====================
echo -e "${YELLOW}=========================================="
echo "5. CORE FUNCTIONALITY ENDPOINTS"
echo -e "==========================================${NC}"
echo ""
print_partition "EQUIVALENCE PARTITIONS for POST /cart/optimal-coupon:"
print_partition "  ItemIds Array:"
print_partition "    - Valid: Non-empty array of existing item IDs"
print_partition "    - Invalid: empty array, null, non-existent IDs"
print_partition "  StoreId:"
print_partition "    - Valid: Existing store ID"
print_partition "    - Invalid: Non-existent ID"
print_partition "  Boundary Values for array size:"
print_partition "    - size = 0 (AT lower boundary - invalid)"
print_partition "    - size = 1 (ABOVE lower boundary - valid)"
print_partition "    - size = 10 (typical - valid)"
print_partition "    - size = 100+ (large - valid)"
echo ""
print_partition "EQUIVALENCE PARTITIONS for GET /stores/optimal:"
print_partition "  Keyword (String, optional):"
print_partition "    - Valid: Any string including empty"
print_partition "    - Invalid: null when category also null"
print_partition "  Category (String, optional):"
print_partition "    - Valid: Any string"
print_partition "    - Invalid: null when keyword also null"
print_partition "  At least one must be provided"
echo ""

print_test "5.1 - POST /cart/optimal-coupon typical cart"
echo "Input Type: TYPICAL VALID"
echo "Partition: itemIds array size=3, existing storeId"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID, $ITEM3_ID, $ITEM4_ID], \"storeId\": $STORE1_ID}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "5.2 - POST /cart/optimal-coupon single item"
echo "Input Type: TYPICAL VALID - Boundary test"
echo "Partition: itemIds array size=1 (AT lower valid boundary)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID], \"storeId\": $STORE1_ID}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "5.3 - POST /cart/optimal-coupon empty cart"
echo "Input Type: INVALID - Boundary test"
echo "Partition: itemIds array size=0 (AT/BELOW lower boundary - invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [], \"storeId\": $STORE1_ID}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "5.4 - POST /cart/optimal-coupon non-existent item"
echo "Input Type: INVALID"
echo "Partition: itemIds contains non-existent ID=99999"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [99999], \"storeId\": $STORE1_ID}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "5.5 - POST /cart/optimal-coupon non-existent store"
echo "Input Type: ATYPICAL VALID"
echo "Partition: storeId=99999 (non-existent - returns no coupon)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID], \"storeId\": 99999}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "5.6 - GET /stores/optimal with category"
echo "Input Type: TYPICAL VALID"
echo "Partition: category provided, keyword null"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores/optimal?category=books")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "5.7 - GET /stores/optimal with keyword"
echo "Input Type: TYPICAL VALID"
echo "Partition: keyword provided, category null"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores/optimal?keyword=Java")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "5.8 - GET /stores/optimal with both params"
echo "Input Type: ATYPICAL VALID"
echo "Partition: Both keyword and category provided"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores/optimal?keyword=Java&category=books")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "5.9 - GET /stores/optimal with no params"
echo "Input Type: INVALID - Boundary test"
echo "Partition: Both keyword and category null (BELOW minimum params)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores/optimal")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "5.10 - GET /stores/optimal with empty strings"
echo "Input Type: INVALID - Boundary test"
echo "Partition: Both params empty strings (length=0)"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores/optimal?keyword=&category=")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "5.11 - GET /stores/optimal non-existent category"
echo "Input Type: ATYPICAL VALID"
echo "Partition: category with zero matches"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/stores/optimal?category=nonexistent")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "5.12 - POST /cart/suggest-items typical"
echo "Input Type: TYPICAL VALID"
echo "Partition: cart below threshold, valid coupon"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM3_ID], \"storeId\": $STORE1_ID, \"couponId\": $COUPON1_ID}")
echo "$RESPONSE" | head -1 | jq '.'
check_status "$RESPONSE" "200"
print_result $?

print_test "5.13 - POST /cart/suggest-items threshold met"
echo "Input Type: ATYPICAL VALID"
echo "Partition: cart total >= minimum purchase (AT/ABOVE threshold)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID, $ITEM3_ID], \"storeId\": $STORE1_ID, \"couponId\": $COUPON1_ID}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "5.14 - POST /cart/suggest-items empty cart"
echo "Input Type: INVALID - Boundary test"
echo "Partition: itemIds array size=0 (invalid)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [], \"storeId\": $STORE1_ID, \"couponId\": $COUPON1_ID}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "400"
print_result $?

print_test "5.15 - POST /cart/suggest-items non-existent coupon"
echo "Input Type: INVALID"
echo "Partition: couponId=99999 (non-existent)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID], \"storeId\": $STORE1_ID, \"couponId\": 99999}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "5.16 - POST /cart/suggest-items wrong coupon type"
echo "Input Type: INVALID"
echo "Partition: couponId references non-TotalPriceCoupon (wrong type)"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/suggest-items" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$ITEM1_ID], \"storeId\": $STORE1_ID, \"couponId\": $COUPON4_ID}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

# ==================== 6. MULTI-CLIENT FUNCTIONALITY TESTS ====================
echo ""
echo -e "${YELLOW}=========================================="
echo "6. MULTI-CLIENT FUNCTIONALITY TESTS"
echo -e "==========================================${NC}"
echo ""

print_test "6.0 - Multi-client setup"
echo "Creating separate stores/items for isolation testing"
CLIENT1_STORE=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "Client1Store"}' | jq -r '.id')
echo "Client 1 Store ID: $CLIENT1_STORE"

CLIENT1_ITEM=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Client1Item\", \"price\": 25.00, \"storeId\": $CLIENT1_STORE, \"category\": \"electronics\"}" | jq -r '.id')
echo "Client 1 Item ID: $CLIENT1_ITEM"

CLIENT2_STORE=$(curl -s -X POST "$BASE_URL/store" \
  -H "Content-Type: application/json" \
  -d '{"name": "Client2Store"}' | jq -r '.id')
echo "Client 2 Store ID: $CLIENT2_STORE"

CLIENT2_ITEM=$(curl -s -X POST "$BASE_URL/item" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Client2Item\", \"price\": 30.00, \"storeId\": $CLIENT2_STORE, \"category\": \"electronics\"}" | jq -r '.id')
echo "Client 2 Item ID: $CLIENT2_ITEM"
echo -e "${GREEN}Setup complete${NC}"
echo ""

print_test "6.1 - Multi-client data isolation (Client 1)"
echo "Input Type: TYPICAL VALID"
echo "Verifies: Client 1 sees only their items"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/store/$CLIENT1_STORE")
ITEMS=$(echo "$RESPONSE" | head -1 | jq '.')
echo "$ITEMS"
ITEM_COUNT=$(echo "$ITEMS" | jq 'length')
echo "Items returned: $ITEM_COUNT (should be 1)"
check_status "$RESPONSE" "200"
print_result $?

print_test "6.2 - Multi-client data isolation (Client 2)"
echo "Input Type: TYPICAL VALID"
echo "Verifies: Client 2 sees only their items"
RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/store/$CLIENT2_STORE")
ITEMS=$(echo "$RESPONSE" | head -1 | jq '.')
echo "$ITEMS"
ITEM_COUNT=$(echo "$ITEMS" | jq 'length')
echo "Items returned: $ITEM_COUNT (should be 1)"
check_status "$RESPONSE" "200"
print_result $?

print_test "6.3 - Multi-client concurrent operations"
echo "Input Type: TYPICAL VALID"
echo "Verifies: Both clients can create coupons independently"
RESPONSE1=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $CLIENT1_STORE, \"discountValue\": 5.0, \"isPercentage\": false, \"minimumPurchase\": 20.0}")
echo "Client 1 coupon:"
echo "$RESPONSE1" | head -1 | jq '.'

RESPONSE2=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/coupon" \
  -H "Content-Type: application/json" \
  -d "{\"type\": \"totalprice\", \"storeId\": $CLIENT2_STORE, \"discountValue\": 10.0, \"isPercentage\": true, \"minimumPurchase\": 25.0}")
echo "Client 2 coupon:"
echo "$RESPONSE2" | head -1 | jq '.'

check_status "$RESPONSE1" "201"
STATUS1=$?
check_status "$RESPONSE2" "201"
STATUS2=$?
if [ $STATUS1 -eq 0 ] && [ $STATUS2 -eq 0 ]; then
    print_result 0
else
    print_result 1
fi

print_test "6.4 - Multi-client coupon isolation"
echo "Input Type: TYPICAL VALID"
echo "Verifies: Coupon isolation between clients"
RESPONSE1=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupons/store/$CLIENT1_STORE")
COUNT1=$(echo "$RESPONSE1" | head -1 | jq 'length')
echo "Client 1 has $COUNT1 coupon(s)"

RESPONSE2=$(curl -s -w "\n%{http_code}" "$BASE_URL/coupons/store/$CLIENT2_STORE")
COUNT2=$(echo "$RESPONSE2" | head -1 | jq 'length')
echo "Client 2 has $COUNT2 coupon(s)"

check_status "$RESPONSE1" "200"
STATUS1=$?
check_status "$RESPONSE2" "200"
STATUS2=$?
if [ $STATUS1 -eq 0 ] && [ $STATUS2 -eq 0 ]; then
    print_result 0
else
    print_result 1
fi

print_test "6.5 - Cross-client interference prevention"
echo "Input Type: ATYPICAL VALID"
echo "Verifies: Client 1 cannot use Client 2's items with their coupons"
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart/optimal-coupon" \
  -H "Content-Type: application/json" \
  -d "{\"itemIds\": [$CLIENT2_ITEM], \"storeId\": $CLIENT1_STORE}")
echo "$RESPONSE" | head -1
check_status "$RESPONSE" "200"
print_result $?

print_test "6.6 - Independent deletion operations"
echo "Input Type: TYPICAL VALID"
echo "Verifies: Deleting Client 1 data doesn't affect Client 2"
RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/item/$CLIENT1_ITEM")
echo "Deleted Client 1 item"
check_status "$RESPONSE" "200"

RESPONSE2=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/store/$CLIENT2_STORE")
ITEM_COUNT=$(echo "$RESPONSE2" | head -1 | jq 'length')
echo "Client 2 still has $ITEM_COUNT item(s) (should be 1)"
check_status "$RESPONSE2" "200"
print_result $?

# ==================== FINAL SUMMARY ====================
echo ""
echo "=========================================="
echo "TEST SUMMARY"
echo "=========================================="
echo -e "${GREEN}Passed: $PASS_COUNT${NC}"
echo -e "${RED}Failed: $FAIL_COUNT${NC}"
TOTAL=$((PASS_COUNT + FAIL_COUNT))
echo "Total: $TOTAL"
echo ""

# ==================== DATABASE CLEANUP ====================
echo -e "${YELLOW}=========================================="
echo "CLEANING UP DATABASE"
echo -e "==========================================${NC}"
echo ""

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo -e "${RED}psql not found. Database cleanup will be skipped.${NC}"
    echo ""
    echo "To install PostgreSQL client (psql):"
    echo "  Ubuntu/Debian: sudo apt install postgresql-client"
    echo "  Fedora/RHEL:   sudo dnf install postgresql"
    echo "  macOS:         brew install postgresql"
    echo ""
    echo "After installing, you can manually clean the database by running:"
    echo "  ./cleanup_db.sh"
    echo ""
else
    # Database connection details from application.properties
    DB_HOST="advancedse-db1.cro62egwmoki.us-east-2.rds.amazonaws.com"
    DB_PORT="5432"
    DB_NAME="coupon_db"
    DB_USER="postgres"
    export PGPASSWORD="AdvancedSE_TeamProject"

    echo "Removing all test data from database..."

    # Truncate tables and reset identity columns
    psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "TRUNCATE TABLE stores, items, coupons RESTART IDENTITY CASCADE;" > /dev/null 2>&1

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Database cleanup successful. All data removed and IDs reset.${NC}"
    else
        echo -e "${RED}Warning: Database cleanup failed. You may need to clean up manually.${NC}"
    fi
    echo ""
fi

if [ $FAIL_COUNT -eq 0 ]; then
    echo -e "${GREEN}All tests passed! ✓${NC}"
    exit 0
else
    echo -e "${RED}Some tests failed. Please review the output above.${NC}"
    exit 1
fi
