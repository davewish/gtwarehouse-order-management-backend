package com.gt.warehouse.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.gt.warehouse.BaseIntegrationTest;
import com.gt.warehouse.domain.BatchStatus;
import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.repository.InventoryBatchRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ser.Serializers.Base;

public class InventoryBatchTest extends BaseIntegrationTest {
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private InventoryBatchRepository inventoryBatchRepository;

  @Test
  void shouldDefaultStatusWhenNull() {
    InventoryBatch inventoryBatch = InventoryBatch.builder()
        .quantity(10)
        .reservedQuantity(0)
        .expiryDate(LocalDate.now().plusDays(5))
        .status(null)
        .build();
    inventoryBatch.prePersist();
    assertEquals(BatchStatus.AVAILABLE, inventoryBatch.getStatus());
    assertNotNull(inventoryBatch.getCreatedAt());
  }
  @Test
 void shouldNotOverrideExistingStatus(){
   InventoryBatch batch =
       InventoryBatch.builder()
           .quantity(10)
           .reservedQuantity(0)
           .expiryDate(LocalDate.now().plusDays(5))
           .status(BatchStatus.RESERVED)
           .build();

   batch.prePersist();

   assertEquals(BatchStatus.RESERVED, batch.getStatus());
 }
  @Test
  void shouldSetCreatedAtWhenSaved() {

    Product product = productRepository.save(
        Product.builder().name("Milk").sku("MLK").build());

    InventoryBatch batch =
        inventoryBatchRepository.save(
            InventoryBatch.builder()
                .product(product)
                .quantity(5)
                .reservedQuantity(0)
                .expiryDate(LocalDate.now().plusDays(5))
                .build());

    assertNotNull(batch.getCreatedAt());
    assertEquals(BatchStatus.AVAILABLE, batch.getStatus());
  }
  @Test
  void shouldUseNoArgsConstructorAndSetters() {
Product product= Product.builder().name("food").sku("sdfsdf").id(10L).build();
    InventoryBatch batch = new InventoryBatch();

    batch.setId(1L);
    batch.setQuantity(20);
    batch.setReservedQuantity(5);
    batch.setStatus(BatchStatus.AVAILABLE);
    batch.setExpiryDate(LocalDate.now().plusDays(3));
    batch.setProduct(product);
    batch.setCreatedAt(LocalDateTime.now());

    assertEquals(1L, batch.getId());
    assertEquals(20, batch.getQuantity());
    assertEquals(5, batch.getReservedQuantity());
    assertNotNull(batch.getCreatedAt());
    assertEquals(BatchStatus.AVAILABLE, batch.getStatus());
  }
  @Test
  void shouldUseAllArgsConstructor() {

    InventoryBatch batch =
        new InventoryBatch(
            1L,
            null,
            50,
            BatchStatus.RESERVED,
            10,
            LocalDate.now().plusDays(5),
            null);

    assertEquals(50, batch.getQuantity());
    assertEquals(10, batch.getReservedQuantity());
    assertEquals(BatchStatus.RESERVED, batch.getStatus());
  }

  @Test
  void shouldSetProductAndExpiryDate() {

    Product product =
        Product.builder()
            .name("Milk")
            .sku("MLK-1")
            .build();

    LocalDate expiry = LocalDate.now().plusDays(7);

    InventoryBatch batch = new InventoryBatch();
    batch.setProduct(product);
    batch.setExpiryDate(expiry);

    assertEquals(product, batch.getProduct());
    assertEquals(expiry, batch.getExpiryDate());
  }

}
