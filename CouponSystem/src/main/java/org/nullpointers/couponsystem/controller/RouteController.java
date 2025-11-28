package org.nullpointers.couponsystem.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.nullpointers.couponsystem.model.CategoryCoupon;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.ItemCoupon;
import org.nullpointers.couponsystem.model.Store;
import org.nullpointers.couponsystem.model.TotalPriceCoupon;
import org.nullpointers.couponsystem.service.CouponService;
import org.nullpointers.couponsystem.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle routing for the application.
 */
@RestController
public class RouteController {
  private final DataService dataService;
  private final CouponService couponService;

  @Autowired
  public RouteController(DataService dataService, CouponService couponService) {
    this.dataService = dataService;
    this.couponService = couponService;
  }

  @GetMapping({"/", "/index"})
  public String index() {
    return "Welcome to the Coupon Management System! "
        + "Direct your browser or Postman to an endpoint to make API calls.";
  }

  // ===== Store Endpoints =====

  /**
   * Creates a new store.
   *
   * @param store the Store object to create
   * @return ResponseEntity with created store and HTTP 201, or error with HTTP 400
   */
  @PostMapping("/store")
  public ResponseEntity<?> createStore(@RequestBody Store store) {
    try {
      // Validate store name is not null or empty
      if (store.getName() == null || store.getName().trim().isEmpty()) {
        return new ResponseEntity<>("Store name cannot be empty.",
            HttpStatus.BAD_REQUEST);
      }
      Store created = dataService.addStore(store);
      return new ResponseEntity<>(created, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Error creating store: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Retrieves a store by ID.
   *
   * @param id the store ID
   * @return ResponseEntity with store and HTTP 200, or error with HTTP 404
   */
  @GetMapping("/store/{id}")
  public ResponseEntity<?> getStore(@PathVariable int id) {
    Store store = dataService.getStore(id);
    if (store == null) {
      return new ResponseEntity<>("Store not found.", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(store, HttpStatus.OK);
  }

  /**
   * Retrieves all stores.
   *
   * @return ResponseEntity with list of stores and HTTP 200
   */
  @GetMapping("/stores")
  public ResponseEntity<?> getAllStores() {
    return new ResponseEntity<>(dataService.getAllStores(), HttpStatus.OK);
  }

  /**
   * Deletes a store by ID.
   *
   * @param id the store ID
   * @return ResponseEntity with success message and HTTP 200, or error with HTTP 404
   */
  @DeleteMapping("/store/{id}")
  public ResponseEntity<?> deleteStore(@PathVariable int id) {
    boolean deleted = dataService.deleteStore(id);
    if (!deleted) {
      return new ResponseEntity<>("Store not found.", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Store deleted successfully.", HttpStatus.OK);
  }

  // ===== Item Endpoints =====

  /**
   * Creates a new item.
   *
   * @param item the Item object to create
   * @return ResponseEntity with created item and HTTP 201, or error with HTTP 400
   */
  @PostMapping("/item")
  public ResponseEntity<?> createItem(@RequestBody Item item) {
    try {
      // Validate item name is not null or empty
      if (item.getName() == null || item.getName().trim().isEmpty()) {
        return new ResponseEntity<>("Item name cannot be empty.",
            HttpStatus.BAD_REQUEST);
      }
      // Validate item price is not negative
      if (item.getPrice() < 0) {
        return new ResponseEntity<>("Item price cannot be negative.",
            HttpStatus.BAD_REQUEST);
      }
      // Validate that the store exists
      if (dataService.getStore(item.getStoreId()) == null) {
        return new ResponseEntity<>("Store does not exist.", HttpStatus.BAD_REQUEST);
      }
      Item created = dataService.addItem(item);
      return new ResponseEntity<>(created, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Error creating item: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Retrieves an item by ID.
   *
   * @param id the item ID
   * @return ResponseEntity with item and HTTP 200, or error with HTTP 404
   */
  @GetMapping("/item/{id}")
  public ResponseEntity<?> getItem(@PathVariable int id) {
    Item item = dataService.getItem(id);
    if (item == null) {
      return new ResponseEntity<>("Item not found.", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(item, HttpStatus.OK);
  }

  /**
   * Retrieves all items.
   *
   * @return ResponseEntity with list of items and HTTP 200
   */
  @GetMapping("/items")
  public ResponseEntity<?> getAllItems() {
    return new ResponseEntity<>(dataService.getAllItems(), HttpStatus.OK);
  }

  /**
   * Retrieves all items from a specific store.
   *
   * @param storeId the store ID
   * @return ResponseEntity with list of items and HTTP 200
   */
  @GetMapping("/items/store/{storeId}")
  public ResponseEntity<?> getItemsByStore(@PathVariable int storeId) {
    return new ResponseEntity<>(dataService.getItemsByStore(storeId), HttpStatus.OK);
  }

  /**
   * Searches for items by keyword.
   *
   * @param keyword the search keyword
   * @return ResponseEntity with list of matching items and HTTP 200
   */
  @GetMapping("/items/search")
  public ResponseEntity<?> searchItems(@RequestParam String keyword) {
    return new ResponseEntity<>(dataService.searchItemsByKeyword(keyword), HttpStatus.OK);
  }

  /**
   * Retrieves items by category.
   *
   * @param category the category name
   * @return ResponseEntity with list of items and HTTP 200
   */
  @GetMapping("/items/category/{category}")
  public ResponseEntity<?> getItemsByCategory(@PathVariable String category) {
    return new ResponseEntity<>(dataService.getItemsByCategory(category), HttpStatus.OK);
  }

  /**
   * Deletes an item by ID.
   *
   * @param id the item ID
   * @return ResponseEntity with success message and HTTP 200, or error with HTTP 404
   */
  @DeleteMapping("/item/{id}")
  public ResponseEntity<?> deleteItem(@PathVariable int id) {
    boolean deleted = dataService.deleteItem(id);
    if (!deleted) {
      return new ResponseEntity<>("Item not found.", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Item deleted successfully.", HttpStatus.OK);
  }

  // ===== Coupon Endpoints =====

  /**
   * Creates a new coupon. Accepts different coupon types via a request body.
   *
   * @param requestBody map containing coupon details and type
   * @return ResponseEntity with created coupon and HTTP 201, or error with HTTP 400
   */
  @PostMapping("/coupon")
  public ResponseEntity<?> createCoupon(@RequestBody Map<String, Object> requestBody) {
    try {
      final String type = (String) requestBody.get("type");
      int storeId = ((Number) requestBody.get("storeId")).intValue();
      double discountValue = ((Number) requestBody.get("discountValue")).doubleValue();
      boolean isPercentage = (Boolean) requestBody.get("isPercentage");

      // Validate discount value is not negative
      if (discountValue < 0) {
        return new ResponseEntity<>("Discount value cannot be negative.",
            HttpStatus.BAD_REQUEST);
      }

      // Validate percentage discount is not > 100
      if (isPercentage && discountValue > 100) {
        return new ResponseEntity<>("Percentage discount cannot exceed 100%.",
            HttpStatus.BAD_REQUEST);
      }

      // Validate store exists
      if (dataService.getStore(storeId) == null) {
        return new ResponseEntity<>("Store does not exist.", HttpStatus.BAD_REQUEST);
      }

      Coupon coupon;

      switch (type.toLowerCase()) {
        case "totalprice":
          double minPurchase = ((Number) requestBody.get("minimumPurchase")).doubleValue();
          // Validate minimum purchase is not negative
          if (minPurchase < 0) {
            return new ResponseEntity<>("Minimum purchase cannot be negative.",
                HttpStatus.BAD_REQUEST);
          }
          coupon = new TotalPriceCoupon(0, storeId, discountValue, isPercentage, minPurchase);
          break;
        case "category":
          String category = (String) requestBody.get("category");
          // Validate category is not null or empty
          if (category == null || category.trim().isEmpty()) {
            return new ResponseEntity<>("Category cannot be empty.",
                HttpStatus.BAD_REQUEST);
          }
          coupon = new CategoryCoupon(0, storeId, discountValue, isPercentage, category);
          break;
        case "item":
          int targetItemId = ((Number) requestBody.get("targetItemId")).intValue();
          // Validate target item exists
          if (dataService.getItem(targetItemId) == null) {
            return new ResponseEntity<>("Target item does not exist.",
                HttpStatus.BAD_REQUEST);
          }
          coupon = new ItemCoupon(0, storeId, discountValue, isPercentage, targetItemId);
          break;
        default:
          return new ResponseEntity<>("Invalid coupon type.", HttpStatus.BAD_REQUEST);
      }

      Coupon created = dataService.addCoupon(coupon);
      return new ResponseEntity<>(created, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Error creating coupon: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Retrieves a coupon by ID.
   *
   * @param id the coupon ID
   * @return ResponseEntity with coupon and HTTP 200, or error with HTTP 404
   */
  @GetMapping("/coupon/{id}")
  public ResponseEntity<?> getCoupon(@PathVariable int id) {
    Coupon coupon = dataService.getCoupon(id);
    if (coupon == null) {
      return new ResponseEntity<>("Coupon not found.", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(coupon, HttpStatus.OK);
  }

  /**
   * Retrieves all coupons.
   *
   * @return ResponseEntity with list of coupons and HTTP 200
   */
  @GetMapping("/coupons")
  public ResponseEntity<?> getAllCoupons() {
    return new ResponseEntity<>(dataService.getAllCoupons(), HttpStatus.OK);
  }

  /**
   * Retrieves all coupons from a specific store.
   *
   * @param storeId the store ID
   * @return ResponseEntity with list of coupons and HTTP 200
   */
  @GetMapping("/coupons/store/{storeId}")
  public ResponseEntity<?> getCouponsByStore(@PathVariable int storeId) {
    return new ResponseEntity<>(dataService.getCouponsByStore(storeId), HttpStatus.OK);
  }

  /**
   * Deletes a coupon by ID.
   *
   * @param id the coupon ID
   * @return ResponseEntity with success message and HTTP 200, or error with HTTP 404
   */
  @DeleteMapping("/coupon/{id}")
  public ResponseEntity<?> deleteCoupon(@PathVariable int id) {
    boolean deleted = dataService.deleteCoupon(id);
    if (!deleted) {
      return new ResponseEntity<>("Coupon not found.", HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Coupon deleted successfully.", HttpStatus.OK);
  }

  // ===== Core Functionality Endpoints =====

  /**
   * Finds the optimal coupon for a cart of items from a specific store.
   *
   * @param requestBody map containing itemIds (array) and storeId
   * @return ResponseEntity with optimal coupon and discount info, or message if none found
   */
  @PostMapping("/cart/optimal-coupon")
  public ResponseEntity<?> findOptimalCoupon(@RequestBody Map<String, Object> requestBody) {
    try {
      @SuppressWarnings("unchecked")
      ArrayList<Integer> itemIdsList = (ArrayList<Integer>) requestBody.get("itemIds");
      int storeId = ((Number) requestBody.get("storeId")).intValue();

      // Validate cart is not empty
      if (itemIdsList == null || itemIdsList.isEmpty()) {
        return new ResponseEntity<>("Cart cannot be empty.",
            HttpStatus.BAD_REQUEST);
      }

      int[] itemIds = itemIdsList.stream().mapToInt(Integer::intValue).toArray();

      // Validate all items exist
      for (int itemId : itemIds) {
        if (dataService.getItem(itemId) == null) {
          return new ResponseEntity<>("Item with ID " + itemId + " does not exist.",
              HttpStatus.BAD_REQUEST);
        }
      }

      Coupon optimalCoupon = couponService.findOptimalCoupon(itemIds, storeId);

      if (optimalCoupon == null) {
        return new ResponseEntity<>("No applicable coupon found.", HttpStatus.OK);
      }

      // Calculate discount amount
      Item[] items = new Item[itemIds.length];
      for (int i = 0; i < itemIds.length; i++) {
        items[i] = dataService.getItem(itemIds[i]);
      }
      double discount = optimalCoupon.calculateDiscount(items);

      Map<String, Object> response = new HashMap<>();
      response.put("coupon", optimalCoupon);
      response.put("discount", discount);

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error finding optimal coupon: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Finds optimal stores for purchasing items matching a search criteria.
   *
   * @param keyword search keyword (optional)
   * @param category category filter (optional)
   * @return ResponseEntity with list of store recommendations sorted by price
   */
  @GetMapping("/stores/optimal")
  public ResponseEntity<?> findOptimalStores(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String category) {
    try {
      if ((keyword == null || keyword.isEmpty()) && (category == null || category.isEmpty())) {
        return new ResponseEntity<>("Either keyword or category must be provided.",
            HttpStatus.BAD_REQUEST);
      }

      ArrayList<CouponService.StoreRecommendation> recommendations =
          couponService.findOptimalStoresForSearch(keyword, category);

      if (recommendations.isEmpty()) {
        return new ResponseEntity<>("No matching items found.", HttpStatus.OK);
      }

      return new ResponseEntity<>(recommendations, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error finding optimal stores: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Suggests items to add to cart to meet a TotalPriceCoupon threshold.
   *
   * @param requestBody map containing itemIds (array), storeId, and couponId
   * @return ResponseEntity with suggested items to add
   */
  @PostMapping("/cart/suggest-items")
  public ResponseEntity<?> suggestItemsForCoupon(@RequestBody Map<String, Object> requestBody) {
    try {
      @SuppressWarnings("unchecked")
      ArrayList<Integer> itemIdsList = (ArrayList<Integer>) requestBody.get("itemIds");
      int storeId = ((Number) requestBody.get("storeId")).intValue();
      int couponId = ((Number) requestBody.get("couponId")).intValue();

      // Validate cart is not empty
      if (itemIdsList == null || itemIdsList.isEmpty()) {
        return new ResponseEntity<>("Cart cannot be empty.",
            HttpStatus.BAD_REQUEST);
      }

      int[] itemIds = itemIdsList.stream().mapToInt(Integer::intValue).toArray();

      // Validate all items exist
      for (int itemId : itemIds) {
        if (dataService.getItem(itemId) == null) {
          return new ResponseEntity<>("Item with ID " + itemId + " does not exist.",
              HttpStatus.BAD_REQUEST);
        }
      }

      // Note: Coupon validation is handled by service layer
      // Non-existent coupons will result in empty suggestions (not an error)
      ArrayList<Item> suggestions =
          couponService.findItemsToMeetCouponThreshold(itemIds, storeId, couponId);

      if (suggestions.isEmpty()) {
        return new ResponseEntity<>(
            "Either cart already meets threshold, or coupon is invalid.", HttpStatus.OK);
      }

      return new ResponseEntity<>(suggestions, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error suggesting items: " + e.getMessage(),
          HttpStatus.BAD_REQUEST);
    }
  }

}