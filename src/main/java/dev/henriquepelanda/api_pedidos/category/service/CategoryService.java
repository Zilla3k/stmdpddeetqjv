package dev.henriquepelanda.api_pedidos.category.service;

import dev.henriquepelanda.api_pedidos.category.dto.CategoryRequestDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryUpdateDTO;
import dev.henriquepelanda.api_pedidos.category.entity.Category;
import dev.henriquepelanda.api_pedidos.category.repository.CategoryRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
  private final CategoryRepository _categoryRepository;

  public CategoryService(CategoryRepository categoryRepository)
  {
    this._categoryRepository = categoryRepository;
  }

  public CategoryResponseDTO create
  (
    CategoryRequestDTO request
  )
  {
    String name = requireText(request.name(), "Category name invalid!");
    String description = requireText(request.description(), "Description invalid!");

    if(_categoryRepository.existsByName(name))
    {
      throw new BusinessException("Category name already exist!");
    }

    Category category = new Category(
            name,
            description
    );

    Category savedCategory = _categoryRepository.save(category);

    return new CategoryResponseDTO(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.getDescription()
    );
  }

  public List<CategoryResponseDTO> findAll(){
    List<Category> categories = _categoryRepository.findAll();

    return categories.stream()
            .map(category -> new CategoryResponseDTO(
                    category.getId(),
                    category.getName(),
                    category.getDescription()
            ))
            .toList();
  }

  public CategoryResponseDTO findById(UUID id){
    Category category = _categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("category not found!"));

    return new CategoryResponseDTO(
            category.getId(),
            category.getName(),
            category.getDescription()
    );
  }

  public CategoryResponseDTO update(UUID id, CategoryUpdateDTO request){
    Category category = _categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("category not found!"));

    String name = request.name() != null ? requireText(request.name(), "Category name invalid!") : category.getName();
    String description = request.description() != null ? requireText(request.description(), "Description invalid!") : category.getDescription();

    if (request.name() != null && !name.equals(category.getName()) && _categoryRepository.existsByName(name)) {
      throw new BusinessException("Category name already exist!");
    }

    category.update(
            name,
            description
    );

    Category updated = _categoryRepository.save(category);

    return new CategoryResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getDescription()
    );
  }

  public void delete(UUID id){
    Category category = _categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Category not found!"));

    _categoryRepository.delete(category);
  }

  private String requireText(String value, String message) {
    if (value == null || value.trim().isBlank()) {
      throw new BusinessException(message);
    }

    return value.trim();
  }
}
