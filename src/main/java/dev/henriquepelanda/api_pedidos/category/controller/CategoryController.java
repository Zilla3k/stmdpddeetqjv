package dev.henriquepelanda.api_pedidos.category.controller;


import dev.henriquepelanda.api_pedidos.category.dto.CategoryRequestDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryUpdateDTO;
import dev.henriquepelanda.api_pedidos.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    return _categoryService.create(request);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<CategoryResponseDTO> findAll(){
    return _categoryService.findAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CategoryResponseDTO findById(@PathVariable UUID id){
    return _categoryService.findById(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CategoryResponseDTO update(@PathVariable UUID id, @RequestBody @Valid CategoryUpdateDTO request){
    return _categoryService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id){
    _categoryService.delete(id);
  }
}
