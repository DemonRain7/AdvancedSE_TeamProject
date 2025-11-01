package org.nullpointers.couponsystem.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nullpointers.couponsystem.model.CategoryCoupon;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.Store;
import org.nullpointers.couponsystem.model.TotalPriceCoupon;
import org.nullpointers.couponsystem.service.CouponService;
import org.nullpointers.couponsystem.service.DataService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for the RouteController class using mocking.
 */
@SpringBootTest
public class RouteControllerTest {
  private RouteController controller;
  private DataService mockDataService;
  private CouponService mockCouponService;
  private Store testStore;
  private Item testItem;
  private Coupon testCoupon;

  /**
   * Sets up test data and mocks before each test.
   */
  @BeforeEach
  public void setUp() {
    mockDataService = mock(DataService.class);
    mockCouponService = mock(CouponService.class);
    controller = new RouteController(mockDataService, mockCouponService);

    testStore = new Store(1, "Test Store");
    testItem = new Item(1, "Test Item", 50.0, 1, "books");
    testCoupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
  }

  @Test
  public void indexTest() {
    String result = controller.index();
    assertTrue(result.contains("Welcome"));
    assertTrue(result.contains("Coupon Management System"));
  }

  // ===== Store Endpoint Tests =====

  @Test
  public void createStoreTest() {
    when(mockDataService.addStore(any(Store.class))).thenReturn(testStore);

    ResponseEntity<?> response = controller.createStore(testStore);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  public void getStoreWhenExistsTest() {
    when(mockDataService.getStore(1)).thenReturn(testStore);

    ResponseEntity<?> response = controller.getStore(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testStore, response.getBody());
  }

  @Test
  public void getStoreWhenNotFoundTest() {
    when(mockDataService.getStore(999)).thenReturn(null);

    ResponseEntity<?> response = controller.getStore(999);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getAllStoresTest() {
    ArrayList<Store> stores = new ArrayList<>();
    stores.add(testStore);
    when(mockDataService.getAllStores()).thenReturn(stores);

    ResponseEntity<?> response = controller.getAllStores();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  public void deleteStoreWhenExistsTest() {
    when(mockDataService.deleteStore(1)).thenReturn(true);

    ResponseEntity<?> response = controller.deleteStore(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void deleteStoreWhenNotFoundTest() {
    when(mockDataService.deleteStore(999)).thenReturn(false);

    ResponseEntity<?> response = controller.deleteStore(999);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ===== Item Endpoint Tests =====

  @Test
  public void createItemWithValidStoreTest() {
    when(mockDataService.getStore(1)).thenReturn(testStore);
    when(mockDataService.addItem(any(Item.class))).thenReturn(testItem);

    ResponseEntity<?> response = controller.createItem(testItem);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void createItemWithInvalidStoreTest() {
    when(mockDataService.getStore(999)).thenReturn(null);
    Item invalidItem = new Item(1, "Test", 10.0, 999, "cat");

    ResponseEntity<?> response = controller.createItem(invalidItem);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void getItemWhenExistsTest() {
    when(mockDataService.getItem(1)).thenReturn(testItem);

    ResponseEntity<?> response = controller.getItem(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testItem, response.getBody());
  }

  @Test
  public void getItemWhenNotFoundTest() {
    when(mockDataService.getItem(999)).thenReturn(null);

    ResponseEntity<?> response = controller.getItem(999);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getAllItemsTest() {
    ArrayList<Item> items = new ArrayList<>();
    items.add(testItem);
    when(mockDataService.getAllItems()).thenReturn(items);

    ResponseEntity<?> response = controller.getAllItems();

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void getItemsByStoreTest() {
    ArrayList<Item> items = new ArrayList<>();
    items.add(testItem);
    when(mockDataService.getItemsByStore(1)).thenReturn(items);

    ResponseEntity<?> response = controller.getItemsByStore(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void searchItemsTest() {
    ArrayList<Item> items = new ArrayList<>();
    items.add(testItem);
    when(mockDataService.searchItemsByKeyword(anyString())).thenReturn(items);

    ResponseEntity<?> response = controller.searchItems("Test");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void getItemsByCategoryTest() {
    ArrayList<Item> items = new ArrayList<>();
    items.add(testItem);
    when(mockDataService.getItemsByCategory("books")).thenReturn(items);

    ResponseEntity<?> response = controller.getItemsByCategory("books");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void deleteItemWhenExistsTest() {
    when(mockDataService.deleteItem(1)).thenReturn(true);

    ResponseEntity<?> response = controller.deleteItem(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void deleteItemWhenNotFoundTest() {
    when(mockDataService.deleteItem(999)).thenReturn(false);

    ResponseEntity<?> response = controller.deleteItem(999);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ===== Coupon Endpoint Tests =====

  @Test
  public void createTotalPriceCouponTest() {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("type", "totalprice");
    requestBody.put("storeId", 1);
    requestBody.put("discountValue", 10.0);
    requestBody.put("isPercentage", true);
    requestBody.put("minimumPurchase", 50.0);

    when(mockDataService.getStore(1)).thenReturn(testStore);
    when(mockDataService.addCoupon(any(Coupon.class))).thenReturn(testCoupon);

    ResponseEntity<?> response = controller.createCoupon(requestBody);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void createCategoryCouponTest() {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("type", "category");
    requestBody.put("storeId", 1);
    requestBody.put("discountValue", 5.0);
    requestBody.put("isPercentage", false);
    requestBody.put("category", "books");

    Coupon categoryCoupon = new CategoryCoupon(1, 1, 5.0, false, "books");
    when(mockDataService.getStore(1)).thenReturn(testStore);
    when(mockDataService.addCoupon(any(Coupon.class))).thenReturn(categoryCoupon);

    ResponseEntity<?> response = controller.createCoupon(requestBody);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void createItemCouponTest() {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("type", "item");
    requestBody.put("storeId", 1);
    requestBody.put("discountValue", 15.0);
    requestBody.put("isPercentage", true);
    requestBody.put("targetItemId", 1);

    when(mockDataService.getStore(1)).thenReturn(testStore);
    when(mockDataService.getItem(1)).thenReturn(testItem);
    when(mockDataService.addCoupon(any(Coupon.class))).thenReturn(testCoupon);

    ResponseEntity<?> response = controller.createCoupon(requestBody);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void createCouponWithInvalidTypeTest() {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("type", "invalid");
    requestBody.put("storeId", 1);
    requestBody.put("discountValue", 10.0);
    requestBody.put("isPercentage", true);

    ResponseEntity<?> response = controller.createCoupon(requestBody);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void getCouponWhenExistsTest() {
    when(mockDataService.getCoupon(1)).thenReturn(testCoupon);

    ResponseEntity<?> response = controller.getCoupon(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void getCouponWhenNotFoundTest() {
    when(mockDataService.getCoupon(999)).thenReturn(null);

    ResponseEntity<?> response = controller.getCoupon(999);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getAllCouponsTest() {
    ArrayList<Coupon> coupons = new ArrayList<>();
    coupons.add(testCoupon);
    when(mockDataService.getAllCoupons()).thenReturn(coupons);

    ResponseEntity<?> response = controller.getAllCoupons();

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void getCouponsByStoreTest() {
    ArrayList<Coupon> coupons = new ArrayList<>();
    coupons.add(testCoupon);
    when(mockDataService.getCouponsByStore(1)).thenReturn(coupons);

    ResponseEntity<?> response = controller.getCouponsByStore(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void deleteCouponWhenExistsTest() {
    when(mockDataService.deleteCoupon(1)).thenReturn(true);

    ResponseEntity<?> response = controller.deleteCoupon(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void deleteCouponWhenNotFoundTest() {
    when(mockDataService.deleteCoupon(999)).thenReturn(false);

    ResponseEntity<?> response = controller.deleteCoupon(999);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  // ===== Core Functionality Tests =====

  @Test
  public void findOptimalCouponTest() {
    Map<String, Object> requestBody = new HashMap<>();
    ArrayList<Integer> itemIds = new ArrayList<>();
    itemIds.add(1);
    itemIds.add(2);
    requestBody.put("itemIds", itemIds);
    requestBody.put("storeId", 1);

    when(mockDataService.getItem(anyInt())).thenReturn(testItem);
    when(mockCouponService.findOptimalCoupon(any(int[].class), anyInt()))
        .thenReturn(testCoupon);

    ResponseEntity<?> response = controller.findOptimalCoupon(requestBody);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void findOptimalCouponWhenNoneFoundTest() {
    Map<String, Object> requestBody = new HashMap<>();
    ArrayList<Integer> itemIds = new ArrayList<>();
    itemIds.add(1);
    requestBody.put("itemIds", itemIds);
    requestBody.put("storeId", 1);

    when(mockDataService.getItem(anyInt())).thenReturn(testItem);
    when(mockCouponService.findOptimalCoupon(any(int[].class), anyInt())).thenReturn(null);

    ResponseEntity<?> response = controller.findOptimalCoupon(requestBody);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("No applicable coupon"));
  }

  @Test
  public void findOptimalStoresWithKeywordTest() {
    ArrayList<CouponService.StoreRecommendation> recommendations = new ArrayList<>();
    when(mockCouponService.findOptimalStoresForSearch(anyString(), any()))
        .thenReturn(recommendations);

    ResponseEntity<?> response = controller.findOptimalStores("book", null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void findOptimalStoresWithCategoryTest() {
    ArrayList<CouponService.StoreRecommendation> recommendations = new ArrayList<>();
    when(mockCouponService.findOptimalStoresForSearch(any(), anyString()))
        .thenReturn(recommendations);

    ResponseEntity<?> response = controller.findOptimalStores(null, "books");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void findOptimalStoresWithNoParametersTest() {
    ResponseEntity<?> response = controller.findOptimalStores(null, null);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void suggestItemsForCouponTest() {
    Map<String, Object> requestBody = new HashMap<>();
    ArrayList<Integer> itemIds = new ArrayList<>();
    itemIds.add(1);
    requestBody.put("itemIds", itemIds);
    requestBody.put("storeId", 1);
    requestBody.put("couponId", 1);

    ArrayList<Item> suggestions = new ArrayList<>();
    suggestions.add(testItem);
    when(mockDataService.getItem(1)).thenReturn(testItem);
    when(mockCouponService.findItemsToMeetCouponThreshold(
        any(int[].class), anyInt(), anyInt())).thenReturn(suggestions);

    ResponseEntity<?> response = controller.suggestItemsForCoupon(requestBody);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void suggestItemsForCouponWhenNoSuggestionsTest() {
    Map<String, Object> requestBody = new HashMap<>();
    ArrayList<Integer> itemIds = new ArrayList<>();
    itemIds.add(1);
    requestBody.put("itemIds", itemIds);
    requestBody.put("storeId", 1);
    requestBody.put("couponId", 1);

    when(mockDataService.getItem(1)).thenReturn(testItem);
    when(mockCouponService.findItemsToMeetCouponThreshold(
        any(int[].class), anyInt(), anyInt())).thenReturn(new ArrayList<>());

    ResponseEntity<?> response = controller.suggestItemsForCoupon(requestBody);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}

