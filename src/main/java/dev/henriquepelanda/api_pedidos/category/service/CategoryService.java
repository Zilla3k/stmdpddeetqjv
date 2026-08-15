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
    if(_categoryRepository.existsByName(request.name()))
    {
      throw new BusinessException("Category name already exist!");
    }

    if(request.name() == null){
      throw new BusinessException("Category name invalid!");
    }

    if (request.description() == null) {
      throw new BusinessException("Description invalid!");
    }

    Category category = new Category(
            request.name(),
            request.description()
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

    category.update(
            request.name(),
            request.description()
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
}
