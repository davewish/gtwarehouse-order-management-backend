package com.gt.warehouse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

@Entity
@Table(name = "inventory_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBatch {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name="product_Id")
  private Product product;

  @Column(nullable = false)
  private Integer  quantity;
  @Column(nullable = false)
  private LocalDate expiryDate;
  @Column(nullable = false,updatable = false)
  private LocalDateTime createdAt;
  @PrePersist
  public void prePersist(){
    this.createdAt= LocalDateTime.now();
  }
}
