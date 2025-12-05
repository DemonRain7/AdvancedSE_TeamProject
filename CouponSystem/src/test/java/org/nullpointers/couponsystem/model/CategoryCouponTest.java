package org.nullpointers.couponsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for the CategoryCoupon class.
 */
@SpringBootTest
@ActiveProfiles("test1")
public class CategoryCouponTest {
  private CategoryCoupon percentageCoupon;
  private CategoryCoupon fixedCoupon;
  private Item[] cartItems;

  /**
   * Sets up test fixtures before each test method.
   */
  @BeforeEach
  public void setUp() {
    percentageCoupon = new CategoryCoupon(1, 1, 20.0, true, "books");
    fixedCoupon = new CategoryCoupon(2, 1, 10.0, false, "toys");
    
    cartItems = new Item[]{
        new Item(1, "Book1", 30.0, 1, "books"),
        new Item(2, "Book2", 20.0, 1, "books"),
        new Item(3, "Toy1", 15.0, 1, "toys")
    };
  }

  @Test
  public void completeConstructorTest() {
    assertEquals(1, percentageCoupon.getId());
    assertEquals(1, percentageCoupon.getStoreId());
    assertEquals(20.0, percentageCoupon.getDiscountValue(), 0.001);
    assertTrue(percentageCoupon.isPercentage());
    assertEquals("books", percentageCoupon.getCategory());
  }

  @Test
  public void noArgsConstructorTest() {
    CategoryCoupon coupon = new CategoryCoupon();
    assertEquals(0, coupon.getId());
    assertEquals(0, coupon.getStoreId());
    assertEquals(0.0, coupon.getDiscountValue(), 0.001);
    assertFalse(coupon.isPercentage());
    assertEquals("", coupon.getCategory());
  }

  @Test
  public void isApplicableWhenCategoryExistsTest() {
    assertTrue(percentageCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableWhenCategoryNotExistsTest() {
    CategoryCoupon electronicsoCoupon = new CategoryCoupon(3, 1, 10.0, true, "electronics");
    assertFalse(electronicsoCoupon.isApplicable(cartItems));
  }

  @Test
  public void isApplicableCaseInsensitiveTest() {
    CategoryCoupon upperCaseCoupon = new CategoryCoupon(4, 1, 10.0, true, "BOOKS");
    assertTrue(upperCaseCoupon.isApplicable(cartItems));
  }

  @Test
  public void calculateDiscountWithPercentageTest() {
    // Partition: discountValue=20.0% (typical percentage)
    double discount = percentageCoupon.calculateDiscount(cartItems);
    assertEquals(10.0, discount, 0.001);  // 20% of 50 (30 + 20)
  }

  @Test
  public void calculateDiscountWithZeroPercentageTest() {
    // Partition: discountValue=0.0% (AT minimum boundary)
    CategoryCoupon zeroCoupon = new CategoryCoupon(5, 1, 0.0, true, "books");
    double discount = zeroCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWithMaxPercentageTest() {
    // Partition: discountValue=100.0% (AT maximum boundary)
    CategoryCoupon maxCoupon = new CategoryCoupon(6, 1, 100.0, true, "books");
    double discount = maxCoupon.calculateDiscount(cartItems);
    assertEquals(50.0, discount, 0.001);  // 100% of 50 (30 + 20)
  }

  @Test
  public void calculateDiscountWithFixedAmountTest() {
    // Partition: discountValue=10.0 (fixed amount)
    double discount = fixedCoupon.calculateDiscount(cartItems);
    assertEquals(10.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWithZeroFixedAmountTest() {
    // Partition: discountValue=0.0 (AT minimum boundary - fixed)
    CategoryCoupon zeroFixedCoupon = new CategoryCoupon(7, 1, 0.0, false, "books");
    double discount = zeroFixedCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWhenNotApplicableTest() {
    // Partition: category="electronics" (no items in cart with this category)
    CategoryCoupon electronicsCoupon = new CategoryCoupon(3, 1, 10.0, true, "electronics");
    double discount = electronicsCoupon.calculateDiscount(cartItems);
    assertEquals(0.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWithLargeFixedAmountTest() {
    // Partition: discountValue=100.0 (ABOVE category total - capped at category total)
    CategoryCoupon largeCoupon = new CategoryCoupon(8, 1, 100.0, false, "books");
    double discount = largeCoupon.calculateDiscount(cartItems);
    assertEquals(50.0, discount, 0.001);  // Min of 100 and 50
  }

  @Test
  public void calculateDiscountAtExactCategoryTotalTest() {
    // Partition: discountValue=50.0 (AT category total boundary)
    CategoryCoupon exactCoupon = new CategoryCoupon(9, 1, 50.0, false, "books");
    double discount = exactCoupon.calculateDiscount(cartItems);
    assertEquals(50.0, discount, 0.001);
  }

  @Test
  public void calculateDiscountWithDifferentStoreTest() {
    Item[] mixedStoreCart = new Item[]{
        new Item(1, "Book1", 30.0, 2, "books"),
        new Item(2, "Book2", 20.0, 1, "books")
    };
    double discount = percentageCoupon.calculateDiscount(mixedStoreCart);
    assertEquals(4.0, discount, 0.001);  // 20% of 20 (only store 1 items)
  }

  @Test
  public void settersTest() {
    percentageCoupon.setCategory("electronics");
    assertEquals("electronics", percentageCoupon.getCategory());
  }

  @Test
  public void toStringTest() {
    String result = percentageCoupon.toString();
    assertTrue(result.contains("CategoryCoupon"));
    assertTrue(result.contains("20.00%"));
    assertTrue(result.contains("books"));
  }
}

