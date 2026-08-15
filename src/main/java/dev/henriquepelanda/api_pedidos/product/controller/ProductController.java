package dev.henriquepelanda.api_pedidos.product.controller;

import dev.henriquepelanda.api_pedidos.product.dto.ProductRequestDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductFilterDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductResponseDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductUpdateDTO;
import dev.henriquepelanda.api_pedidos.product.service.ProductService;
import dev.henriquepelanda.api_pedidos.common.dto.ErrorResponseDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Operations for creating, querying, and updating products.")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create product", description = "Creates a new product and links it to an existing category.")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "Product created successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "404", description = "Category or product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ProductResponseDTO create(@RequestBody @Valid ProductRequestDTO request)
  {
    return productService.create(request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "List products", description = "Lists products with optional name and category filters.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Products returned successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public Page<ProductResponseDTO> findAll(
          @RequestParam(required = false) String name,
          @RequestParam(required = false) UUID categoryId,
          Pageable pageable
  ){
    return productService.findAll(new ProductFilterDTO(name, categoryId), pageable);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get product by id", description = "Gets a product by its identifier.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Product returned successfully"),
          @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ProductResponseDTO findById(@PathVariable UUID id){
    return productService.findById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update product", description = "Partially updates a product.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Product updated successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "404", description = "Product or category not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public ProductResponseDTO update(@PathVariable UUID id, @RequestBody @Valid ProductUpdateDTO request){
    return productService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete product", description = "Deletes a product by its identifier.")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public void delete(@PathVariable UUID id){
    productService.delete(id);
  }
}
