package dev.henriquepelanda.api_pedidos.category.service;

import dev.henriquepelanda.api_pedidos.category.dto.CategoryRequestDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.category.entity.Category;
import dev.henriquepelanda.api_pedidos.category.repository.CategoryRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  private final CategoryRepository _categoryRepository;

  public CategoryService(CategoryRepository categoryRepository)
  {
    this._categoryRepository = categoryRepository;
  }

  public CategoryResponseDTO createCategory
  (
    CategoryRequestDTO request
  )
  {
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
}
