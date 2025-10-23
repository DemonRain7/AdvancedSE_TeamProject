package org.nullpointers.couponsystem.model;

/**
 * A coupon that applies a discount to all items in a specific category.
 * The discount can be either a percentage or a fixed amount.
 */
public class CategoryCoupon extends Coupon {
  private String category;

  public CategoryCoupon(int id, int storeId, double discountValue, 
                        boolean isPercentage, String category) {
    super(id, storeId, discountValue, isPercentage);
    this.category = category;
  }

  public CategoryCoupon() {
    super();
    this.category = "";
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

