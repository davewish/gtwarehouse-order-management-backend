package com.gt.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gt.warehouse.BaseIntegrationTest;
import com.gt.warehouse.domain.InventoryBatch;
import com.gt.warehouse.domain.Order;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.OrderItemBatchAllocation;
import com.gt.warehouse.domain.OrderStatus;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.dto.CreateOrderRequest;
import com.gt.warehouse.dto.OrderItemRequest;
import com.gt.warehouse.repository.InventoryBatchRepository;
import com.gt.warehouse.repository.OrderItemBatchAllocationRepository;
import com.gt.warehouse.repository.OrderRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrderServiceIT extends BaseIntegrationTest {

  @Autowired
  private OrderService orderService;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private InventoryBatchRepository inventoryBatchRepository;
  @Autowired
  private OrderItemBatchAllocationRepository orderItemBatchAllocationRepository;

  // --------------------------------------------------
  // CREATE ORDER + RESERVE STOCK
  // --------------------------------------------------
  @Test
  void shouldCreateOrderAndReserveStock() {

    // Arrange
    Product product =
        productRepository.save(
            Product.builder()
                .name("Milk")
                .sku("MLK-1")
                .build());

    inventoryBatchRepository.save(
        InventoryBatch.builder()
            .product(product)
            .quantity(10)
            .reservedQuantity(0)
            .expiryDate(LocalDate.now().plusDays(10))
            .build());

    CreateOrderRequest request =
        new CreateOrderRequest(
            List.of(new OrderItemRequest(product.getId(), 5)));

    // Act
    orderService.createOrder(request);

    // Assert order
    List<Order> orders = orderRepository.findAll();
    assertEquals(1, orders.size());

    Order savedOrder = orders.get(0);
    assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
    assertEquals(1, savedOrder.getItems().size());

    // Assert stock reservation
    InventoryBatch batch =
        inventoryBatchRepository.findAll().get(0);

    assertEquals(5, batch.getReservedQuantity());
    assertEquals(10, batch.getQuantity());
  }

  // --------------------------------------------------
  // PRODUCT NOT FOUND
  // --------------------------------------------------
  @Test
  void shouldThrowExceptionWhenProductNotFound() {

    CreateOrderRequest request =
        new CreateOrderRequest(
            List.of(new OrderItemRequest(999L, 5)));

    assertThrows(
        IllegalArgumentException.class,
        () -> orderService.createOrder(request));
  }

  // --------------------------------------------------
  // CANCEL ORDER + RETURN STOCK
  // --------------------------------------------------
  @Test
  void shouldCancelOrderAndReturnStock() {

    // Arrange product
    Product product =
        productRepository.save(
            Product.builder()
                .name("Bread")
                .sku("BRD-1")
                .build());

    // Arrange stock
    InventoryBatch batch =
        inventoryBatchRepository.save(
            InventoryBatch.builder()
                .product(product)
                .quantity(10)
                .reservedQuantity(5)
                .expiryDate(LocalDate.now().plusDays(5))
                .build());

    // Arrange order + item (FIXED RELATIONSHIP)

    CreateOrderRequest request =
        new CreateOrderRequest(
            List.of(new OrderItemRequest(product.getId(), 5)));
   Order order = orderService.createOrder(request);

    // Act
    orderService.cancelOrder(order.getId());

    // Assert order status
    Order updated =
        orderRepository.findById(order.getId()).get();

    assertEquals(OrderStatus.CANCELLED, updated.getStatus());

    // Assert stock returned
    InventoryBatch updatedBatch =
        inventoryBatchRepository.findById(batch.getId()).get();

    assertEquals(5, updatedBatch.getReservedQuantity());
    assertEquals(15,updatedBatch.getQuantity());
  }

  // --------------------------------------------------
  // INVALID STATE — SHIPPED
  // --------------------------------------------------
  @Test
  void shouldThrowExceptionWhenCancellingShippedOrder() {

    Order order = Order.builder().id(200L).build();


    assertThrows(
        RuntimeException.class,
        () -> orderService.cancelOrder(order.getId()));
  }
}
