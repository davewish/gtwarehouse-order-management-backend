package com.gt.warehouse.service;

import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.repository.InventoryBatchRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {
  private final InventoryBatchRepository inventoryBatchRepository;

  @Transactional
  public  void reserveStock(Product product, int requiredQuantity){
    List<InventoryBatch> batches= inventoryBatchRepository.findByProductAndQuantityGreaterThanOrderByExpiryDateAsc(product,0);
    int remaining= requiredQuantity;
    for(InventoryBatch batch: batches){
      if(remaining < 0) break;
      int available= batch.getQuantity();
      int toDeduct=Math.min(available , remaining);
      batch.setQuantity(available - toDeduct);
    }
     if( remaining > 0 ){
       throw new IllegalArgumentException("Insufficient stock for product : " + product.getSku());
     }
  }

}
