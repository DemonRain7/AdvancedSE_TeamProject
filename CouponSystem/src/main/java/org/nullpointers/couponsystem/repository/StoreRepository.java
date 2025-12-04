package org.nullpointers.couponsystem.repository;

import org.nullpointers.couponsystem.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
}

