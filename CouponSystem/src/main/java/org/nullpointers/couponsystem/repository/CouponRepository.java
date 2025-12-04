package org.nullpointers.couponsystem.repository;

import java.util.List;
import org.nullpointers.couponsystem.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
  List<Coupon> findByStoreId(int storeId);
}

