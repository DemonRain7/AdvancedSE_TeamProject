# Academic Honesty Pledge

I, Yu Qiu yq2427, have read and understood the following:
  - CS department's Policies and Procedures on Academic Honesty
  - The Course Specific Academic Honesty Policies
  - The academic honesty policy above outlining the consequences of not submitting this pledge and other aspects of the policy


  I affirm that I will abide by all the policies stated in the relevant materials from above. I understand that the relevant policies apply to: individual assignments, group projects, and individual examinations.

  I also affirm that I understand that all course materials, with the exception of the team project, are subject to the appropriate copyrights and thus will not post them on any public forum or publicly hosted repository, this includes but is not limited to: GitHub, stackoverflow, chegg etc.

  I also affirm that I will be 100% honest when evaluating the performance of myself and my teammates when prompted by an assignment or member of the teaching staff.

  Finally I affirm that I will not attempt to find any loopholes in these policies for the benefit of myself or others enrolled in the course presently or possibly in the future.

**Signed:** Yu Qiu yq2427, Oct.24, 2025

---

# PART ZERO

## 0A. Team Member Points

| Team Member | Points |
|-------------|--------|
| Yu Qiu | 21 |
| Eric Xue | 23 |
| Teresa | 20 |
| Tianjun Feng | 18 |
| YeYu | 18 |

## Explanation of Scores

### Yu Qiu (Me)– 21 points

I coordinated the entire workflow — task allocating (which needs tremendous
considering I think — fair distribution, order conflicts, timeline design, etc.), managing branching, PR reviews, etc. For the coding part, I am responsible for GitHub setup, building configuration and initial springboot setups. I also contributed heavily to testing (service-layer test cases, and some final validation like Stylecheck). And I am also active in discussions.

### Eric Xue – 23 points

Eric is responsible for the Data Service and Business Logic Service. He also authored early detailed architectural outlines (which I think is hard-work). He is also responsible for the final version validation (like logical validation for all the APIs). Eric's leadership in coding logic and active involvement justifies a top-tier score.

### Teresa – 20 points

As the Data Modeler, Teresa implemented the system's data models and entity relations, which provided the foundation for service and API layers. She also maintained strong communication across the team, helping align model changes with business logic and integration tests. While her technical role was primarily focused on modeling rather than system orchestration, her reliability and team engagement (like communication with the TA) were critical to progress. She is very active.

### Tianjun Feng – 18 points

Tianjun focused on API controllers and error handling, implementing key endpoints and ensuring robust request–response behavior. Though his contributions were solid and technically correct, he was slightly less active in inter-team discussions and overall coordination compared to others.

### YeYu – 18 points

YeYu handled testing and documentation, including model/controller tests and API integration scripts. He also wrote detailed developer guides and troubleshooting documentation, ensuring reproducibility and developer accessibility. While being a little bit inactive, his testing work was crucial for ensuring system reliability and coverage completeness.

---

# PART ONE

## Selected API Entry Points

### 1. POST /coupon (RouteController.java)
- Takes multiple inputs including type, storeId, discountValue, isPercentage, and type-specific parameters. 
- Uses a switch statement & lots of ifs.

### 2. POST /cart/optimal-coupon (RouteController.java)
- Takes an array of item IDs and a store ID.
- Contains a loop and ifs.

### 3. POST /item (RouteController.java)
- Takes multiple inputs through an Item object (name, price, storeId, category).
- Contains ifs.

---

## Detailed Analysis 1: POST /coupon Endpoint

### Input Parameters Explanations for TA

Inputs a JSON request body with the following parameters:

**Common parameters (all coupon types):**
- **type** (String): Coupon type - "totalprice", "category", or "item"
- **storeId** (Integer): ID of the store this coupon belongs to
- **discountValue** (Double): Discount amount
- **isPercentage** (Boolean): true for percentage discount, false for fixed amount

**type-specific additional paras:**
- **minimumPurchase** (Double): Required for TotalPriceCoupon - minimum cart total to qualify for discount
- **category** (String): Required for CategoryCoupon - product category this coupon applies to
- **targetItemId** (Integer): Required for ItemCoupon - specific item ID this coupon discounts

### Equivalence Partitions

**Valid Partitions:**

**V1: TotalPriceCoupon with percentage discount**
- type = "totalprice", storeId exists, isPercentage = true, 0 ≤ discountValue ≤ 100, minimumPurchase ≥ 0

