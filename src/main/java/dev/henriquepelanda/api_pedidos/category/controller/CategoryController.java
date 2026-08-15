package dev.henriquepelanda.api_pedidos.category.controller;


import dev.henriquepelanda.api_pedidos.category.dto.CategoryRequestDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryUpdateDTO;
import dev.henriquepelanda.api_pedidos.category.service.CategoryService;
import dev.henriquepelanda.api_pedidos.common.dto.ErrorResponseDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Operations for creating and querying categories.")
public class CategoryController {
  private final CategoryService _categoryService;

  public CategoryController(CategoryService categoryService){
    this._categoryService = categoryService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create category", description = "Creates a new category.")
  @ApiResponses({
          @ApiResponse(responseCode = "201", description = "Category created successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public CategoryResponseDTO create(@RequestBody @Valid CategoryRequestDTO request){
    return _categoryService.create(request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "List categories", description = "Lists all registered categories.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Categories returned successfully")
  })
  public List<CategoryResponseDTO> findAll(){
    return _categoryService.findAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Get category by id", description = "Gets a category by its identifier.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Category returned successfully"),
          @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public CategoryResponseDTO findById(@PathVariable UUID id){
    return _categoryService.findById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(summary = "Update category", description = "Partially updates a category.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Category updated successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
          @ApiResponse(responseCode = "422", description = "Business rule violated", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public CategoryResponseDTO update(@PathVariable UUID id, @RequestBody @Valid CategoryUpdateDTO request){
    return _categoryService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete category", description = "Deletes a category by its identifier.")
  @ApiResponses({
          @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
  })
  public void delete(@PathVariable UUID id){
    _categoryService.delete(id);
  }
}
