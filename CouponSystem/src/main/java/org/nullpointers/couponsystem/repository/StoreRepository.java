package org.nullpointers.couponsystem.repository;

import org.nullpointers.couponsystem.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Store entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
}

