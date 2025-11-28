package org.nullpointers.couponsystem.model;

/**
 * A coupon that applies a discount based on the total purchase amount.
 * The discount is only applied if the cart total meets the minimum purchase requirement.
 */
public class TotalPriceCoupon extends Coupon {
  private double minimumPurchase;

  /**
   * Constructs a TotalPriceCoupon with specified parameters.
   *
   * @param id unique identifier for the coupon
   * @param storeId the store this coupon belongs to
   * @param discountValue the discount amount or percentage
   * @param isPercentage true if discount is a percentage, false if fixed amount
   * @param minimumPurchase minimum cart total required to apply this coupon
   */
  public TotalPriceCoupon(int id, int storeId, double discountValue,
                          boolean isPercentage, double minimumPurchase) {
    super(id, storeId, discountValue, isPercentage);
    this.minimumPurchase = minimumPurchase;
    setType("totalprice");
  }

  /**
   * Default constructor for TotalPriceCoupon.
   * Sets minimum purchase to 0.0.
   */
  public TotalPriceCoupon() {
    super();
    this.minimumPurchase = 0.0;
    setType("totalprice");
  }

  @Override
  public double calculateDiscount(Item[] items) {
    if (!isApplicable(items)) {
      return 0.0;
    }
    
    double total = 0.0;
    for (Item item : items) {
      if (item.getStoreId() == getStoreId()) {
        total += item.getPrice();
      }
    }
    
    return getDiscountAmount(total);
  }

  @Override
  public boolean isApplicable(Item[] items) {
    double total = 0.0;
    for (Item item : items) {
      if (item.getStoreId() == getStoreId()) {
        total += item.getPrice();
      }
    }
    return total >= minimumPurchase;
  }

  public double getMinimumPurchase() {
    return minimumPurchase;
  }

  public void setMinimumPurchase(double minimumPurchase) {
    this.minimumPurchase = minimumPurchase;
  }

  @Override
  public String toString() {
    return String.format("TotalPriceCoupon{id=%d, storeId=%d, discount=%.2f%s, minPurchase=%.2f}",
        getId(), getStoreId(), getDiscountValue(), 
        isPercentage() ? "%" : "$", minimumPurchase);
  }
}

