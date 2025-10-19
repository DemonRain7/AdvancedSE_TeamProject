package org.nullpointers.couponsystem.model;

public class Item {
  private int id;
  private String name;
  private double price;
  private int storeId;
  private String category;

  public Item(int id, String name, double price, int storeId, String category) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.storeId = storeId;
    this.category = category;
  }

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
  public String toString() {
    return String.format("Item{id=%d, name='%s', price=%.2f, storeId=%d, category='%s'}",
        id, name, price, storeId, category);
  }
}

