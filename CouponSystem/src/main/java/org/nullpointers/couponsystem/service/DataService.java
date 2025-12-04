package org.nullpointers.couponsystem.service;

import java.util.ArrayList;
import java.util.Optional;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.Store;
import org.nullpointers.couponsystem.repository.CouponRepository;
import org.nullpointers.couponsystem.repository.ItemRepository;
import org.nullpointers.couponsystem.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing all data in the coupon system.
 * Acts as an interface to the database for items, stores, and coupons.
 */
@Service
public class DataService {
  private final StoreRepository storeRepository;
  private final ItemRepository itemRepository;
  private final CouponRepository couponRepository;

  /**
   * Initializes the data service with repositories.
   *
   * @param storeRepository the store repository
   * @param itemRepository the item repository
   * @param couponRepository the coupon repository
   */
  @Autowired
  public DataService(StoreRepository storeRepository, ItemRepository itemRepository,
                     CouponRepository couponRepository) {
    this.storeRepository = storeRepository;
    this.itemRepository = itemRepository;
    this.couponRepository = couponRepository;
  }

  /**
   * Adds a new item to the system.
   *
   * @param item the item to add
   * @return the added item with assigned ID
   */
  public Item addItem(Item item) {
    return itemRepository.save(item);
  }

  /**
   * Adds a new store to the system.
   *
   * @param store the store to add
   * @return the added store with assigned ID
   */
  public Store addStore(Store store) {
    return storeRepository.save(store);
  }

  /**
   * Adds a new coupon to the system.
   *
   * @param coupon the coupon to add
   * @return the added coupon with assigned ID
   */
  public Coupon addCoupon(Coupon coupon) {
    return couponRepository.save(coupon);
  }

  public Item getItem(int id) {
    Optional<Item> item = itemRepository.findById(id);
    return item.orElse(null);
  }

  public Store getStore(int id) {
    Optional<Store> store = storeRepository.findById(id);
    return store.orElse(null);
  }

  public Coupon getCoupon(int id) {
    Optional<Coupon> coupon = couponRepository.findById(id);
    return coupon.orElse(null);
  }

  public ArrayList<Item> getAllItems() {
    return new ArrayList<>(itemRepository.findAll());
  }

  public ArrayList<Store> getAllStores() {
    return new ArrayList<>(storeRepository.findAll());
  }

  public ArrayList<Coupon> getAllCoupons() {
    return new ArrayList<>(couponRepository.findAll());
  }

  /**
   * Retrieves all items from a specific store.
   *
   * @param storeId the ID of the store
   * @return list of items from the specified store
   */
  public ArrayList<Item> getItemsByStore(int storeId) {
    return new ArrayList<>(itemRepository.findByStoreId(storeId));
  }

  /**
   * Retrieves all coupons from a specific store.
   *
   * @param storeId the ID of the store
   * @return list of coupons from the specified store
   */
  public ArrayList<Coupon> getCouponsByStore(int storeId) {
    return new ArrayList<>(couponRepository.findByStoreId(storeId));
  }

  /**
   * Retrieves all items matching a specific category.
   *
   * @param category the category to search for
   * @return list of items in the specified category
   */
  public ArrayList<Item> getItemsByCategory(String category) {
    return new ArrayList<>(itemRepository.findByCategoryIgnoreCase(category));
  }

  /**
   * Searches for items by keyword in their name.
   *
   * @param keyword the keyword to search for
   * @return list of items containing the keyword
   */
  public ArrayList<Item> searchItemsByKeyword(String keyword) {
    return new ArrayList<>(itemRepository.findByNameContainingIgnoreCase(keyword));
  }

  /**
   * Deletes an item from the system.
   *
   * @param id the item ID
   * @return true if deleted, false if not found
   */
  public boolean deleteItem(int id) {
    if (itemRepository.existsById(id)) {
      itemRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Deletes a store from the system.
   *
   * @param id the store ID
   * @return true if deleted, false if not found
   */
  public boolean deleteStore(int id) {
    if (storeRepository.existsById(id)) {
      storeRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Deletes a coupon from the system.
   *
   * @param id the coupon ID
   * @return true if deleted, false if not found
   */
  public boolean deleteCoupon(int id) {
    if (couponRepository.existsById(id)) {
      couponRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
