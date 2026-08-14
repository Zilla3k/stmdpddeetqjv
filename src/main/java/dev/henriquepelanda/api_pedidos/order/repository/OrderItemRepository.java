package dev.henriquepelanda.api_pedidos.order.repository;

import dev.henriquepelanda.api_pedidos.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
