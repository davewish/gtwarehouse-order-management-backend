package com.gt.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gt.warehouse.BaseIntegrationTest;
import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.repository.InventoryBatchRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class InventoryServiceIT  extends BaseIntegrationTest {
  @Autowired
  private InventoryService inventoryService;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private InventoryBatchRepository inventoryBatchRepository;
@Test
  void shouldReserveStockUsingFifo(){
    Product product =
        productRepository.save(
            Product.builder().name("milk").sku("234567").build());

    InventoryBatch batch1 =
        inventoryBatchRepository.save(
            InventoryBatch.builder()
                .product(product)
                .quantity(5)
                .reservedQuantity(0)
                .expiryDate(LocalDate.of(2026, 1, 1))
                .build());

    InventoryBatch batch2 =
        inventoryBatchRepository.save(
            InventoryBatch.builder()
                .product(product)
                .quantity(10)
                .reservedQuantity(0)
                .expiryDate(LocalDate.of(2026, 2, 1))
                .build());

    OrderItem orderItem =
        OrderItem.builder().product(product).quantity(7).build();

    inventoryService.reserveStock(orderItem);

    InventoryBatch updated1 =
        inventoryBatchRepository.findById(batch1.getId()).get();

    InventoryBatch updated2 =
        inventoryBatchRepository.findById(batch2.getId()).get();

    // FIFO check
    assertEquals(5, updated1.getReservedQuantity());
    assertEquals(5, updated1.getQuantity());

    assertEquals(2, updated2.getReservedQuantity());
    assertEquals(10, updated2.getQuantity());
  }
  @Test
  void shouldThrowExceptionWhenStockInsufficient(){

    Product product= productRepository.save(Product.builder().name("bread").sku("brd0123").build());
    inventoryBatchRepository.save(InventoryBatch.builder().product(product).reservedQuantity(0).quantity(3).expiryDate(LocalDate.of(2026,1,1)).build());
    OrderItem item= OrderItem.builder().product(product).quantity(10).build();
    assertThrows(IllegalArgumentException.class, ()->inventoryService.reserveStock(item));

  }


}
