package org.nullpointers.couponsystem.repository;

import java.util.List;
import org.nullpointers.couponsystem.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Coupon entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
  /**
   * Finds all coupons associated with a specific store.
   *
   * @param storeId the ID of the store
   * @return a list of coupons for the specified store
   */
  List<Coupon> findByStoreId(int storeId);
}

