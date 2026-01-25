package com.gt.warehouse.repository;

import com.gt.warehouse.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
