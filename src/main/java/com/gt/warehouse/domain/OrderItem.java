package com.gt.warehouse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="order_items")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

  @ManyToOne(optional=false)
   private Order order;
  @ManyToOne(optional = false)
  private Product product;

  @Column(nullable = false)
  private Integer quantity;


}
