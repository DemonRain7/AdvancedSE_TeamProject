package org.nullpointers.couponsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the ItemCoupon class.
 */
@SpringBootTest
public class ItemCouponTest {
  private ItemCoupon percentageCoupon;
  private ItemCoupon fixedCoupon;
  private Item[] cartItems;

  @BeforeEach
  public void setUp() {
    percentageCoupon = new ItemCoupon(1, 1, 15.0, true, 1);
    fixedCoupon = new ItemCoupon(2, 1, 5.0, false, 2);
    
    cartItems = new Item[]{
        new Item(1, "Laptop", 100.0, 1, "electronics"),
        new Item(2, "Mouse", 20.0, 1, "electronics"),
        new Item(3, "Keyboard", 50.0, 2, "electronics")
    };
  }

  @Test
  public void completeConstructorTest() {
    assertEquals(1, percentageCoupon.getId());
    assertEquals(1, percentageCoupon.getStoreId());
    assertEquals(15.0, percentageCoupon.getDiscountValue(), 0.001);
    assertTrue(percentageCoupon.isPercentage());
    assertEquals(1, percentageCoupon.getTargetItemId());
  }

  @Test
  public void noArgsConstructorTest() {
    ItemCoupon coupon = new ItemCoupon();
    assertEquals(0, coupon.getId());
    assertEquals(0, coupon.getStoreId());
    assertEquals(0.0, coupon.getDiscountValue(), 0.001);
    assertFalse(coupon.isPercentage());
    assertEquals(0, coupon.getTargetItemId());
  }

  @Test
  public void isApplicableWhenItemExistsTest() {
    assertTrue(percentageCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWhenItemNotExistsTest() {
    ItemCoupon nonExistentCoupon = new ItemCoupon(3, 1, 10.0, true, 999);
    assertFalse(nonExistentCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWhenItemExistsButDifferentStoreTest() {
    ItemCoupon differentStoreCoupon = new ItemCoupon(4, 2, 10.0, true, 1);
    assertFalse(differentStoreCoupon.isApplicable(cartItems));
  }

  @Test
  public void calculateDiscountWithPercentageTest() {
    double discount = percentageCoupon.calculateDiscount(cartItems);
    assertEquals(15.0, discount, 0.001);  // 15% of 100
  }

  @Test
  public void calculateDiscountWithFixedAmountTest() {
    double discount = fixedCoupon.calculateDiscount(cartItems);
    assertEquals(5.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWhenNotApplicableTest() {
    ItemCoupon nonExistentCoupon = new ItemCoupon(3, 1, 10.0, true, 999);
    double discount = nonExistentCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountDoesNotExceedItemPriceTest() {
    ItemCoupon largeCoupon = new ItemCoupon(5, 1, 50.0, false, 2);
    double discount = largeCoupon.calculateDiscount(cartItems);
    assertEquals(20.0, discount, 0.001);  // Min of 50 and 20
  }

  @Test
  public void settersTest() {
    percentageCoupon.setTargetItemId(5);
    assertEquals(5, percentageCoupon.getTargetItemId());
  }

  @Test
  public void toStringTest() {
    String result = percentageCoupon.toString();
    assertTrue(result.contains("ItemCoupon"));
    assertTrue(result.contains("15.00%"));
    assertTrue(result.contains("targetItemId=1"));
  }
}

