package dev.henriquepelanda.api_pedidos.product.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String name;
  private String description;
  private BigDecimal price;
  private UUID categoryId;
  private Integer stockQuantity;

  protected Product()
  {
  }

  public Product
  (
    String name,
    String description,
    BigDecimal price,
    UUID categoryId,
    Integer stockQuantity
  ) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.categoryId = categoryId;
    this.stockQuantity = stockQuantity;
  }

  public UUID getId(){
    return id;
  }

  public String getName(){
    return this.name;
  }

  public String getDescription(){
    return this.description;
  }

  public BigDecimal getPrice(){
    return this.price;
  }

  public UUID getCategoryId(){
    return this.categoryId;
  }

  public Integer getStockQuantity(){
    return this.stockQuantity;
  }

  public void update
  (
    String name,
    String description,
    BigDecimal price,
    UUID categoryId,
    Integer stockQuantity
  )
  {
    if (name != null) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
    if (price != null) {
      this.price = price;
    }
    if (categoryId != null) {
      this.categoryId = categoryId;
    }
    if (stockQuantity != null) {
      this.stockQuantity = stockQuantity;
    }
  }

  public void decreaseStock(Integer quantity) {
    if (quantity == null) {
      return;
    }

    this.stockQuantity = this.stockQuantity - quantity;
  }

  public void increaseStock(Integer quantity) {
    if (quantity == null) {
      return;
    }

    this.stockQuantity = this.stockQuantity + quantity;
  }
}
