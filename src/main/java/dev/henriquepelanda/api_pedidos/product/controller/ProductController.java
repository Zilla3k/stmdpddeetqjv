package dev.henriquepelanda.api_pedidos.product.controller;

import dev.henriquepelanda.api_pedidos.product.dto.ProductRequestDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductFilterDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductResponseDTO;
import dev.henriquepelanda.api_pedidos.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponseDTO create(@RequestBody @Valid ProductRequestDTO request)
  {
    return productService.create(request);
  }

  @GetMapping
  public Page<ProductResponseDTO> findAll(
          @RequestParam(required = false) String name,
          @RequestParam(required = false) UUID categoryId,
          Pageable pageable
  ){
    return productService.findAll(new ProductFilterDTO(name, categoryId), pageable);
  }

  @GetMapping("/{id}")
  public ProductResponseDTO findById(@PathVariable UUID id){
    return productService.findById(id);
  }

  @PatchMapping("/{id}")
  public ProductResponseDTO update(@PathVariable UUID id, @RequestBody ProductRequestDTO request){
    return productService.update(id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id){
    productService.delete(id);
  }
}
