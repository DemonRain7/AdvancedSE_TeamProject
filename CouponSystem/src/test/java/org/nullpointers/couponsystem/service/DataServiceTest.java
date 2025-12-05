package org.nullpointers.couponsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.nullpointers.couponsystem.model.CategoryCoupon;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.Store;
import org.nullpointers.couponsystem.model.TotalPriceCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit tests for the DataService class.
 */
@SpringBootTest
@ActiveProfiles("test1")
@Transactional
public class DataServiceTest {
  @Autowired
  private DataService dataService;

  @Test
  public void addItemTest() {
    Store store = dataService.addStore(new Store(0, "Tech Store"));
    Item item = new Item(0, "Laptop", 999.99, store.getId(), "electronics");
    Item added = dataService.addItem(item);
    
    assertNotNull(added);
    assertTrue(added.getId() > 0);
    assertEquals("Laptop", added.getName());
  }

  @Test
  public void addItemWithExistingIdTest() {
    Store store = dataService.addStore(new Store(0, "Tech Store"));
    Item item = new Item(0, "Laptop", 999.99, store.getId(), "electronics");
    Item added = dataService.addItem(item);
    
    assertTrue(added.getId() > 0);
  }

  @Test
  public void addStoreTest() {
    Store store = new Store(0, "Tech Store");
    Store added = dataService.addStore(store);
    
    assertNotNull(added);
    assertTrue(added.getId() > 0);
    assertEquals("Tech Store", added.getName());
  }

  @Test
  public void addCouponTest() {
    Store store = dataService.addStore(new Store(0, "Tech Store"));
    Coupon coupon = new TotalPriceCoupon(0, store.getId(), 10.0, true, 50.0);
    Coupon added = dataService.addCoupon(coupon);
    
    assertNotNull(added);
    assertTrue(added.getId() > 0);
  }

  @Test
  public void getItemTest() {
    // Partition: Valid existing ID (typical valid)
    Store store = dataService.addStore(new Store(0, "Tech Store"));
    Item item = new Item(0, "Laptop", 999.99, store.getId(), "electronics");
    Item added = dataService.addItem(item);

    Item retrieved = dataService.getItem(added.getId());
    assertNotNull(retrieved);
    assertEquals(added.getId(), retrieved.getId());
  }

  @Test
  public void getItemNotFoundTest() {
    // Partition: ID=999 (ABOVE maximum existing ID - non-existent)
    Item retrieved = dataService.getItem(999);
    assertNull(retrieved);
  }

  @Test
  public void getItemWithZeroIdTest() {
    // Partition: ID=0 (BELOW minimum valid ID - invalid)
    Item retrieved = dataService.getItem(0);
    assertNull(retrieved);
  }

  @Test
  public void getItemWithNegativeIdTest() {
    // Partition: ID=-1 (BELOW minimum valid ID - invalid)
    Item retrieved = dataService.getItem(-1);
    assertNull(retrieved);
  }

  @Test
  public void getStoreTest() {
    // Partition: Valid existing ID (typical valid)
    Store store = new Store(0, "Tech Store");
    Store added = dataService.addStore(store);

    Store retrieved = dataService.getStore(added.getId());
    assertNotNull(retrieved);
    assertEquals(added.getId(), retrieved.getId());
  }

  @Test
  public void getStoreNotFoundTest() {
    // Partition: ID=999 (ABOVE maximum existing ID - invalid)
    Store retrieved = dataService.getStore(999);
    assertNull(retrieved);
  }

  @Test
  public void getStoreWithZeroIdTest() {
    // Partition: ID=0 (BELOW minimum valid ID - invalid)
    Store retrieved = dataService.getStore(0);
    assertNull(retrieved);
  }

  @Test
  public void getStoreWithNegativeIdTest() {
    // Partition: ID=-1 (BELOW minimum valid ID - invalid)
    Store retrieved = dataService.getStore(-1);
    assertNull(retrieved);
  }

  @Test
  public void getCouponTest() {
    // Partition: Valid existing ID (typical valid)
    Store store = dataService.addStore(new Store(0, "Tech Store"));
    Coupon coupon = new TotalPriceCoupon(0, store.getId(), 10.0, true, 50.0);
    Coupon added = dataService.addCoupon(coupon);

    Coupon retrieved = dataService.getCoupon(added.getId());
    assertNotNull(retrieved);
    assertEquals(added.getId(), retrieved.getId());
  }

  @Test
  public void getCouponNotFoundTest() {
    // Partition: ID=999 (ABOVE maximum existing ID - invalid)
    Coupon retrieved = dataService.getCoupon(999);
    assertNull(retrieved);
  }

  @Test
  public void getCouponWithZeroIdTest() {
    // Partition: ID=0 (BELOW minimum valid ID - invalid)
    Coupon retrieved = dataService.getCoupon(0);
    assertNull(retrieved);
  }

  @Test
  public void getCouponWithNegativeIdTest() {
    // Partition: ID=-1 (BELOW minimum valid ID - invalid)
    Coupon retrieved = dataService.getCoupon(-1);
    assertNull(retrieved);
  }

  @Test
  public void getAllItemsTest() {
    Store store = dataService.addStore(new Store(0, "Store1"));
    dataService.addItem(new Item(0, "Item1", 10.0, store.getId(), "cat1"));
    dataService.addItem(new Item(0, "Item2", 20.0, store.getId(), "cat2"));
    
    ArrayList<Item> items = dataService.getAllItems();
    assertEquals(2, items.size());
  }

  @Test
  public void getAllStoresTest() {
    dataService.addStore(new Store(0, "Store1"));
    dataService.addStore(new Store(0, "Store2"));
    
    ArrayList<Store> stores = dataService.getAllStores();
    assertEquals(2, stores.size());
  }

