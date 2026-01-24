package com.gt.warehouse.repository;

import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.Product;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch,Long> {
  List<InventoryBatch> findByProductAndQuantityGreaterThanByExpiryDateAsc(Product product ,Integer quantity);
  List<Integer> findByExpiryDateBefore(LocalDate date);


}
