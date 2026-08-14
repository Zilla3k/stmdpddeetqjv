package dev.henriquepelanda.api_pedidos.product.repository;

import dev.henriquepelanda.api_pedidos.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
  boolean existsByName(String name);
}