  @Test
  public void getAllCouponsTest() {
    Store store = dataService.addStore(new Store(0, "Store1"));
    dataService.addCoupon(new TotalPriceCoupon(0, store.getId(), 10.0, true, 50.0));
    dataService.addCoupon(new CategoryCoupon(0, store.getId(), 5.0, false, "books"));
    
    ArrayList<Coupon> coupons = dataService.getAllCoupons();
    assertEquals(2, coupons.size());
  }

  @Test
  public void getItemsByStoreTest() {
    Store store1 = dataService.addStore(new Store(0, "Store1"));
    Store store2 = dataService.addStore(new Store(0, "Store2"));
    dataService.addItem(new Item(0, "Item1", 10.0, store1.getId(), "cat1"));
    dataService.addItem(new Item(0, "Item2", 20.0, store1.getId(), "cat2"));
    dataService.addItem(new Item(0, "Item3", 30.0, store2.getId(), "cat1"));
    
    ArrayList<Item> store1Items = dataService.getItemsByStore(store1.getId());
    assertEquals(2, store1Items.size());
  }

  @Test
  public void getCouponsByStoreTest() {
    Store store1 = dataService.addStore(new Store(0, "Store1"));
    Store store2 = dataService.addStore(new Store(0, "Store2"));
    dataService.addCoupon(new TotalPriceCoupon(0, store1.getId(), 10.0, true, 50.0));
    dataService.addCoupon(new CategoryCoupon(0, store1.getId(), 5.0, false, "books"));
    dataService.addCoupon(new TotalPriceCoupon(0, store2.getId(), 15.0, false, 100.0));
    
    ArrayList<Coupon> store1Coupons = dataService.getCouponsByStore(store1.getId());
    assertEquals(2, store1Coupons.size());
  }

  @Test
  public void getItemsByCategoryTest() {
    Store store1 = dataService.addStore(new Store(0, "Store1"));
    Store store2 = dataService.addStore(new Store(0, "Store2"));
    dataService.addItem(new Item(0, "Book1", 10.0, store1.getId(), "books"));
    dataService.addItem(new Item(0, "Book2", 20.0, store2.getId(), "books"));
    dataService.addItem(new Item(0, "Toy1", 30.0, store1.getId(), "toys"));
    
    ArrayList<Item> bookItems = dataService.getItemsByCategory("books");
    assertEquals(2, bookItems.size());
  }

  @Test
  public void getItemsByCategoryCaseInsensitiveTest() {
    Store store1 = dataService.addStore(new Store(0, "Store1"));
    Store store2 = dataService.addStore(new Store(0, "Store2"));
    dataService.addItem(new Item(0, "Book1", 10.0, store1.getId(), "Books"));
    dataService.addItem(new Item(0, "Book2", 20.0, store2.getId(), "BOOKS"));
    
    ArrayList<Item> bookItems = dataService.getItemsByCategory("books");
    assertEquals(2, bookItems.size());
  }

  @Test
  public void searchItemsByKeywordTest() {
    Store store1 = dataService.addStore(new Store(0, "Store1"));
    dataService.addItem(new Item(0, "Gaming Laptop", 999.0, store1.getId(), "electronics"));
    dataService.addItem(new Item(0, "Office Laptop", 699.0, store1.getId(), "electronics"));
    dataService.addItem(new Item(0, "Gaming Mouse", 49.0, store1.getId(), "electronics"));
    
    ArrayList<Item> laptops = dataService.searchItemsByKeyword("Laptop");
    assertEquals(2, laptops.size());
  }

  @Test
  public void searchItemsByKeywordCaseInsensitiveTest() {
    Store store1 = dataService.addStore(new Store(0, "Store1"));
    dataService.addItem(new Item(0, "Gaming Laptop", 999.0, store1.getId(), "electronics"));
    
    ArrayList<Item> results = dataService.searchItemsByKeyword("laptop");
    assertEquals(1, results.size());
  }

  @Test
  public void deleteItemTest() {
    Store store = dataService.addStore(new Store(0, "Store1"));
    Item item = dataService.addItem(new Item(0, "Item1", 10.0, store.getId(), "cat1"));
    
    boolean deleted = dataService.deleteItem(item.getId());
    assertTrue(deleted);
    assertNull(dataService.getItem(item.getId()));
  }

  @Test
  public void deleteItemNotFoundTest() {
    // Partition: ID=999 (ABOVE maximum existing ID - invalid)
    boolean deleted = dataService.deleteItem(999);
    assertFalse(deleted);
  }

  @Test
  public void deleteItemWithZeroIdTest() {
    // Partition: ID=0 (BELOW minimum valid ID - invalid)
    boolean deleted = dataService.deleteItem(0);
    assertFalse(deleted);
  }

  @Test
  public void deleteItemWithNegativeIdTest() {
    // Partition: ID=-1 (BELOW minimum valid ID - invalid)
    boolean deleted = dataService.deleteItem(-1);
    assertFalse(deleted);
  }

  @Test
  public void deleteStoreTest() {
    Store store = dataService.addStore(new Store(0, "Store1"));
    
    boolean deleted = dataService.deleteStore(store.getId());
    assertTrue(deleted);
    assertNull(dataService.getStore(store.getId()));
  }

  @Test
  public void deleteCouponTest() {
    Store store = dataService.addStore(new Store(0, "Store1"));
    Coupon coupon = dataService.addCoupon(new TotalPriceCoupon(0, store.getId(), 10.0, true, 50.0));
    
    boolean deleted = dataService.deleteCoupon(coupon.getId());
    assertTrue(deleted);
    assertNull(dataService.getCoupon(coupon.getId()));
  }
}
