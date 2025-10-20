package org.nullpointers.couponsystem.service;

import java.util.ArrayList;
import java.util.Comparator;
import org.nullpointers.couponsystem.model.Coupon;
import org.nullpointers.couponsystem.model.Item;
import org.nullpointers.couponsystem.model.Store;
import org.nullpointers.couponsystem.model.TotalPriceCoupon;
import org.springframework.stereotype.Service;

/**
 * Service class that provides core coupon-related business logic.
 * Handles optimal coupon selection, store recommendations, and cart optimization.
 */
@Service
public class CouponService {
  private final DataService dataService;

  public CouponService(DataService dataService) {
    this.dataService = dataService;
  }

  /**
   * Identifies the optimal coupon for a given cart of items from a specific store.
   * Optimal is defined as the coupon that provides the maximum discount.
   *
   * @param itemIds array of item IDs in the cart
   * @param storeId the store ID
   * @return the optimal coupon, or null if no applicable coupon exists
   */
  public Coupon findOptimalCoupon(int[] itemIds, int storeId) {
    Item[] items = convertIdsToItems(itemIds);
    if (items == null) {
      return null;
    }

    ArrayList<Coupon> storeCoupons = dataService.getCouponsByStore(storeId);
    Coupon bestCoupon = null;
    double maxDiscount = 0.0;

    for (Coupon coupon : storeCoupons) {
      if (coupon.isApplicable(items)) {
        double discount = coupon.calculateDiscount(items);
        if (discount > maxDiscount) {
          maxDiscount = discount;
          bestCoupon = coupon;
        }
      }
    }

    return bestCoupon;
  }

  /**
   * Finds optimal stores for purchasing items matching a keyword or category.
   * Returns stores sorted by final price (after applying best coupon) in ascending order.
   * Assumes the intent is to buy one of any matching items.
   *
   * @param keyword the keyword to search for in item names
   * @param category the category to filter by (can be null)
   * @return list of StoreRecommendation objects sorted by best price
   */
  public ArrayList<StoreRecommendation> findOptimalStoresForSearch(
      String keyword, String category) {
    ArrayList<Item> matchingItems;
    
    if (category != null && !category.isEmpty()) {
      matchingItems = dataService.getItemsByCategory(category);
    } else {
      matchingItems = dataService.searchItemsByKeyword(keyword);
    }

    if (matchingItems.isEmpty()) {
      return new ArrayList<>();
    }

    // Group items by store and find the cheapest item per store
    ArrayList<StoreRecommendation> recommendations = new ArrayList<>();
    
    for (Store store : dataService.getAllStores()) {
      Item cheapestItem = null;
      double lowestPrice = Double.MAX_VALUE;

      for (Item item : matchingItems) {
        if (item.getStoreId() == store.getId() && item.getPrice() < lowestPrice) {
          cheapestItem = item;
          lowestPrice = item.getPrice();
        }
      }

      if (cheapestItem != null) {
        // Calculate price with best applicable coupon
        Item[] cart = new Item[]{cheapestItem};
        Coupon bestCoupon = findOptimalCoupon(
            new int[]{cheapestItem.getId()}, store.getId());
        
        double finalPrice = lowestPrice;
        double discount = 0.0;
        
        if (bestCoupon != null) {
          discount = bestCoupon.calculateDiscount(cart);
          finalPrice = lowestPrice - discount;
        }

        recommendations.add(new StoreRecommendation(
            store, cheapestItem, bestCoupon, finalPrice, discount));
      }
    }

    recommendations.sort(Comparator.comparingDouble(StoreRecommendation::getFinalPrice));
    return recommendations;
  }

  /**
   * Finds the cheapest items from a store that could help satisfy a TotalPriceCoupon.
   * Returns items that would bring the cart total to meet the minimum purchase requirement.
   *
   * @param itemIds array of current cart item IDs
   * @param storeId the store ID
   * @param couponId the TotalPriceCoupon ID
   * @return list of suggested items to add, or empty list if coupon doesn't exist or not applicable
   */
  public ArrayList<Item> findItemsToMeetCouponThreshold(
      int[] itemIds, int storeId, int couponId) {
    Coupon coupon = dataService.getCoupon(couponId);
    
    if (!(coupon instanceof TotalPriceCoupon)) {
      return new ArrayList<>();
    }

    TotalPriceCoupon totalPriceCoupon = (TotalPriceCoupon) coupon;
    
    if (totalPriceCoupon.getStoreId() != storeId) {
      return new ArrayList<>();
    }

    // Calculate current cart total
    Item[] items = convertIdsToItems(itemIds);
    if (items == null) {
      return new ArrayList<>();
    }

    double currentTotal = 0.0;
    for (Item item : items) {
      if (item.getStoreId() == storeId) {
        currentTotal += item.getPrice();
      }
    }

    // If already meets threshold, return empty list
    if (currentTotal >= totalPriceCoupon.getMinimumPurchase()) {
      return new ArrayList<>();
    }

    double amountNeeded = totalPriceCoupon.getMinimumPurchase() - currentTotal;

    // Get all items from the store, sort by price
    ArrayList<Item> storeItems = dataService.getItemsByStore(storeId);
    storeItems.sort(Comparator.comparingDouble(Item::getPrice));

    // Find the cheapest combination to meet the threshold
    ArrayList<Item> suggestions = new ArrayList<>();
    double addedAmount = 0.0;

    for (Item item : storeItems) {
      if (addedAmount >= amountNeeded) {
        break;
      }
      suggestions.add(item);
      addedAmount += item.getPrice();
    }

    return suggestions;
  }

  /**
   * Helper method to convert item IDs to Item objects.
   *
   * @param itemIds array of item IDs
   * @return array of Item objects, or null if any ID is invalid
   */
  private Item[] convertIdsToItems(int[] itemIds) {
    if (itemIds == null || itemIds.length == 0) {
      return new Item[0];
    }

    Item[] items = new Item[itemIds.length];
    for (int i = 0; i < itemIds.length; i++) {
      Item item = dataService.getItem(itemIds[i]);
      if (item == null) {
        return null;
      }
      items[i] = item;
    }
    return items;
  }

  /**
   * Inner class to represent a store recommendation with pricing details.
   */
  public static class StoreRecommendation {
    private final Store store;
    private final Item item;
    private final Coupon coupon;
    private final double finalPrice;
    private final double discount;

    /**
     * Creates a store recommendation.
     *
     * @param store the store
     * @param item the item
     * @param coupon the applicable coupon (can be null)
     * @param finalPrice the final price after discount
     * @param discount the discount amount
     */
    public StoreRecommendation(Store store, Item item, Coupon coupon, 
                               double finalPrice, double discount) {
      this.store = store;
      this.item = item;
      this.coupon = coupon;
      this.finalPrice = finalPrice;
      this.discount = discount;
    }

    public Store getStore() {
      return store;
    }

    public Item getItem() {
      return item;
    }

    public Coupon getCoupon() {
      return coupon;
    }

    public double getFinalPrice() {
      return finalPrice;
    }

    public double getDiscount() {
      return discount;
    }
  }
}
