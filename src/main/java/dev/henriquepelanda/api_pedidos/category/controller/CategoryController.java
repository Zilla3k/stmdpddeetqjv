package dev.henriquepelanda.api_pedidos.category.controller;


import dev.henriquepelanda.api_pedidos.category.dto.CategoryRequestDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {
  private final CategoryService _categoryService;

  public CategoryController(CategoryService categoryService){
    this._categoryService = categoryService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponseDTO create(@RequestBody @Valid CategoryRequestDTO request){
    return _categoryService.createCategory(request);
  }
}
