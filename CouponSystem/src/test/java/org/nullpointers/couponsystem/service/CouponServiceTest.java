package org.nullpointers.couponsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nullpointers.couponsystem.model.CategoryCoupon;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.ItemCoupon;
import org.nullpointers.couponsystem.model.Store;
import org.nullpointers.couponsystem.model.TotalPriceCoupon;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for the CouponService class using mocking.
 */
@SpringBootTest
@ActiveProfiles("test1")
public class CouponServiceTest {
  private CouponService couponService;
  private DataService mockDataService;
  private ArrayList<Coupon> testCoupons;
  private ArrayList<Item> testItems;
  private ArrayList<Store> testStores;

  /**
   * Sets up test data and mocks before each test.
   */
  @BeforeEach
  public void setUp() {
    mockDataService = mock(DataService.class);
    couponService = new CouponService(mockDataService);

    // Create test items
    testItems = new ArrayList<>();
    testItems.add(new Item(1, "Book1", 30.0, 1, "books"));
    testItems.add(new Item(2, "Book2", 40.0, 1, "books"));
    testItems.add(new Item(3, "Toy1", 20.0, 1, "toys"));
    testItems.add(new Item(4, "Book3", 25.0, 2, "books"));

    // Create test stores
    testStores = new ArrayList<>();
    testStores.add(new Store(1, "Store 1"));
    testStores.add(new Store(2, "Store 2"));

    // Create test coupons
    testCoupons = new ArrayList<>();
    testCoupons.add(new TotalPriceCoupon(1, 1, 10.0, true, 50.0));
    testCoupons.add(new CategoryCoupon(2, 1, 5.0, false, "books"));
    testCoupons.add(new ItemCoupon(3, 1, 15.0, true, 1));
  }

