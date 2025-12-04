package org.nullpointers.couponsystem.repository;

import java.util.List;
import org.nullpointers.couponsystem.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
  List<Item> findByStoreId(int storeId);

  List<Item> findByCategoryIgnoreCase(String category);

  List<Item> findByNameContainingIgnoreCase(String keyword);
}

