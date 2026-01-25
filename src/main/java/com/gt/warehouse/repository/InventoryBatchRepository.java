package com.gt.warehouse.repository;

import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.Product;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch,Long> {
  List<InventoryBatch> findByProductAndQuantityGreaterThanOrderByExpiryDateAsc(Product product ,Integer quantity);
  List<Integer> findByExpiryDateBefore(LocalDate date);
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT b FROM InventoryBatch b WHERE b.product = :product AND b.quantity > 0 ORDER BY b.expiryDate ASC")
  List<InventoryBatch> findAvailableBatchesForUpdate(Product product);



}