**V2: TotalPriceCoupon with fixed discount**
- type = "totalprice", storeId exists, isPercentage = false, discountValue ≥ 0, minimumPurchase ≥ 0

**V3: CategoryCoupon with percentage discount**
- type = "category", storeId exists, isPercentage = true, 0 ≤ discountValue ≤ 100, category non-empty (after trim())

**V4: CategoryCoupon with fixed discount**
- type = "category", storeId exists, isPercentage = false, discountValue ≥ 0, category non-empty (after trim())

**V5: ItemCoupon with percentage discount**
- type = "item", storeId exists, isPercentage = true, 0 ≤ discountValue ≤ 100, targetItemId exists

**V6: ItemCoupon with fixed discount**
- type = "item", storeId exists, isPercentage = false, discountValue ≥ 0, targetItemId exists

**Invalid Partitions:**

**I1: Negative discount**
- discountValue < 0 → HTTP 400 "Discount value cannot be negative."

**I2: Percentage exceeds 100**
- isPercentage=true AND discountValue > 100 → HTTP 400 "Percentage discount cannot exceed 100%."

**I3: Non-existent store**
- storeId not in database → HTTP 400 "Store does not exist."

**I4: Invalid type**
- type not in {totalprice, category, item} → HTTP 400 "Invalid coupon type."

**I5: Negative minimum purchase**
- minimumPurchase < 0 → HTTP 400 "Minimum purchase cannot be negative."

**I6: Empty category**
- category null/empty/whitespace → HTTP 400 "Category cannot be empty."

**I7: Non-existent target item**
- targetItemId not in database → HTTP 400 "Target item does not exist."

### Boundary Values

**discountValue (percentage):** -0.01 (invalid), 0.0 (valid min), 50.0 (normal), 100.0 (valid max), 100.01 (invalid)

**discountValue (fixed):** -0.01 (invalid), 0.0 (valid min), 25.0 (normal), no upper limit

**minimumPurchase:** -0.01 (invalid), 0.0 (valid min), 50.0 (normal), no upper limit

**category:** null (invalid), "" (invalid), "   " (invalid), "a" (valid min), long string (valid max)

### Test Cases

#### V1 (REUSING - createTotalPriceCouponTest)

Input: `{"type": "totalprice", "storeId": 1, "discountValue": 10.0, "isPercentage": true, "minimumPurchase": 50.0}`

Expected: HTTP 201 with TotalPriceCoupon object

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 10.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 10.0 <= 100
- [X] `if (store not exists)` - store 1 exists (let's just assume so)
- [O] Switch case "totalprice" - matches
- [X] `if (minPurchase < 0)` - 50.0 >= 0
- [O] Create TotalPriceCoupon object
- [O] dataService.addCoupon()
- [O] Return HTTP 201

#### V2 (NEW)

Input: `{"type": "totalprice", "storeId": 1, "discountValue": 5.0, "isPercentage": false, "minimumPurchase": 20.0}`

Expected: HTTP 201 with TotalPriceCoupon object

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 5.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - isPercentage=false, skipped
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "totalprice" - matches
- [X] `if (minPurchase < 0)` - 20.0 >= 0
- [O] Create TotalPriceCoupon object
- [O] dataService.addCoupon()
- [O] Return HTTP 201

#### V3 (NEW)

Input: `{"type": "category", "storeId": 1, "discountValue": 15.0, "isPercentage": true, "category": "electronics"}`

Expected: HTTP 201 with CategoryCoupon object

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 15.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 15.0 <= 100
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "category" - matches
- [X] `if (category empty)` - "electronics" is valid
- [O] Create CategoryCoupon object
- [O] dataService.addCoupon()
- [O] Return HTTP 201

#### V4 (REUSING - createCategoryCouponTest)

Input: `{"type": "category", "storeId": 1, "discountValue": 5.0, "isPercentage": false, "category": "books"}`

Expected: HTTP 201 with CategoryCoupon object

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 5.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - isPercentage=false, skipped
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "category" - matches
- [X] `if (category empty)` - "books" is valid
- [O] Create CategoryCoupon object
- [O] dataService.addCoupon()
- [O] Return HTTP 201

#### V5 (REUSING - createItemCouponTest)

Input: `{"type": "item", "storeId": 1, "discountValue": 15.0, "isPercentage": true, "targetItemId": 1}`

Expected: HTTP 201 with ItemCoupon object

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 15.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 15.0 <= 100
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "item" - matches
- [X] `if (targetItem not exists)` - item 1 exists
- [O] Create ItemCoupon object
- [O] dataService.addCoupon()
- [O] Return HTTP 201

#### V6 (NEW)

Input: `{"type": "item", "storeId": 1, "discountValue": 10.0, "isPercentage": false, "targetItemId": 1}`

Expected: HTTP 201 with ItemCoupon object

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 10.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - isPercentage=false, skipped
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "item" - matches
- [X] `if (targetItem not exists)` - item 1 exists
- [O] Create ItemCoupon object
- [O] dataService.addCoupon()
- [O] Return HTTP 201

#### I1 (NEW)

Input: `{"type": "totalprice", "storeId": 1, "discountValue": -5.0, "isPercentage": false, "minimumPurchase": 0.0}`

Expected: HTTP 400 "Discount value cannot be negative."

Code path:
- [O] Extract parameters from request body
- [O] `if (discountValue < 0)` - -5.0 < 0, enters condition
- [O] Return HTTP 400 "Discount value cannot be negative."
- [X] Percentage validation - not reached
- [X] Store validation - not reached
- [X] Switch statement - not reached

#### I2 (NEW)

Input: `{"type": "category", "storeId": 1, "discountValue": 100.01, "isPercentage": true, "category": "toys"}`

Expected: HTTP 400 "Percentage discount cannot exceed 100%."

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 100.01 >= 0
- [O] `if (isPercentage && discountValue > 100)` - 100.01 > 100, enters condition
- [O] Return HTTP 400 "Percentage discount cannot exceed 100%."
- [X] Store validation - not reached
- [X] Switch statement - not reached

#### I3 (NEW)

Input: `{"type": "totalprice", "storeId": 9999, "discountValue": 20.0, "isPercentage": true, "minimumPurchase": 100.0}`

Expected: HTTP 400 "Store does not exist."

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 20.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 20.0 <= 100
- [O] `if (store not exists)` - store 9999 doesn't exist, enters condition (let's just assume so)
- [O] Return HTTP 400 "Store does not exist."
- [X] Switch statement - not reached

