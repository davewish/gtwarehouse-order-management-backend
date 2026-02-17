package com.gt.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.repository.InventoryBatchRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.time.LocalDate;
import net.bytebuddy.NamingStrategy.Suffixing.BaseNameResolver.ForGivenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(InventoryService.class)
public class InventoryServiceTest {

  @Autowired
  private InventoryService inventoryService;
  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private InventoryBatchRepository inventoryBatchRepository;

  @Test
  void shouldReserveStocksUsingFifo() {
    Product product = productRepository.save(Product.builder().name("milk").sku("234567").build());
    InventoryBatch batch1 = inventoryBatchRepository.save(
        InventoryBatch.builder().product(product).quantity(5).reservedQuantity(0).expiryDate(LocalDate.of(2026, 1, 1))
            .build());
    InventoryBatch batch2 = inventoryBatchRepository.save(
        InventoryBatch.builder().product(product).quantity(10).reservedQuantity(0).expiryDate(LocalDate.of(2026, 2, 1))
            .build());
    OrderItem orderItem = OrderItem.builder().product(product).quantity(7).build();
    inventoryService.reserveStock(orderItem);
    InventoryBatch  updated1= inventoryBatchRepository.findById(batch1.getId()).get();
    InventoryBatch updated2= inventoryBatchRepository.findById(batch2.getId()).get();
    assertEquals(5, updated1.getQuantity());
    assertEquals(10,updated2.getQuantity());
    assertEquals(5,updated1.getReservedQuantity());
    assertEquals(2,updated2.getReservedQuantity());
  }

  @Test
  void shouldThrowExceptionWhenStockInsufficient(){

    Product product= productRepository.save(Product.builder().name("bread").sku("brd0123").build());
    inventoryBatchRepository.save(InventoryBatch.builder().product(product).reservedQuantity(0).quantity(3).expiryDate(LocalDate.of(2026,1,1)).build());
    OrderItem item= OrderItem.builder().product(product).quantity(10).build();
    assertThrows(IllegalArgumentException.class, ()->inventoryService.reserveStock(item));

  }
}
