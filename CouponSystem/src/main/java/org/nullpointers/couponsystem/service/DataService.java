package org.nullpointers.couponsystem.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.Store;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing all data in the coupon system.
 * Acts as an in-memory database for items, stores, and coupons.
 */
@Service
public class DataService {
  private final Map<Integer, Item> items;
  private final Map<Integer, Store> stores;
  private final Map<Integer, Coupon> coupons;
  private int nextItemId;
  private int nextStoreId;
  private int nextCouponId;

  /**
   * Initializes the data service with empty collections.
   */
  public DataService() {
    this.items = new HashMap<>();
    this.stores = new HashMap<>();
    this.coupons = new HashMap<>();
    this.nextItemId = 1;
    this.nextStoreId = 1;
    this.nextCouponId = 1;
  }

  /**
   * Adds a new item to the system.
   *
   * @param item the item to add
   * @return the added item with assigned ID
   */
  public Item addItem(Item item) {
    if (item.getId() == 0) {
      item.setId(nextItemId++);
    }
    items.put(item.getId(), item);
    return item;
  }

  /**
   * Adds a new store to the system.
   *
   * @param store the store to add
   * @return the added store with assigned ID
   */
  public Store addStore(Store store) {
    if (store.getId() == 0) {
      store.setId(nextStoreId++);
    }
    stores.put(store.getId(), store);
    return store;
  }

  /**
   * Adds a new coupon to the system.
   *
   * @param coupon the coupon to add
   * @return the added coupon with assigned ID
   */
  public Coupon addCoupon(Coupon coupon) {
    if (coupon.getId() == 0) {
      coupon.setId(nextCouponId++);
    }
    coupons.put(coupon.getId(), coupon);
    return coupon;
  }

  public Item getItem(int id) {
    return items.get(id);
  }

  public Store getStore(int id) {
    return stores.get(id);
  }

  public Coupon getCoupon(int id) {
    return coupons.get(id);
  }

  public ArrayList<Item> getAllItems() {
    return new ArrayList<>(items.values());
  }

  public ArrayList<Store> getAllStores() {
    return new ArrayList<>(stores.values());
  }

  public ArrayList<Coupon> getAllCoupons() {
    return new ArrayList<>(coupons.values());
  }

  /**
   * Retrieves all items from a specific store.
   *
   * @param storeId the ID of the store
   * @return list of items from the specified store
   */
  public ArrayList<Item> getItemsByStore(int storeId) {
    ArrayList<Item> result = new ArrayList<>();
    for (Item item : items.values()) {
      if (item.getStoreId() == storeId) {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Retrieves all coupons from a specific store.
   *
   * @param storeId the ID of the store
   * @return list of coupons from the specified store
   */
  public ArrayList<Coupon> getCouponsByStore(int storeId) {
    ArrayList<Coupon> result = new ArrayList<>();
    for (Coupon coupon : coupons.values()) {
      if (coupon.getStoreId() == storeId) {
        result.add(coupon);
      }
    }
    return result;
  }

  /**
   * Retrieves all items matching a specific category.
   *
   * @param category the category to search for
   * @return list of items in the specified category
   */
  public ArrayList<Item> getItemsByCategory(String category) {
    ArrayList<Item> result = new ArrayList<>();
    for (Item item : items.values()) {
      if (item.getCategory().equalsIgnoreCase(category)) {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Searches for items by keyword in their name.
   *
   * @param keyword the keyword to search for
   * @return list of items containing the keyword
   */
  public ArrayList<Item> searchItemsByKeyword(String keyword) {
    ArrayList<Item> result = new ArrayList<>();
    for (Item item : items.values()) {
      if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Deletes an item from the system.
   *
   * @param id the item ID
   * @return true if deleted, false if not found
   */
  public boolean deleteItem(int id) {
    return items.remove(id) != null;
  }

  /**
   * Deletes a store from the system.
   *
   * @param id the store ID
   * @return true if deleted, false if not found
   */
  public boolean deleteStore(int id) {
    return stores.remove(id) != null;
  }

  /**
   * Deletes a coupon from the system.
   *
   * @param id the coupon ID
   * @return true if deleted, false if not found
   */
  public boolean deleteCoupon(int id) {
    return coupons.remove(id) != null;
  }
}