#### I4 (REUSING - createCouponWithInvalidTypeTest)

Input: `{"type": "invalid", "storeId": 1, "discountValue": 10.0, "isPercentage": true}`

Expected: HTTP 400 "Invalid coupon type."

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 10.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 10.0 <= 100
- [X] `if (store not exists)` - store 1 exists
- [O] Switch default case - "invalid" doesn't match any case
- [X] Type-specific validations - not reached
- [X] Create coupon object - not reached
- [O] Return HTTP 400 "Invalid coupon type."

#### I5 (NEW)

Input: `{"type": "totalprice", "storeId": 1, "discountValue": 10.0, "isPercentage": true, "minimumPurchase": -25.0}`

Expected: HTTP 400 "Minimum purchase cannot be negative."

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 10.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 10.0 <= 100
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "totalprice" - matches
- [O] `if (minPurchase < 0)` - -25.0 < 0, enters condition
- [O] Return HTTP 400 "Minimum purchase cannot be negative."
- [X] Create coupon - not reached

#### I6 (NEW)

Input: `{"type": "category", "storeId": 1, "discountValue": 8.0, "isPercentage": false, "category": ""}`

Expected: HTTP 400 "Category cannot be empty."

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 8.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - isPercentage=false, skipped
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "category" - matches
- [O] `if (category empty)` - "".trim().isEmpty() is true, enters condition
- [O] Return HTTP 400 "Category cannot be empty."
- [X] Create coupon - not reached

#### I7 (NEW)

Input: `{"type": "item", "storeId": 1, "discountValue": 25.0, "isPercentage": true, "targetItemId": 9999}`

Expected: HTTP 400 "Target item does not exist."

Code path:
- [O] Extract parameters from request body
- [X] `if (discountValue < 0)` - 25.0 >= 0
- [X] `if (isPercentage && discountValue > 100)` - 25.0 <= 100
- [X] `if (store not exists)` - store 1 exists
- [O] Switch case "item" - matches
- [O] `if (targetItem not exists)` - item 9999 doesn't exist, enters condition
- [O] Return HTTP 400 "Target item does not exist."
- [X] Create coupon - not reached

---

## Detailed Analysis 2: POST /cart/optimal-coupon Endpoint

### Input Parameters Explanations for TA

A JSON request body which contains:

- **itemIds** (Array of Integers): List of item IDs in the shopping cart
- **storeId** (Integer): ID of the store to find coupons from

