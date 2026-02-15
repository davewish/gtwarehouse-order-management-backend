package com.gt.warehouse.service;

import com.gt.warehouse.domain.Order;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.OrderItemBatchAllocation;
import com.gt.warehouse.domain.OrderStatus;
import com.gt.warehouse.dto.CreateOrderRequest;
import com.gt.warehouse.dto.OrderItemResponse;
import com.gt.warehouse.dto.OrderResponse;
import com.gt.warehouse.exception.InvalidOrderStateException;
import com.gt.warehouse.exception.OrderNotFoundException;
import com.gt.warehouse.repository.OrderRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class OrderService {

  private final OrderRepository orderRepository;
  private final InventoryService inventoryService;
  private final ProductRepository productRepository;
  private final OrderMetrics orderMetrics;

  public OrderService(OrderRepository orderRepository, InventoryService inventoryService,
      ProductRepository productRepository, OrderMetrics orderMetrics) {
    this.orderRepository = orderRepository;
    this.inventoryService = inventoryService;
    this.productRepository = productRepository;
    this.orderMetrics= orderMetrics;
  }


  @Transactional
  public Order createOrder(CreateOrderRequest request) {
    List<OrderItem> orderItems = request.items().stream()
        .map(req -> OrderItem.builder()
            .product(productRepository.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found")))
            .quantity(req.quantity())
            .build())
        .collect(Collectors.toList());

    Order order = Order.builder()
        .status(OrderStatus.CREATED)
        .items(orderItems)
        .build();
    for (OrderItem orderItem : orderItems) {
      orderItem.setOrder(order);
    }
    for (OrderItem orderItem : orderItems) {
      List<OrderItemBatchAllocation> allocations = inventoryService.reserveStock(orderItem);
      orderItem.setBatchAllocations(allocations);
    }
    orderMetrics.increment();
    return orderRepository.save(order);

  }

  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException("Order not found"));

    if (order.getStatus() == OrderStatus.CANCELLED) {
      throw new InvalidOrderStateException("Order already cancelled");
    }
    if (order.getStatus() == OrderStatus.SHIPPED) {
      throw new InvalidOrderStateException("Cannot cancel shipped order");
    }
    for (OrderItem orderItem : order.getItems()) {
      inventoryService.returnStock(orderItem);
    }
     order.setStatus(OrderStatus.CANCELLED);

  }

  @Transactional
  public List<OrderResponse> getOrders() {
    return orderRepository.findAll()
        .stream()
        .map(order -> new OrderResponse(
            order.getId(),
            order.getStatus().toString(),
            order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getQuantity()
                ))
                .toList()
        ))
        .toList();
  }

}



