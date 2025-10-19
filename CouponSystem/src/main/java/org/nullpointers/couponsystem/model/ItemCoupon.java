package org.nullpointers.couponsystem.model;

public class ItemCoupon extends Coupon {
  private int targetItemId;

  public ItemCoupon(int id, int storeId, double discountValue, 
                    boolean isPercentage, int targetItemId) {
    super(id, storeId, discountValue, isPercentage);
    this.targetItemId = targetItemId;
  }

  public ItemCoupon() {
    super();
    this.targetItemId = 0;
  }

  @Override
  public double calculateDiscount(Item[] items) {
    if (!isApplicable(items)) {
      return 0.0;
    }
    
    for (Item item : items) {
      if (item.getId() == targetItemId && item.getStoreId() == getStoreId()) {
        return getDiscountAmount(item.getPrice());
      }
    }
    
    return 0.0;
  }

  @Override
  public boolean isApplicable(Item[] items) {
    for (Item item : items) {
      if (item.getId() == targetItemId && item.getStoreId() == getStoreId()) {
        return true;
      }
    }
    return false;
  }

  public int getTargetItemId() {
    return targetItemId;
  }

  public void setTargetItemId(int targetItemId) {
    this.targetItemId = targetItemId;
  }

  @Override
  public String toString() {
    return String.format("ItemCoupon{id=%d, storeId=%d, discount=%.2f%s, targetItemId=%d}",
        getId(), getStoreId(), getDiscountValue(), 
        isPercentage() ? "%" : "$", targetItemId);
  }
}

