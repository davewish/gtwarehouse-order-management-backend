package com.gt.warehouse.service;
import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.OrderItemBatchAllocation;
import com.gt.warehouse.repository.InventoryBatchRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class InventoryService {

  private final InventoryBatchRepository inventoryBatchRepository;

  public InventoryService(InventoryBatchRepository inventoryBatchRepository) {
    this.inventoryBatchRepository = inventoryBatchRepository;
  }

  @Transactional
  public List<OrderItemBatchAllocation> reserveStock(OrderItem orderItem) {

    List<InventoryBatch> batches =
        inventoryBatchRepository.findAvailableBatchesForUpdate(orderItem.getProduct());

    int remaining = orderItem.getQuantity();
    List<OrderItemBatchAllocation> allocations = new ArrayList<>();

    for (InventoryBatch batch : batches) {
      if (remaining <= 0) break;

      int available = batch.getQuantity() - batch.getReservedQuantity();

      if(available <= 0) continue;

      int toAllocate = Math.min(available, remaining);
      batch.setReservedQuantity(batch.getReservedQuantity()+ toAllocate);

      OrderItemBatchAllocation allocation = new OrderItemBatchAllocation();
      allocation.setOrderItem(orderItem);
      allocation.setBatch(batch);
      allocation.setQuantityAllocated(toAllocate);

      allocations.add(allocation);
      remaining -= toAllocate;
    }

    if (remaining > 0)
      throw new IllegalArgumentException("Insufficient stock");

    return allocations;
  }


  @Transactional
  public void returnStock(OrderItem orderItem) {
    for (OrderItemBatchAllocation allocation : orderItem.getBatchAllocations()) {
      InventoryBatch batch = allocation.getBatch();
      batch.setReservedQuantity(batch.getReservedQuantity() - allocation.getQuantityAllocated());
      batch.setQuantity(batch.getQuantity() + allocation.getQuantityAllocated());
    }
  }

}
