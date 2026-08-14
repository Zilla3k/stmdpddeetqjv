package dev.henriquepelanda.api_pedidos.product.controller;

import dev.henriquepelanda.api_pedidos.product.dto.ProductRequestDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductResponseDTO;
import dev.henriquepelanda.api_pedidos.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
  public List<ProductResponseDTO> findAll(){
    return productService.findAll();
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
