package org.nullpointers.couponsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for the ItemCoupon class.
 */
@SpringBootTest
@ActiveProfiles("test1")
public class ItemCouponTest {
  private ItemCoupon percentageCoupon;
  private ItemCoupon fixedCoupon;
  private Item[] cartItems;

  /**
   * Sets up test fixtures before each test method.
   */
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
    // Partition: targetItemId=1 (existing valid ID - applicable)
    assertTrue(percentageCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWhenItemNotExistsTest() {
    // Partition: targetItemId=999 (ABOVE maximum existing ID - not applicable)
    ItemCoupon nonExistentCoupon = new ItemCoupon(3, 1, 10.0, true, 999);
    assertFalse(nonExistentCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWithZeroTargetItemIdTest() {
    // Partition: targetItemId=0 (BELOW minimum valid ID - not applicable)
    ItemCoupon zeroIdCoupon = new ItemCoupon(5, 1, 10.0, true, 0);
    assertFalse(zeroIdCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWithNegativeTargetItemIdTest() {
    // Partition: targetItemId=-1 (BELOW minimum valid ID - not applicable)
    ItemCoupon negativeIdCoupon = new ItemCoupon(6, 1, 10.0, true, -1);
    assertFalse(negativeIdCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWhenItemExistsButDifferentStoreTest() {
    // Partition: targetItemId exists but storeId mismatch (not applicable)
    ItemCoupon differentStoreCoupon = new ItemCoupon(4, 2, 10.0, true, 1);
    assertFalse(differentStoreCoupon.isApplicable(cartItems));
  }

  @Test
  public void calculateDiscountWithPercentageTest() {
    // Partition: discountValue=15.0% (typical percentage)
    double discount = percentageCoupon.calculateDiscount(cartItems);
    assertEquals(15.0, discount, 0.001);  // 15% of 100
  }

  @Test
  public void calculateDiscountWithZeroPercentageTest() {
    // Partition: discountValue=0.0% (AT minimum boundary)
    ItemCoupon zeroCoupon = new ItemCoupon(7, 1, 0.0, true, 1);
    double discount = zeroCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWithMaxPercentageTest() {
    // Partition: discountValue=100.0% (AT maximum boundary)
    ItemCoupon maxCoupon = new ItemCoupon(8, 1, 100.0, true, 1);
    double discount = maxCoupon.calculateDiscount(cartItems);
    assertEquals(100.0, discount, 0.001);  // 100% of 100
  }

  @Test
  public void calculateDiscountWithFixedAmountTest() {
    // Partition: discountValue=5.0 (fixed amount BELOW item price)
    double discount = fixedCoupon.calculateDiscount(cartItems);
    assertEquals(5.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWithZeroFixedAmountTest() {
    // Partition: discountValue=0.0 (AT minimum boundary - fixed)
    ItemCoupon zeroFixedCoupon = new ItemCoupon(9, 1, 0.0, false, 1);
    double discount = zeroFixedCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWhenNotApplicableTest() {
    // Partition: targetItemId=999 (non-existent - not applicable)
    ItemCoupon nonExistentCoupon = new ItemCoupon(3, 1, 10.0, true, 999);
    double discount = nonExistentCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountDoesNotExceedItemPriceTest() {
    // Partition: discountValue=50.0 (ABOVE item price=20.0 - capped at item price)
    ItemCoupon largeCoupon = new ItemCoupon(5, 1, 50.0, false, 2);
    double discount = largeCoupon.calculateDiscount(cartItems);
    assertEquals(20.0, discount, 0.001);  // Min of 50 and 20
  }

  @Test
  public void calculateDiscountAtExactItemPriceTest() {
    // Partition: discountValue=20.0 (AT item price boundary)
    ItemCoupon exactCoupon = new ItemCoupon(10, 1, 20.0, false, 2);
    double discount = exactCoupon.calculateDiscount(cartItems);
    assertEquals(20.0, discount, 0.001);
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

