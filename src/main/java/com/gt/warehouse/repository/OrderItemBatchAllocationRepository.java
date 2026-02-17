package com.gt.warehouse.repository;

import com.gt.warehouse.domain.OrderItemBatchAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemBatchAllocationRepository extends  JpaRepository<OrderItemBatchAllocation,Long> {

}