### Equivalence Partitions

(I now found that we should add the verification for storeId, I guess I should just ignore it for now XD, since whether it's valid or not the output remains the same as some partitions.)

**Valid Partitions:**

**V1: Valid cart with applicable coupon**
- itemIds with all valid item IDs, couponService returns an applicable coupon

**V2: Valid cart but no applicable coupon**
- itemIds with all valid item IDs, couponService returns null (no coupon applicable)

**Invalid Partitions:**

**I1: Null or empty itemIds**
- itemIds = null OR itemIds = [] (empty array) → HTTP 400 "Cart cannot be empty."

**I2: Cart contains non-existent item**
- itemIds contains at least one item ID that doesn't exist in database → HTTP 400 "Item with ID X does not exist."

### Boundary Values

**itemIds array size:** null (invalid), 0 (invalid), 1 (valid min), verybig number (valid max)

**itemId values:** non-existent ID like -1 or 9999 (invalid boundary (biggest negative & smallest positive but not in the DB)), existing ID (valid, perhaps 0/1 is the smallest boundary)

### Test Cases

#### V1 (REUSING - findOptimalCouponTest)

Input: `{"itemIds": [1, 2], "storeId": 1}`

Expected: HTTP 200 with response containing coupon and discount information

Code path:
- [O] Extract parameters from request body
- [X] `if (itemIds null or empty)` - [1, 2] is not null/empty
- [O] Convert ArrayList to int array
- [O] Loop through itemIds to validate - enters loop twice
- [X] `if (item not exists)` - items 1 and 2 exist
- [O] Call couponService.findOptimalCoupon()
- [X] `if (optimalCoupon == null)` - coupon found
- [O] Loop to build Item array
- [O] Calculate discount via optimalCoupon.calculateDiscount()
- [O] Build response with coupon and discount
- [O] Return HTTP 200

#### V2 (REUSING - findOptimalCouponWhenNoneFoundTest)

Input: `{"itemIds": [1], "storeId": 1}`

Expected: HTTP 200 "No applicable coupon found."

Code path:
- [O] Extract parameters from request body
- [X] `if (itemIds null or empty)` - [1] is not null/empty
- [O] Convert ArrayList to int array
- [O] Loop through itemIds to validate - enters loop once
- [X] `if (item not exists)` - item 1 exists
- [O] Call couponService.findOptimalCoupon()
- [O] `if (optimalCoupon == null)` - no coupon found, enters condition
- [O] Return HTTP 200 "No applicable coupon found."
- [X] Discount calculation - not reached
- [X] Build response map - not reached

#### I1 (NEW)

Input: `{"itemIds": null/[], "storeId": 1}`

Expected: HTTP 400 "Cart cannot be empty."

Code path:
- [O] Extract parameters from request body
- [O] `if (itemIds null or empty)` - itemIds is null, enters condition
- [O] Return HTTP 400 "Cart cannot be empty."
- [X] Array conversion - not reached
- [X] Validation loop - not reached
- [X] Service call - not reached

#### I2 (NEW)

Input: `{"itemIds": [1, 9999], "storeId": 1}`

Expected: HTTP 400 "Item with ID 9999 does not exist."

Code path:
- [O] Extract parameters from request body
- [X] `if (itemIds null or empty)` - [1, 9999] is not null/empty
- [O] Convert ArrayList to int array
- [O] Loop through itemIds to validate - enters loop twice
- [X] `if (item not exists)` - item 1 exists (first iteration)
- [O] `if (item not exists)` - item 9999 doesn't exist (second iteration), enters condition
- [O] Return HTTP 400 "Item with ID 9999 does not exist."
- [X] Service call - not reached
- [X] Discount calculation - not reached

---

## Detailed Analysis 3: POST /item Endpoint

### Input Parameters Explanations for TA

Also a JSON request body, representing an Item object with:

- **name** (String): Name of the item
- **price** (Double): Price of the item
- **storeId** (Integer): ID of the store this item belongs to
- **category** (String): Category of the item. (Oh damn, this is another value we forget to verify!)


### Equivalence Partitions

**Valid Partitions:**

**V1: Valid item with all required fields**
- name is non-null and non-empty (after trim), price >= 0, storeId exists in database

**Invalid Partitions:**

**I1: Null or empty name**
- name = null OR name.trim() = "" → HTTP 400 "Item name cannot be empty."

**I2: Negative price**
- price < 0 → HTTP 400 "Item price cannot be negative."

**I3: Non-existent store**
- storeId not in database → HTTP 400 "Store does not exist."

### Boundary Values

**name:** null (invalid), "" (invalid), "   " (invalid), "a" (valid min), long string (valid max)

**price:** -0.01 (invalid), 0.0 (valid min), 10.0 (normal), very large number (valid max)

**storeId:** same as previously mentioned: non-existent ID like -1 or 9999 (invalid boundary (biggest negative & smallest positive but not in the DB)), existing ID (valid, perhaps 0/1 is the valid min)

### Test Cases

#### V1 (REUSING - createItemWithValidStoreTest)

Input: `{"name": "Test Item", "price": 50.0, "storeId": 1, "category": "books"}`

Expected: HTTP 201 with created Item object

Code path:
- [O] Extract Item object from request body
- [X] `if (name null or empty)` - "Test Item" is valid
- [X] `if (price < 0)` - 50.0 >= 0
- [X] `if (store not exists)` - store 1 exists
- [O] Call dataService.addItem()
- [O] Return HTTP 201 with created item

#### I1 - null/whitespace/empty name (NEW)

Input: `{"name": null/"   "/"", "price": 10.0, "storeId": 1, "category": "electronics"}`

Expected: HTTP 400 "Item name cannot be empty."

Code path:
- [O] Extract Item object from request body
- [O] `if (name null or empty)` - name is null, enters condition
- [O] Return HTTP 400 "Item name cannot be empty."
- [X] Price validation - not reached
- [X] Store validation - not reached
- [X] Item creation - not reached

#### I2 - negative price (NEW)

Input: `{"name": "Test", "price": -5.0, "storeId": 1, "category": "books"}`

Expected: HTTP 400 "Item price cannot be negative."

Code path:
- [O] Extract Item object from request body
- [X] `if (name null or empty)` - "Test" is valid
- [O] `if (price < 0)` - -5.0 < 0, enters condition
- [O] Return HTTP 400 "Item price cannot be negative."
- [X] Store validation - not reached
- [X] Item creation - not reached

#### I3 (REUSING - createItemWithInvalidStoreTest)

Input: `{"name": "Test", "price": 10.0, "storeId": 9999, "category": "cat"}`

Expected: HTTP 400 "Store does not exist."

Code path:
- [O] Extract Item object from request body
- [X] `if (name null or empty)` - "Test" is valid
- [X] `if (price < 0)` - 10.0 >= 0
- [O] `if (store not exists)` - assume store 9999 doesn't exist, enters condition
- [O] Return HTTP 400 "Store does not exist."
- [X] Item creation - not reached

---

# PART TWO

## Part 2.A

### My Thoughts:
We definitely need to add this advertising functionality in all of the API endpoints. So this requires creating an ad management service, a unified response wrapper (return original output + ad), and updating each endpoint's return logic with the wrapper.

### 1. New Component: AdService & Ad

**Purpose**: Provides random ad selection from the adbase.

**Pseudocode**:

`service/AdService.java`
```
class AdService:
    private adDatabase: List<Ad> administrators

    method getRandomAd() -> Ad:
        randomIndex = generateRandomNumber(0, adDatabase.size() - 1)
        return adDatabase.get(randomIndex)
```

`model/Ad.java`
```
class Ad:
    id: int
    content: string
    .... (etc.)
```

### 2. Unified Response Wrapper

**Purpose**: Wraps all API responses with advertising data.

**Pseudocode**:

`model/ApiResponseWithAd.java`
```
class ApiResponseWithAd<T>:
    data: T
    advertisement: Ad

    constructor(data: T, ad: Ad):
        this.data = data
        this.advertisement = ad
```

### 3. Modified RouteController Structure

The controller needs to inject AdService and wrap all responses:

**Pseudocode**:

`controller/RouteController.java` (only modified)
```
class RouteController:
    private ...
    private ...
    private adService: AdService

    method wrapWithAd<T>(data: T) -> ApiResponseWithAd<T>:
        ad = adService.getRandomAd()
        return new ApiResponseWithAd<T>(data, ad)
```

### 4. Modifications for Each API Endpoint

All the API endpoints need to be modified to return advertisements. Each endpoint executes its original business logic, then wraps the result with a randomly selected ad before returning.

#### **Endpoint 1: GET /**
**What it does when called**:
```
method index() -> ApiResponseWithAd<String>:
    ...
    message = "Welcome to Coupon Management System API"
    return wrapWithAd(message)
```

#### **Endpoint 2: POST /store**
**What it does when called**:
```
method createStore(request: CreateStoreRequest) -> ApiResponseWithAd<Store>:
    ...
    store = dataService.addStore(request.name)
    return wrapWithAd(store)
```

#### **Endpoint 3: GET /store/{id}**
**What it does when called**:
```
method getStore(id: int) -> ApiResponseWithAd<Store>:
    ...
    store = dataService.getStore(id)
    if store == null:
        throw NotFoundException()
    return wrapWithAd(store)
```

#### **Endpoint 4: GET /stores**
**What it does when called**:
```
method getAllStores() -> ApiResponseWithAd<List<Store>>:
    ...
    stores = dataService.getAllStores()
    return wrapWithAd(stores)
```

#### **Endpoint 5: DELETE /store/{id}**
**What it does when called**:
```
method deleteStore(id: int) -> ApiResponseWithAd<String>:
    ...
    success = dataService.deleteStore(id)
    if !success:
        throw NotFoundException()
    message = "Store deleted successfully"
    return wrapWithAd(message)
```

#### **Endpoint 6: POST /item**
**What it does when called**:
```
method createItem(request: CreateItemRequest) -> ApiResponseWithAd<Item>:
    ...
    validateStore(request.storeId)
    item = dataService.addItem(request.item)
    return wrapWithAd(item)
```

#### **Endpoint 7: GET /item/{id}**
**What it does when called**:
```
method getItem(id: int) -> ApiResponseWithAd<Item>:
    ...
    item = dataService.getItem(id)
    if item == null:
        throw NotFoundException()
    return wrapWithAd(item)
```

#### **Endpoint 8: GET /items**
**What it does when called**:
```
method getAllItems() -> ApiResponseWithAd<List<Item>>:
    ...
    items = dataService.getAllItems()
    return wrapWithAd(items)
```

#### **Endpoint 9: GET /items/store/{storeId}**
**What it does when called**:
```
method getItemsByStore(storeId: int) -> ApiResponseWithAd<List<Item>>:
    ...
    items = dataService.getItemsByStore(storeId)
    return wrapWithAd(items)
```

#### **Endpoint 10: GET /items/search**
**What it does when called**:
```
method searchItems(keyword: String) -> ApiResponseWithAd<List<Item>>:
    ...
    items = dataService.searchItemsByKeyword(keyword)
    return wrapWithAd(items)
```

#### **Endpoint 11: GET /items/category/{category}**
**What it does when called**:
```
method getItemsByCategory(category: String) -> ApiResponseWithAd<List<Item>>:
    ...
    items = dataService.getItemsByCategory(category)
    return wrapWithAd(items)
```

#### **Endpoint 12: DELETE /item/{id}**
**What it does when called**:
```
method deleteItem(id: int) -> ApiResponseWithAd<String>:
    ...
    success = dataService.deleteItem(id)
    if !success:
        throw NotFoundException()
    message = "Item deleted successfully"
    return wrapWithAd(message)
```

#### **Endpoint 13: POST /coupon**
**What it does when called**:
```
method createCoupon(request: CreateCouponRequest) -> ApiResponseWithAd<Coupon>:
    ...
    validateStore(request.storeId)
    coupon = createCouponByType(request)
    dataService.addCoupon(coupon)
    return wrapWithAd(coupon)
```

#### **Endpoint 14: GET /coupon/{id}**
**What it does when called**:
```
method getCoupon(id: int) -> ApiResponseWithAd<Coupon>:
    ...
    coupon = dataService.getCoupon(id)
    if coupon == null:
        throw NotFoundException()
    return wrapWithAd(coupon)
```

#### **Endpoint 15: GET /coupons**
**What it does when called**:
```
method getAllCoupons() -> ApiResponseWithAd<List<Coupon>>:
    ...
    coupons = dataService.getAllCoupons()
    return wrapWithAd(coupons)
```

#### **Endpoint 16: GET /coupons/store/{storeId}**
**What it does when called**:
```
method getCouponsByStore(storeId: int) -> ApiResponseWithAd<List<Coupon>>:
    ...
    coupons = dataService.getCouponsByStore(storeId)
    return wrapWithAd(coupons)
```

#### **Endpoint 17: DELETE /coupon/{id}**
```
method deleteCoupon(id: int) -> ApiResponseWithAd<String>:
    ...
    success = dataService.deleteCoupon(id)
    if !success:
        throw NotFoundException()
    message = "Coupon deleted successfully"
    return wrapWithAd(message)
```

#### **Endpoint 18: POST /cart/optimal-coupon**
**What it does when called**:
```
method findOptimalCoupon(request: CartRequest) -> ApiResponseWithAd<Coupon>:
    ...
    optimalCoupon = couponService.findOptimalCoupon(request.itemIds, request.storeId)
    return wrapWithAd(optimalCoupon)
```

#### **Endpoint 19: GET /stores/optimal**
**What it does when called**:
```
method findOptimalStores(keyword: String, category: String) -> ApiResponseWithAd<List<StoreRecommendation>>:
    ...
    recommendations = couponService.findOptimalStoresForSearch(keyword, category)
    return wrapWithAd(recommendations)
```

#### **Endpoint 20: POST /cart/suggest-items**
**What it does when called**:
```
method suggestItems(request: SuggestItemsRequest) -> ApiResponseWithAd<List<Item>>:
    ...
    suggestions = couponService.findItemsToMeetCouponThreshold(
        request.itemIds, request.storeId, request.couponId)
    return wrapWithAd(suggestions)
```

---

## Part 2.B

### My Thoughts

I would first do **Unit tests** and then do **API tests**:

- **Unit Tests**: Test the new/modified individual components in isolation to make sure that AdService correctly selects ads and that controllers properly wrap responses.

- **API Tests**: Make real HTTP requests to verify that ads are delivered alongside original data through the complete request-response cycle. BTW also verify that business logic remains unchanged.

### 1. Existing Tests That Can Be Reused As-Is

**All Original Model Tests**:

- `StoreTest.java` - Tests Store model getters/setters
- `ItemTest.java` - Tests Item model getters/setters
- `TotalPriceCouponTest.java` - Tests coupon calculation logic
- `CategoryCouponTest.java` - Tests category-specific discount logic
- `ItemCouponTest.java` - Tests item-specific discount logic

**Rationale**: Unconcerned parts.

### 2. Existing Tests That Need Modifications

#### Only & All test cases RouteControllerTest.java

**Current State**: Tests all the endpoints by mocking services and verifying response structure and HTTP status codes.

**Changes Needed Across All Controller Tests**: Change assertion from checking response body directly to checking `response.getBody().data`. For the Ad-part tests, I would write them in the new test cases section.


**Example Modification for createStoreTest**:

**Current Test**:
```
createStoreTest():
    when(mockDataService.addStore(any(Store.class))).thenReturn(testStore)

    response = controller.createStore(testStore)

    assert response.getStatusCode() == CREATED
    assert response.getBody() != null
```

**Modified Test**:
```
createStoreTest():

    when(mockDataService.addStore(any(Store.class))).thenReturn(testStore)

    response = controller.createStore(testStore)

    assert response.getStatusCode() == CREATED
    assert response.getBody() instanceof ApiResponseWithAd
    assert response.getBody().data == testStore
```

### 3. New Tests to Write

#### 3.1 Unit Tests

**A. AdServiceTest.java**

**Purpose**: Validate random ad selection logic.

**Test Case 1: Random Ad Selection Success**
```
test_getRandomAd_returnsAdFromDatabase():
    adService = new AdService()

    selectedAd = adService.getRandomAd()

    assert selectedAd != null
    assert selectedAd.id > 0
    assert selectedAd.content != null
```

**Test Case 2: Non-Empty Adbase Guarantee**
```
test_getRandomAd_neverReturnsNull():
    adService = new AdService()

    for i in range(100):
        result = adService.getRandomAd()
        assert result != null
```

**B. Controller Integration Tests with Advertising**

**Purpose**: Verify that controllers properly wrap responses with ads.

**Test Case 1: All Well-functioned Endpoints Return Ads**
```
test_allEndpoints_returnAdvertisement():
    mockAd = new Ad(1, "Test Ad")
    when(adService.getRandomAd()).thenReturn(mockAd)

    endpoints = [
        {method: "GET", path: "/", expectedStatus: 200},
        {method: "POST", path: "/store", body: {name: "Test"}, expectedStatus: 201},
        {method: "GET", path: "/stores", expectedStatus: 200},
        // ... all 20 endpoints
    ]

    for endpoint in endpoints:
        response = executeRequest(endpoint.method, endpoint.path, endpoint.body)
        assert response.getStatusCode() == endpoint.expectedStatus
        assert response.getBody() instanceof ApiResponseWithAd
        assert response.getBody().advertisement == mockAd
```

**Test Case 2: Even error responses should include ads**
```
test_notFoundError_stillReturnsAd():
    mockAd = new Ad(1, "Error Ad")
    when(adService.getRandomAd()).thenReturn(mockAd)
    when(dataService.getStore(999)).thenReturn(null)

    response = controller.getStore(999) //Assumed non-existed above

    assert response.getStatusCode() == 404
    assert response.getBody() instanceof ApiResponseWithAd
    assert response.getBody().advertisement == mockAd
    assert response.getBody().data == null OR contains error message
```

---

#### 3.2 API Tests

**A. New API Integration Tests**

**Purpose**: Verify that ads are delivered correctly through the complete request-response cycle.

**Test Case 1: End-to-End Store Creation with Ad**
```
test_apiLevel_createStore_returnsStoreWithAd():
    requestBody = {name: "Integration Test Store"}

    response = POST("/store", requestBody)

    assert response.status == 201
    assert response.body.data != null
    assert response.body.data.name == "Integration Test Store"
    assert response.body.advertisement != null
```

**Test Case 2: Optimal Coupon with Ad**
```
test_apiLevel_optimalCoupon_returnsResultWithAd():
    store = createStore("Test Store")
    item1 = createItem("Item1", 50.0, store.id, "electronics")
    item2 = createItem("Item2", 30.0, store.id, "electronics")
    coupon = createCoupon(TotalPriceCoupon, store.id, 10.0, false, 60.0)

    requestBody = {itemIds: [item1.id, item2.id], storeId: store.id}

    response = POST("/cart/optimal-coupon", requestBody)

    assert response.status == 200
    assert response.body.data != null
    assert response.body.data.id == coupon.id
    assert response.body.advertisement != null
```

**B. Tests to Verify Original Functionality Is Not Broken**

**Test Case 1: Coupon Calculation Logic Preserved**
```
test_totalPriceCoupon_calculationStillWorks():
    // Setup
    store = createStore("Test Store")
    item1 = createItem("Item1", 50.0, store.id, "electronics")
    item2 = createItem("Item2", 30.0, store.id, "electronics")
    coupon = createCoupon(TotalPriceCoupon, store.id, 10.0, true, 60.0)  // 10% off, min $60

    // Input: Cart with total $80 (should get 10% discount = $8 off)
    requestBody = {itemIds: [item1.id, item2.id], storeId: store.id}

    response = POST("/cart/optimal-coupon", requestBody)

    // Verify original coupon logic works correctly
    assert response.body.data.id == coupon.id
    expectedDiscount = 80.0 * 0.10  // $8
    assert calculateDiscount(response.body.data, [item1, item2]) == expectedDiscount
    // AND verify ad is present
    assert response.body.advertisement != null
```

**Test Case 2: Store Recommendation Logic Preserved**
```
test_optimalStores_rankingStillCorrect():
    store1 = createStore("Cheap Store")
    store2 = createStore("Expensive Store")
    item1 = createItem("Laptop", 800.0, store1.id, "electronics")
    item2 = createItem("Laptop", 1000.0, store2.id, "electronics")
    coupon1 = createCoupon(TotalPriceCoupon, store1.id, 50.0, false, 500.0)

    response = GET("/stores/optimal?keyword=Laptop")

    // Verify original ranking logic: cheaper store should be first
    assert response.body.data[0].store.id == store1.id
    assert response.body.data[0].finalPrice < response.body.data[1].finalPrice
    assert response.body.advertisement != null
```

**Test Case 3: Item Suggestion Logic Preserved**
```
test_suggestItems_thresholdLogicStillWorks():
    store = createStore("Test Store")
    item1 = createItem("Item1", 30.0, store.id, "electronics")
    item2 = createItem("Item2", 20.0, store.id, "electronics")
    item3 = createItem("Item3", 15.0, store.id, "electronics")
    coupon = createCoupon(TotalPriceCoupon, store.id, 10.0, false, 60.0)  // $10 off, min $60

    requestBody = {itemIds: [item1.id, item2.id], storeId: store.id, couponId: coupon.id}

    response = POST("/cart/suggest-items", requestBody)

    // Verify original suggestion logic: should suggest item3 ($15) to reach threshold
    assert response.body.data.size() > 0
    assert response.body.data.contains(item3)
    currentTotal = 50.0
    suggestedItemPrice = response.body.data[0].price
    assert currentTotal + suggestedItemPrice >= 60.0
    assert response.body.advertisement != null
```