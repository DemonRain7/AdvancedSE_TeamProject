package org.nullpointers.couponsystem.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * A coupon that applies a discount to all items in a specific category.
 * The discount can be either a percentage or a fixed amount.
 */
@Entity
@DiscriminatorValue("CATEGORY")
public class CategoryCoupon extends Coupon {
  private String category;

  /**
   * Constructs a CategoryCoupon with specified parameters.
   *
   * @param id unique identifier for the coupon
   * @param storeId the store this coupon belongs to
   * @param discountValue the discount amount or percentage
   * @param isPercentage true if discount is a percentage, false if fixed amount
   * @param category the category this coupon applies to
   */
  public CategoryCoupon(int id, int storeId, double discountValue,
                        boolean isPercentage, String category) {
    super(id, storeId, discountValue, isPercentage);
    this.category = category;
    setType("category");
  }

  /**
   * Default constructor for CategoryCoupon.
   * Sets category to empty string.
   */
  public CategoryCoupon() {
    super();
    this.category = "";
    setType("category");
  }

  @Override
  public double calculateDiscount(Item[] items) {
    if (!isApplicable(items)) {
      return 0.0;
    }
    
    double categoryTotal = 0.0;
    for (Item item : items) {
      if (item.getStoreId() == getStoreId() 
          && item.getCategory().equalsIgnoreCase(category)) {
        categoryTotal += item.getPrice();
      }
    }
    
    return getDiscountAmount(categoryTotal);
  }

  @Override
  public boolean isApplicable(Item[] items) {
    for (Item item : items) {
      if (item.getStoreId() == getStoreId() 
          && item.getCategory().equalsIgnoreCase(category)) {
        return true;
      }
    }
    return false;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return String.format("CategoryCoupon{id=%d, storeId=%d, discount=%.2f%s, category='%s'}",
        getId(), getStoreId(), getDiscountValue(), 
        isPercentage() ? "%" : "$", category);
  }
}

