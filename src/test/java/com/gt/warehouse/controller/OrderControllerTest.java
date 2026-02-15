package com.gt.warehouse.controller;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.gt.warehouse.domain.Order;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.domain.OrderStatus;
import com.gt.warehouse.domain.Product;
import com.gt.warehouse.dto.CreateOrderRequest;
import com.gt.warehouse.dto.OrderItemRequest;
import com.gt.warehouse.dto.OrderItemResponse;
import com.gt.warehouse.dto.OrderResponse;
import com.gt.warehouse.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(OrderController.class)
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private OrderService orderService;

  @Test
  void shouldCreateOrder() throws Exception {
    Product product = Product.builder().id(1L).build();
    OrderItem item = OrderItem.builder().id(1L).product(product).quantity(5).build();
    Order order = Order.builder().id(10L).status(OrderStatus.CREATED).items(List.of(item)).build();
    OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 5);

    when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(order);
    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
        {
          "items": [
            {
              "productId": 1,
              "quantity": 5
            }
          ]
        }
        """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderId").value(10))
        .andExpect(jsonPath("$.status").value("CREATED"));


  }
  @Test
  void shouldCancerOrderSuccessfully() throws Exception {
    Long orderId=10L;
    doNothing().when(orderService).cancelOrder(orderId);
    mockMvc.perform( post("/orders/{id}/cancel",10L).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Order cancelled successfully"));
  }

  @Test
  void shouldReturnOrders() throws Exception {
    OrderItemResponse item= new OrderItemResponse(1L,2);
    OrderResponse  order= new OrderResponse(100L, "CREATED", List.of(item));
    when(orderService.getOrders()).thenReturn(List.of(order));

    mockMvc.perform(get("/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderId").value(100L))
        .andExpect(jsonPath("$[0].status").value("CREATED"))
        .andExpect(jsonPath("$[0].items[0].productId").value(1L))
        .andExpect(jsonPath("$[0].items[0].quantity").value(2));

  }

}
