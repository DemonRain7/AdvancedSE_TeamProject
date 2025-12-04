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
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit tests for the DataService class.
 */
@SpringBootTest
@Transactional
public class DataServiceTest {
  @Autowired
  private DataService dataService;

  @Test
  public void addItemTest() {
    Item item = new Item(1, "Laptop", 999.99, 1, "electronics");
    Item added = dataService.addItem(item);
    
    assertNotNull(added);
    assertEquals(1, added.getId());
    assertEquals("Laptop", added.getName());
  }

  @Test
  public void addItemWithExistingIdTest() {
    Item item = new Item(5, "Laptop", 999.99, 1, "electronics");
    Item added = dataService.addItem(item);
    
    assertEquals(5, added.getId());
  }

  @Test
  public void addStoreTest() {
    Store store = new Store(1, "Tech Store");
    Store added = dataService.addStore(store);
    
    assertNotNull(added);
    assertEquals(1, added.getId());
    assertEquals("Tech Store", added.getName());
  }

  @Test
  public void addCouponTest() {
    Coupon coupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
    Coupon added = dataService.addCoupon(coupon);
    
    assertNotNull(added);
    assertEquals(1, added.getId());
  }

  @Test
  public void getItemTest() {
    Item item = new Item(1, "Laptop", 999.99, 1, "electronics");
    Item added = dataService.addItem(item);
    
    Item retrieved = dataService.getItem(added.getId());
    assertNotNull(retrieved);
    assertEquals(added.getId(), retrieved.getId());
  }

  @Test
  public void getItemNotFoundTest() {
    Item retrieved = dataService.getItem(999);
    assertNull(retrieved);
  }

  @Test
  public void getStoreTest() {
    Store store = new Store(1, "Tech Store");
    Store added = dataService.addStore(store);
    
    Store retrieved = dataService.getStore(added.getId());
    assertNotNull(retrieved);
    assertEquals(added.getId(), retrieved.getId());
  }

  @Test
  public void getCouponTest() {
    Coupon coupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
    Coupon added = dataService.addCoupon(coupon);
    
    Coupon retrieved = dataService.getCoupon(added.getId());
    assertNotNull(retrieved);
    assertEquals(added.getId(), retrieved.getId());
  }

  @Test
  public void getAllItemsTest() {
    dataService.addItem(new Item(1, "Item1", 10.0, 1, "cat1"));
    dataService.addItem(new Item(2, "Item2", 20.0, 1, "cat2"));
    
    ArrayList<Item> items = dataService.getAllItems();
    assertEquals(2, items.size());
  }

  @Test
  public void getAllStoresTest() {
    dataService.addStore(new Store(1, "Store1"));
    dataService.addStore(new Store(2, "Store2"));
    
    ArrayList<Store> stores = dataService.getAllStores();
    assertEquals(2, stores.size());
  }

  @Test
  public void getAllCouponsTest() {
    dataService.addCoupon(new TotalPriceCoupon(1, 1, 10.0, true, 50.0));
    dataService.addCoupon(new CategoryCoupon(2, 1, 5.0, false, "books"));
    
    ArrayList<Coupon> coupons = dataService.getAllCoupons();
    assertEquals(2, coupons.size());
  }

  @Test
  public void getItemsByStoreTest() {
    dataService.addItem(new Item(1, "Item1", 10.0, 1, "cat1"));
    dataService.addItem(new Item(2, "Item2", 20.0, 1, "cat2"));
    dataService.addItem(new Item(3, "Item3", 30.0, 2, "cat1"));
    
    ArrayList<Item> store1Items = dataService.getItemsByStore(1);
    assertEquals(2, store1Items.size());
  }

  @Test
  public void getCouponsByStoreTest() {
    dataService.addCoupon(new TotalPriceCoupon(1, 1, 10.0, true, 50.0));
    dataService.addCoupon(new CategoryCoupon(2, 1, 5.0, false, "books"));
    dataService.addCoupon(new TotalPriceCoupon(3, 2, 15.0, false, 100.0));
    
    ArrayList<Coupon> store1Coupons = dataService.getCouponsByStore(1);
    assertEquals(2, store1Coupons.size());
  }

  @Test
  public void getItemsByCategoryTest() {
    dataService.addItem(new Item(1, "Book1", 10.0, 1, "books"));
    dataService.addItem(new Item(2, "Book2", 20.0, 2, "books"));
    dataService.addItem(new Item(3, "Toy1", 30.0, 1, "toys"));
    
    ArrayList<Item> bookItems = dataService.getItemsByCategory("books");
    assertEquals(2, bookItems.size());
  }

  @Test
  public void getItemsByCategoryCaseInsensitiveTest() {
    dataService.addItem(new Item(1, "Book1", 10.0, 1, "Books"));
    dataService.addItem(new Item(2, "Book2", 20.0, 2, "BOOKS"));
    
    ArrayList<Item> bookItems = dataService.getItemsByCategory("books");
    assertEquals(2, bookItems.size());
  }

  @Test
  public void searchItemsByKeywordTest() {
    dataService.addItem(new Item(1, "Gaming Laptop", 999.0, 1, "electronics"));
    dataService.addItem(new Item(2, "Office Laptop", 699.0, 1, "electronics"));
    dataService.addItem(new Item(3, "Gaming Mouse", 49.0, 1, "electronics"));
    
    ArrayList<Item> laptops = dataService.searchItemsByKeyword("Laptop");
    assertEquals(2, laptops.size());
  }

  @Test
  public void searchItemsByKeywordCaseInsensitiveTest() {
    dataService.addItem(new Item(1, "Gaming Laptop", 999.0, 1, "electronics"));
    
    ArrayList<Item> results = dataService.searchItemsByKeyword("laptop");
    assertEquals(1, results.size());
  }

  @Test
  public void deleteItemTest() {
    Item item = dataService.addItem(new Item(1, "Item1", 10.0, 1, "cat1"));
    
    boolean deleted = dataService.deleteItem(item.getId());
    assertTrue(deleted);
    assertNull(dataService.getItem(item.getId()));
  }

  @Test
  public void deleteItemNotFoundTest() {
    boolean deleted = dataService.deleteItem(999);
    assertFalse(deleted);
  }

  @Test
  public void deleteStoreTest() {
    Store store = dataService.addStore(new Store(1, "Store1"));
    
    boolean deleted = dataService.deleteStore(store.getId());
    assertTrue(deleted);
    assertNull(dataService.getStore(store.getId()));
  }

  @Test
  public void deleteCouponTest() {
    Coupon coupon = dataService.addCoupon(new TotalPriceCoupon(1, 1, 10.0, true, 50.0));
    
    boolean deleted = dataService.deleteCoupon(coupon.getId());
    assertTrue(deleted);
    assertNull(dataService.getCoupon(coupon.getId()));
  }
}
