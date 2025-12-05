package org.nullpointers.couponsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents an item in the coupon management system.
 * Each item belongs to a store and has a category for coupon matching.
 */
@Entity
@Table(name = "items")
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String name;
  private double price;
  private int storeId;
  private String category;

  /**
   * Constructs an item with the specified parameters.
   *
   * @param id the unique identifier for this item
   * @param name the name of the item
   * @param price the price of the item
   * @param storeId the store this item belongs to
   * @param category the category of the item for coupon matching
   */
  public Item(int id, String name, double price, int storeId, String category) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.storeId = storeId;
    this.category = category;
  }

  /**
   * Default constructor initializing all fields to default values.
   */
  public Item() {
    this.id = 0;
    this.name = "";
    this.price = 0.0;
    this.storeId = 0;
    this.category = "";
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getStoreId() {
    return storeId;
  }

  public void setStoreId(int storeId) {
    this.storeId = storeId;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Item item = (Item) obj;
    return id == item.id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }

  @Override
  public String toString() {
    return String.format("Item{id=%d, name='%s', price=%.2f, storeId=%d, category='%s'}",
        id, name, price, storeId, category);
  }
}