  @Test
  public void findOptimalCouponWithValidCartTest() {
    // Partition: itemIds array size=2 (typical valid)
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getItem(2)).thenReturn(testItems.get(1));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{1, 2}, 1);

    assertNotNull(optimal);
    assertEquals(1, optimal.getId());  // TotalPriceCoupon gives most discount
  }

  @Test
  public void findOptimalCouponWithSingleItemCartTest() {
    // Partition: itemIds array size=1 (AT lower valid boundary)
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{1}, 1);

    assertNotNull(optimal);  // ItemCoupon applies to single item
  }

  @Test
  public void findOptimalCouponWithNoApplicableCouponsTest() {
    // Partition: itemIds array size=1 with no applicable coupons
    when(mockDataService.getItem(3)).thenReturn(testItems.get(2));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{3}, 1);

    assertNull(optimal);  // No coupons apply to toys only with low price
  }

  @Test
  public void findOptimalCouponWithInvalidItemIdTest() {
    // Partition: itemId=999 (ABOVE maximum existing ID - invalid)
    when(mockDataService.getItem(999)).thenReturn(null);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{999}, 1);

    assertNull(optimal);
  }

  @Test
  public void findOptimalCouponWithZeroItemIdTest() {
    // Partition: itemId=0 (BELOW minimum valid ID - invalid)
    when(mockDataService.getItem(0)).thenReturn(null);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{0}, 1);

    assertNull(optimal);
  }

  @Test
  public void findOptimalCouponWithNegativeItemIdTest() {
    // Partition: itemId=-1 (BELOW minimum valid ID - invalid)
    when(mockDataService.getItem(-1)).thenReturn(null);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{-1}, 1);

    assertNull(optimal);
  }

  @Test
  public void findOptimalCouponWithEmptyCartTest() {
    // Partition: itemIds array size=0 (BELOW minimum valid size - invalid)
    Coupon optimal = couponService.findOptimalCoupon(new int[]{}, 1);

    assertNull(optimal);
  }

  @Test
  public void findOptimalCouponWithNullItemIdsTest() {
    // Partition: itemIds array is null (invalid)
    Coupon optimal = couponService.findOptimalCoupon(null, 1);

    assertNull(optimal);
  }

  @Test
  public void findOptimalCouponWithLargeCartTest() {
    // Partition: itemIds array size=4 (ABOVE typical size - valid)
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getItem(2)).thenReturn(testItems.get(1));
    when(mockDataService.getItem(3)).thenReturn(testItems.get(2));
    when(mockDataService.getItem(4)).thenReturn(testItems.get(3));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);

    Coupon optimal = couponService.findOptimalCoupon(new int[]{1, 2, 3, 4}, 1);

    assertNotNull(optimal);
  }

  @Test
  public void findOptimalCouponWithZeroStoreIdTest() {
    // Partition: storeId=0 (BELOW minimum valid ID - invalid)
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getCouponsByStore(0)).thenReturn(new ArrayList<>());

    Coupon optimal = couponService.findOptimalCoupon(new int[]{1}, 0);

    assertNull(optimal);  // No coupons for invalid store
  }

  @Test
  public void findOptimalCouponWithNegativeStoreIdTest() {
    // Partition: storeId=-1 (BELOW minimum valid ID - invalid)
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getCouponsByStore(-1)).thenReturn(new ArrayList<>());

    Coupon optimal = couponService.findOptimalCoupon(new int[]{1}, -1);

    assertNull(optimal);  // No coupons for invalid store
  }

  @Test
  public void findOptimalStoresForSearchByKeywordTest() {
    when(mockDataService.searchItemsByKeyword("Book")).thenReturn(
        new ArrayList<>(testItems.subList(0, 3)));
    when(mockDataService.getAllStores()).thenReturn(testStores);
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getItem(2)).thenReturn(testItems.get(1));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);
    when(mockDataService.getCouponsByStore(2)).thenReturn(new ArrayList<>());

    ArrayList<CouponService.StoreRecommendation> recommendations = 
        couponService.findOptimalStoresForSearch("Book", null);

    assertNotNull(recommendations);
    assertTrue(recommendations.size() > 0);
  }

  @Test
  public void findOptimalStoresForSearchByCategoryTest() {
    when(mockDataService.getItemsByCategory("books")).thenReturn(
        new ArrayList<>(testItems.subList(0, 2)));
    when(mockDataService.getAllStores()).thenReturn(testStores);
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);

    ArrayList<CouponService.StoreRecommendation> recommendations = 
        couponService.findOptimalStoresForSearch(null, "books");

    assertNotNull(recommendations);
    assertTrue(recommendations.size() > 0);
  }

  @Test
  public void findOptimalStoresWithNoMatchingItemsTest() {
    when(mockDataService.searchItemsByKeyword("NonExistent")).thenReturn(new ArrayList<>());

    ArrayList<CouponService.StoreRecommendation> recommendations =
        couponService.findOptimalStoresForSearch("NonExistent", null);

    assertTrue(recommendations.isEmpty());
  }

  @Test
  public void findOptimalStoresForSearchWithNullCategoryTest() {
    when(mockDataService.searchItemsByKeyword("Book")).thenReturn(
        new ArrayList<>(testItems.subList(0, 2)));
    when(mockDataService.getAllStores()).thenReturn(testStores);
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getCouponsByStore(1)).thenReturn(testCoupons);

    ArrayList<CouponService.StoreRecommendation> recommendations =
        couponService.findOptimalStoresForSearch("Book", null);

    assertNotNull(recommendations);
    assertTrue(recommendations.size() > 0);
  }

  @Test
  public void findItemsToMeetCouponThresholdTest() {
    TotalPriceCoupon coupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
    when(mockDataService.getCoupon(1)).thenReturn(coupon);
    when(mockDataService.getItem(3)).thenReturn(testItems.get(2));
    
    ArrayList<Item> storeItems = new ArrayList<>();
    storeItems.add(new Item(5, "Cheap1", 10.0, 1, "misc"));
    storeItems.add(new Item(6, "Cheap2", 15.0, 1, "misc"));
    storeItems.add(new Item(7, "Cheap3", 20.0, 1, "misc"));
    when(mockDataService.getItemsByStore(1)).thenReturn(storeItems);

    ArrayList<Item> suggestions = 
        couponService.findItemsToMeetCouponThreshold(new int[]{3}, 1, 1);

    assertNotNull(suggestions);
    assertTrue(suggestions.size() > 0);
  }

  @Test
  public void findItemsToMeetCouponThresholdAlreadyMetTest() {
    TotalPriceCoupon coupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
    when(mockDataService.getCoupon(1)).thenReturn(coupon);
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getItem(2)).thenReturn(testItems.get(1));

    ArrayList<Item> suggestions = 
        couponService.findItemsToMeetCouponThreshold(new int[]{1, 2}, 1, 1);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  public void findItemsToMeetCouponThresholdWithWrongCouponTypeTest() {
    CategoryCoupon coupon = new CategoryCoupon(2, 1, 5.0, false, "books");
    when(mockDataService.getCoupon(2)).thenReturn(coupon);

    ArrayList<Item> suggestions = 
        couponService.findItemsToMeetCouponThreshold(new int[]{1}, 1, 2);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  public void findItemsToMeetCouponThresholdWithWrongStoreTest() {
    TotalPriceCoupon coupon = new TotalPriceCoupon(1, 2, 10.0, true, 50.0);
    when(mockDataService.getCoupon(1)).thenReturn(coupon);
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));

    ArrayList<Item> suggestions =
        couponService.findItemsToMeetCouponThreshold(new int[]{1}, 1, 1);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  public void findItemsToMeetCouponThresholdWithMixedStoreItemsTest() {
    TotalPriceCoupon coupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
    when(mockDataService.getCoupon(1)).thenReturn(coupon);
    when(mockDataService.getItem(1)).thenReturn(testItems.get(0));
    when(mockDataService.getItem(4)).thenReturn(testItems.get(3));

    ArrayList<Item> storeItems = new ArrayList<>();
    storeItems.add(new Item(5, "Cheap1", 10.0, 1, "misc"));
    when(mockDataService.getItemsByStore(1)).thenReturn(storeItems);

    ArrayList<Item> suggestions =
        couponService.findItemsToMeetCouponThreshold(new int[]{1, 4}, 1, 1);

    assertNotNull(suggestions);
    assertTrue(suggestions.size() > 0);
  }

  @Test
  public void storeRecommendationTest() {
    Store store = new Store(1, "Store 1");
    Item item = new Item(1, "Item", 10.0, 1, "cat");
    Coupon coupon = new TotalPriceCoupon(1, 1, 5.0, false, 20.0);
    
    CouponService.StoreRecommendation recommendation = 
        new CouponService.StoreRecommendation(store, item, coupon, 5.0, 5.0);

    assertEquals(store, recommendation.getStore());
    assertEquals(item, recommendation.getItem());
    assertEquals(coupon, recommendation.getCoupon());
    assertEquals(5.0, recommendation.getFinalPrice(), 0.001);
    assertEquals(5.0, recommendation.getDiscount(), 0.001);
  }
}

