package org.nullpointers.couponsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the Item class.
 */
@SpringBootTest
public class ItemTest {
  private Item testItem;

  @BeforeEach
  public void setUp() {
    testItem = new Item(1, "Laptop", 999.99, 1, "electronics");
  }

  @Test
  public void completeConstructorTest() {
    assertEquals(1, testItem.getId());
    assertEquals("Laptop", testItem.getName());
    assertEquals(999.99, testItem.getPrice(), 0.001);
    assertEquals(1, testItem.getStoreId());
    assertEquals("electronics", testItem.getCategory());
  }

  @Test
  public void noArgsConstructorTest() {
    Item item = new Item();
    assertEquals(0, item.getId());
    assertEquals("", item.getName());
    assertEquals(0.0, item.getPrice(), 0.001);
    assertEquals(0, item.getStoreId());
    assertEquals("", item.getCategory());
  }

  @Test
  public void settersTest() {
    testItem.setId(2);
    testItem.setName("Phone");
    testItem.setPrice(599.99);
    testItem.setStoreId(2);
    testItem.setCategory("mobile");

    assertEquals(2, testItem.getId());
    assertEquals("Phone", testItem.getName());
    assertEquals(599.99, testItem.getPrice(), 0.001);
    assertEquals(2, testItem.getStoreId());
    assertEquals("mobile", testItem.getCategory());
  }

  @Test
  public void equalsWithSameObjectTest() {
    assertTrue(testItem.equals(testItem));
  }

  @Test
  public void equalsWithEqualItemTest() {
    Item sameItem = new Item(1, "Different Name", 100.0, 2, "different");
    assertTrue(testItem.equals(sameItem));
  }

  @Test
  public void equalsWithDifferentItemTest() {
    Item differentItem = new Item(2, "Laptop", 999.99, 1, "electronics");
    assertFalse(testItem.equals(differentItem));
  }

  @Test
  public void equalsWithNullTest() {
    assertFalse(testItem.equals(null));
  }

  @Test
  public void equalsWithDifferentClassTest() {
    assertFalse(testItem.equals("Not an Item"));
  }

  @Test
  public void toStringTest() {
    String result = testItem.toString();
    assertTrue(result.contains("1"));
    assertTrue(result.contains("Laptop"));
    assertTrue(result.contains("999.99"));
    assertTrue(result.contains("electronics"));
  }
}

