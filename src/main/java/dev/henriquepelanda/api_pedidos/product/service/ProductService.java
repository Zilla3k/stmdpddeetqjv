package dev.henriquepelanda.api_pedidos.product.service;

import dev.henriquepelanda.api_pedidos.category.repository.CategoryRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.product.dto.ProductFilterDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductRequestDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductResponseDTO;
import dev.henriquepelanda.api_pedidos.product.entity.Product;
import dev.henriquepelanda.api_pedidos.product.repository.ProductRepository;
import dev.henriquepelanda.api_pedidos.product.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductService {
  private final ProductRepository _productRepository;
  private final CategoryRepository categoryRepository;

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository){
    this._productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  }

  public ProductResponseDTO create(ProductRequestDTO request){
    if(_productRepository.existsByName(request.name())){
      throw new BusinessException("Product name already exists!");
    }

    if(!categoryRepository.existsById(request.categoryId())) {
      throw new BusinessException("Category not found!");
    }

    if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessException("Price must be greater than zero!");
    }

    Product product = new Product(
            request.name(),
            request.description(),
            request.price(),
            request.categoryId(),
            request.stockQuantity()
    );

    Product savedProduct = _productRepository.save(
            product
    );

    return new ProductResponseDTO(
            savedProduct.getId(),
            savedProduct.getName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.getCategoryId(),
            savedProduct.getStockQuantity()
    );
  }

  public Page<ProductResponseDTO> findAll(ProductFilterDTO filter, Pageable pageable){
    return _productRepository.findAll(ProductSpecification.withFilters(filter), pageable)
            .map(product -> new ProductResponseDTO(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategoryId(),
                    product.getStockQuantity()
            ));
  }

  public ProductResponseDTO findById(UUID id){
    Product product = _productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Product not found!"));

    return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategoryId(),
            product.getStockQuantity()
    );
  }

  public ProductResponseDTO update(UUID id, ProductRequestDTO request){
    Product product = _productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Product not found!"));

    if (request.categoryId() != null && !categoryRepository.existsById(request.categoryId())) {
      throw new BusinessException("Category not found!");
    }

    product.update(
            request.name(),
            request.description(),
            request.price(),
            request.categoryId(),
            request.stockQuantity()
    );

    Product updated = _productRepository.save(product);

    return new ProductResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getDescription(),
            updated.getPrice(),
            updated.getCategoryId(),
            updated.getStockQuantity()
    );
  }

  public void delete(UUID id){
    Product product = _productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Product not found!"));

    _productRepository.delete(product);
  }
}
