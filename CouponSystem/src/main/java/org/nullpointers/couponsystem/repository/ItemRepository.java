package org.nullpointers.couponsystem.repository;

import java.util.List;
import org.nullpointers.couponsystem.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Item entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
  /**
   * Finds all items associated with a specific store.
   *
   * @param storeId the ID of the store
   * @return a list of items for the specified store
   */
  List<Item> findByStoreId(int storeId);

  /**
   * Finds all items matching the given category, ignoring case.
   *
   * @param category the category to filter by
   * @return a list of items in the specified category
   */
  List<Item> findByCategoryIgnoreCase(String category);

  /**
   * Finds all items whose name contains the given keyword, ignoring case.
   *
   * @param keyword the keyword to search for in item names
   * @return a list of items matching the keyword
   */
  List<Item> findByNameContainingIgnoreCase(String keyword);
}

