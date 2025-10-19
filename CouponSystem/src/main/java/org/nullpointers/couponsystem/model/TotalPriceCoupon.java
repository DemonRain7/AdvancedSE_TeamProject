package org.nullpointers.couponsystem.model;

public class TotalPriceCoupon extends Coupon {
  private double minimumPurchase;

  public TotalPriceCoupon(int id, int storeId, double discountValue, 
                          boolean isPercentage, double minimumPurchase) {
    super(id, storeId, discountValue, isPercentage);
    this.minimumPurchase = minimumPurchase;
  }

  public TotalPriceCoupon() {
    super();
    this.minimumPurchase = 0.0;
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

