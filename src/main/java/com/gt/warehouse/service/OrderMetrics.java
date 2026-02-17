package com.gt.warehouse.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service

public class OrderMetrics {

  private final Counter orderCreatedCounter;

  public OrderMetrics(MeterRegistry registry) {
    this.orderCreatedCounter = Counter.builder("order_created_total")
        .description("Total number of orders created").register(registry);
  }

  public void increment() {
    orderCreatedCounter.increment();
  }
}
