package com.gt.warehouse.controller;

import com.gt.warehouse.domain.Order;
import com.gt.warehouse.domain.OrderItem;
import com.gt.warehouse.dto.CreateOrderRequest;
import com.gt.warehouse.dto.OrderItemResponse;
import com.gt.warehouse.dto.OrderResponse;
import com.gt.warehouse.service.OrderService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest orderRequest) {
    Order order = orderService.createOrder(orderRequest);
    List<OrderItemResponse> items = order.getItems().stream()
        .map(item -> new OrderItemResponse(item.getProduct().getId(), item.getQuantity())).toList();

    OrderResponse response = new OrderResponse(order.getId(), order.getStatus().name(), items);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  @PostMapping("/{id}/cancel")
  public  ResponseEntity<Map<String,String>> cancelOrder(@PathVariable Long id){
     orderService.cancelOrder(id);
     return ResponseEntity.ok(Map.of("message","Order cancelled successfully"));
  }

}
