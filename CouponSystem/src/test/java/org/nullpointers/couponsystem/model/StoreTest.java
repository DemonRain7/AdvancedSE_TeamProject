package org.nullpointers.couponsystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the Store class.
 */
@SpringBootTest
public class StoreTest {
  private Store testStore;

  @BeforeEach
  public void setUp() {
    testStore = new Store(1, "Tech Store");
  }

  @Test
  public void completeConstructorTest() {
    assertEquals(1, testStore.getId());
    assertEquals("Tech Store", testStore.getName());
  }

  @Test
  public void noArgsConstructorTest() {
    Store store = new Store();
    assertEquals(0, store.getId());
    assertEquals("", store.getName());
  }

  @Test
  public void settersTest() {
    testStore.setId(2);
    testStore.setName("Book Store");

    assertEquals(2, testStore.getId());
    assertEquals("Book Store", testStore.getName());
  }

  @Test
  public void equalsWithSameObjectTest() {
    assertTrue(testStore.equals(testStore));
  }

  @Test
  public void equalsWithEqualStoreTest() {
    Store sameStore = new Store(1, "Different Name");
    assertTrue(testStore.equals(sameStore));
  }

  @Test
  public void equalsWithDifferentStoreTest() {
    Store differentStore = new Store(2, "Tech Store");
    assertFalse(testStore.equals(differentStore));
  }

  @Test
  public void equalsWithNullTest() {
    assertFalse(testStore.equals(null));
  }

  @Test
  public void equalsWithDifferentClassTest() {
    assertFalse(testStore.equals("Not a Store"));
  }

  @Test
  public void toStringTest() {
    String result = testStore.toString();
    assertTrue(result.contains("1"));
    assertTrue(result.contains("Tech Store"));
  }
}

