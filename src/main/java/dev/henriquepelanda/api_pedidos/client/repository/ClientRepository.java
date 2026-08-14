package dev.henriquepelanda.api_pedidos.client.repository;

import dev.henriquepelanda.api_pedidos.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {
  boolean existsByEmail(String email);
  boolean existsByDocument(String document);
}
