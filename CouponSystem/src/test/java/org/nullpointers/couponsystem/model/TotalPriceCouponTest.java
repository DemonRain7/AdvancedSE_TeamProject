package org.nullpointers.couponsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the TotalPriceCoupon class.
 */
@SpringBootTest
public class TotalPriceCouponTest {
  private TotalPriceCoupon percentageCoupon;
  private TotalPriceCoupon fixedCoupon;
  private Item[] cartItems;

  /**
   * Sets up test fixtures before each test method.
   */
  @BeforeEach
  public void setUp() {
    percentageCoupon = new TotalPriceCoupon(1, 1, 10.0, true, 50.0);
    fixedCoupon = new TotalPriceCoupon(2, 1, 15.0, false, 100.0);
    
    cartItems = new Item[]{
        new Item(1, "Item1", 30.0, 1, "books"),
        new Item(2, "Item2", 40.0, 1, "books"),
        new Item(3, "Item3", 20.0, 2, "toys")
    };
  }

  @Test
  public void completeConstructorTest() {
    assertEquals(1, percentageCoupon.getId());
    assertEquals(1, percentageCoupon.getStoreId());
    assertEquals(10.0, percentageCoupon.getDiscountValue(), 0.001);
    assertTrue(percentageCoupon.isPercentage());
    assertEquals(50.0, percentageCoupon.getMinimumPurchase(), 0.001);
  }

  @Test
  public void noArgsConstructorTest() {
    TotalPriceCoupon coupon = new TotalPriceCoupon();
    assertEquals(0, coupon.getId());
    assertEquals(0, coupon.getStoreId());
    assertEquals(0.0, coupon.getDiscountValue(), 0.001);
    assertFalse(coupon.isPercentage());
    assertEquals(0.0, coupon.getMinimumPurchase(), 0.001);
  }

  @Test
  public void isApplicableWhenMeetsThresholdTest() {
    assertTrue(percentageCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWhenDoesNotMeetThresholdTest() {
    assertFalse(fixedCoupon.isApplicable(cartItems));
  }

  @Test
  public void calculateDiscountWithPercentageTest() {
    double discount = percentageCoupon.calculateDiscount(cartItems);
    assertEquals(7.0, discount, 0.001);  // 10% of 70 (30 + 40)
  }

  @Test
  public void calculateDiscountWithFixedAmountTest() {
    Item[] largeCart = new Item[]{
        new Item(1, "Item1", 60.0, 1, "books"),
        new Item(2, "Item2", 50.0, 1, "books")
    };
    double discount = fixedCoupon.calculateDiscount(largeCart);
    assertEquals(15.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWhenNotApplicableTest() {
    double discount = fixedCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void settersTest() {
    percentageCoupon.setMinimumPurchase(75.0);
    assertEquals(75.0, percentageCoupon.getMinimumPurchase(), 0.001);
  }

  @Test
  public void toStringTest() {
    String result = percentageCoupon.toString();
    assertTrue(result.contains("TotalPriceCoupon"));
    assertTrue(result.contains("10.00%"));
    assertTrue(result.contains("50.00"));
  }
}

