package org.nullpointers.couponsystem.model;

public class Store {
  private int id;
  private String name;

  public Store(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public Store() {
    this.id = 0;
    this.name = "";
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Store store = (Store) obj;
    return id == store.id;
  }

  @Override
  public String toString() {
    return String.format("Store{id=%d, name='%s'}", id, name);
  }
}

