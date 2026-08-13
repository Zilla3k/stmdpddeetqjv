package dev.henriquepelanda.api_pedidos.product.repository;

import dev.henriquepelanda.api_pedidos.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  boolean existsByName(String name);
  boolean existsByCategoryId(UUID categoryId);
}
