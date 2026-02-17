package com.gt.warehouse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "inventory_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBatch {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name="product_Id")
  private Product product;

  @Column(nullable = false)
  private Integer  quantity;
  @Enumerated(EnumType.STRING)

  @Column(nullable = false)
  private BatchStatus status;

  @Column(nullable = false)
  private Integer reservedQuantity;

  @Column(nullable = false)
  private LocalDate expiryDate;
  @Column(nullable = false,updatable = false)
  private LocalDateTime createdAt;
  @PrePersist
  public void prePersist(){
    this.createdAt= LocalDateTime.now();
    if(this.status== null) this.status =BatchStatus.AVAILABLE;




  }
}
