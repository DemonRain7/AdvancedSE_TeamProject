package org.nullpointers.couponsystem.model;

/**
 * Abstract base class for all coupon types in the system.
 * Provides common functionality for calculating discounts and checking applicability.
 */
public abstract class Coupon {
  private int id;
  private int storeId;
  private double discountValue;
  private boolean isPercentage;

  /**
   * Constructs a coupon with the specified parameters.
   *
   * @param id the unique identifier for this coupon
   * @param storeId the store this coupon belongs to
   * @param discountValue the discount value (percentage or fixed amount)
   * @param isPercentage true if discount is a percentage, false if it's a fixed amount
   */
  public Coupon(int id, int storeId, double discountValue, boolean isPercentage) {
    this.id = id;
    this.storeId = storeId;
    this.discountValue = discountValue;
    this.isPercentage = isPercentage;
  }

  /**
   * Default constructor initializing all fields to default values.
   */
  public Coupon() {
    this.id = 0;
    this.storeId = 0;
    this.discountValue = 0.0;
    this.isPercentage = false;
  }

  public abstract double calculateDiscount(Item[] items);

  public abstract boolean isApplicable(Item[] items);

  protected double getDiscountAmount(double subtotal) {
    if (isPercentage) {
      return subtotal * (discountValue / 100.0);
    } else {
      return Math.min(discountValue, subtotal);
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getStoreId() {
    return storeId;
  }

  public void setStoreId(int storeId) {
    this.storeId = storeId;
  }

  public double getDiscountValue() {
    return discountValue;
  }

  public void setDiscountValue(double discountValue) {
    this.discountValue = discountValue;
  }

  public boolean isPercentage() {
    return isPercentage;
  }

  public void setPercentage(boolean percentage) {
    isPercentage = percentage;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Coupon coupon = (Coupon) obj;
    return id == coupon.id;
  }
}

