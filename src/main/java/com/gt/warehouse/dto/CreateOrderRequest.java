package com.gt.warehouse.dto;

import java.util.List;

public record CreateOrderRequest(List<OrderItemRequest> items) {

}
