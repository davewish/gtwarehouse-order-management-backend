package com.gt.warehouse.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gt.warehouse.domain.Order;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.dto.CreateOrderRequest;
import com.gt.warehouse.dto.OrderItemRequest;
import com.gt.warehouse.repository.OrderRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
  @Mock
  private ProductRepository productRepository;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private InventoryService inventoryService;
  @InjectMocks
  private OrderService orderService;
@Test
  void shouldCreateOrderAndReserveStock(){
    Product product= Product.builder().id(1L).name("Milk").sku("mlk0234").build();
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
  OrderItemRequest itemRequest = new OrderItemRequest(1L,5);
  List<OrderItemRequest> itemRequests=List.of(itemRequest);
  CreateOrderRequest createOrderRequest= new CreateOrderRequest(itemRequests);
     orderService.createOrder(createOrderRequest);
     verify(orderRepository).save(any(Order.class));
     verify(inventoryService).reserveStock(any(OrderItem.class));
  }
}
