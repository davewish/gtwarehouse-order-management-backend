package com.gt.warehouse.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder.Case;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id ;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status ;
  @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL)
  private List<OrderItem> items;
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

@PrePersist
  public void prePersist(){
  this.createdAt=LocalDateTime.now();
  this.status=OrderStatus.CREATED;
}
}
