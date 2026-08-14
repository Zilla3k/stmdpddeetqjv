package dev.henriquepelanda.api_pedidos.product.specification;

import dev.henriquepelanda.api_pedidos.product.dto.ProductFilterDTO;
import dev.henriquepelanda.api_pedidos.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProductSpecification {
    private ProductSpecification() {
    }

    public static Specification<Product> withFilters(ProductFilterDTO filter) {
        return Specification.where(nameContains(filter.name()))
                .and(hasCategoryId(filter.categoryId()));
    }

    private static Specification<Product> nameContains(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    private static Specification<Product> hasCategoryId(java.util.UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("categoryId"), categoryId);
        };
    }
}
