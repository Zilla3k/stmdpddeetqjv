package dev.henriquepelanda.api_pedidos.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.henriquepelanda.api_pedidos.category.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
  boolean existsByName(String name);
}
