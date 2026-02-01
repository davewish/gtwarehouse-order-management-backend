package com.gt.warehouse.dto;

import com.gt.warehouse.domain.OrderStatus;
import java.util.List;

public record OrderResponse(Long orderId, String status ,List<OrderItemResponse> items )  {

}
