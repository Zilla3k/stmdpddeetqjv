package dev.henriquepelanda.api_pedidos.client.specifications;

import dev.henriquepelanda.api_pedidos.client.entity.Client;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ClientSpecification {
  private ClientSpecification(){
  }

  public static Specification<Client> nameContains(String name) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(name)){
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Client> emailContains(String email) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(email)){
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    };
  }
}
