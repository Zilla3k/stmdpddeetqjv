package dev.henriquepelanda.api_pedidos.client.repository;

import dev.henriquepelanda.api_pedidos.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
  boolean existsByEmail(String email);
  boolean existsByDocument(String document);
}
