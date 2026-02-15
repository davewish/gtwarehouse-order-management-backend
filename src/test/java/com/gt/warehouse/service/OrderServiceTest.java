package com.gt.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gt.warehouse.domain.Order;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.OrderStatus;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.dto.CreateOrderRequest;
import com.gt.warehouse.dto.OrderItemRequest;
import com.gt.warehouse.dto.OrderItemResponse;
import com.gt.warehouse.dto.OrderResponse;
import com.gt.warehouse.exception.InvalidOrderStateException;
import com.gt.warehouse.exception.OrderNotFoundException;
import com.gt.warehouse.repository.OrderRepository;
import com.gt.warehouse.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
  @Mock
  private OrderMetrics orderMetrics;
  @InjectMocks
  private OrderService orderService;

  @Test
  void shouldCreateOrderAndReserveStock() {

    Product product = Product.builder().id(1L).name("Milk").sku("mlk0234").build();

    OrderItemRequest itemRequest = new OrderItemRequest(1L, 5);

    List<OrderItemRequest> itemRequests = List.of(itemRequest);
    CreateOrderRequest createOrderRequest = new CreateOrderRequest(itemRequests);
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    orderService.createOrder(createOrderRequest);

    verify(orderRepository).save(any(Order.class));
    verify(inventoryService).reserveStock(any(OrderItem.class));
  }

  @Test
  void shouldThrowExceptionWhenProductNotFound() {

    OrderItemRequest itemRequest = new OrderItemRequest(2L, 5);

    List<OrderItemRequest> itemRequests = List.of(itemRequest);
    CreateOrderRequest createOrderRequest = new CreateOrderRequest(itemRequests);

    when(productRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,
        () -> orderService.createOrder(createOrderRequest));


  }

  @Test
  void shouldMarkOrderAsCancelled() {
    Product product = Product.builder().id(1L).sku("1234").name("fdsfsd").build();
    OrderItem item = OrderItem.builder().id(1L).quantity(10).product(product).build();
    Order order = Order.builder().id(1L).status(OrderStatus.CREATED).items(List.of(item)).build();

    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    orderService.cancelOrder(1L);
    assertEquals(OrderStatus.CANCELLED, order.getStatus());
    verify(inventoryService).returnStock(any());
  }

  @Test
  void shouldCancelOrderThrowOrderNotFoundWhenOrderNotFound() {
    Order order = Order.builder().id(1L).items(List.of()).status(OrderStatus.CREATED).build();
    when(orderRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(2L));

  }

  @Test
  void shouldCancelOrderThrowExceptionInvalidOrderStateExceptionWhenCancelledOrder() {
    Order order = Order.builder().id(1L).status(OrderStatus.CANCELLED).items(List.of()).build();
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
  }

  @Test
  void shouldCancelOrderThrowExceptionInvalidOrderStateExceptionWhenCancelOrder() {
    Order order = Order.builder().id(1L).items(List.of()).status(OrderStatus.SHIPPED).build();
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
  }
  @Test
  void shouldReturnOrders(){
    Product product= Product.builder().id(1L).name("cantu").build();

    OrderItem  item = OrderItem.builder().product(product).quantity(2).build();
    Order order= Order.builder().id(100L).status(OrderStatus.CREATED).items(List.of(item)).build();
    when(orderRepository.findAll()).thenReturn(List.of(order));

     List<OrderResponse> responses = orderService.getOrders();
    assertEquals(1,responses.size());
    OrderResponse response =  responses.get(0);
    assertEquals(100L, response.orderId());
    assertEquals("CREATED", response.status());

  }
}
