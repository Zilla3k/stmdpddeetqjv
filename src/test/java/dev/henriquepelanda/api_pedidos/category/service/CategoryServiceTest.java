package dev.henriquepelanda.api_pedidos.category.service;

import dev.henriquepelanda.api_pedidos.category.dto.CategoryRequestDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryResponseDTO;
import dev.henriquepelanda.api_pedidos.category.dto.CategoryUpdateDTO;
import dev.henriquepelanda.api_pedidos.category.entity.Category;
import dev.henriquepelanda.api_pedidos.category.repository.CategoryRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.InvalidRequestException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void createShouldPersistCategory() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        CategoryRequestDTO request = new CategoryRequestDTO("  Electronics  ", "  General electronic products  ");
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);

        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(categoryCaptor.capture())).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", categoryId);
            return saved;
        });

        CategoryResponseDTO response = categoryService.create(request);

        assertEquals(categoryId, response.id());
        assertEquals("Electronics", response.name());
        assertEquals("General electronic products", response.description());
        assertEquals("Electronics", categoryCaptor.getValue().getName());
        assertEquals("General electronic products", categoryCaptor.getValue().getDescription());
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createShouldThrowWhenNameIsBlank() {
        CategoryRequestDTO request = new CategoryRequestDTO("   ", "Description");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> categoryService.create(request));

        assertEquals("Category name invalid!", exception.getMessage());
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void createShouldThrowWhenDescriptionIsBlank() {
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics", "   ");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> categoryService.create(request));

        assertEquals("Description invalid!", exception.getMessage());
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void createShouldThrowWhenCategoryNameAlreadyExists() {
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics", "Description");

        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> categoryService.create(request));

        assertEquals("Category name already exist!", exception.getMessage());
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findAllShouldReturnMappedList() {
        Category first = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(first, "id", UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934"));

        Category second = new Category("Books", "Printed books");
        ReflectionTestUtils.setField(second, "id", UUID.fromString("a3d0f3b5-4f6d-4f16-90f0-8e9f7f4dd111"));

        when(categoryRepository.findAll()).thenReturn(List.of(first, second));

        List<CategoryResponseDTO> response = categoryService.findAll();

        assertEquals(2, response.size());
        assertEquals("Electronics", response.get(0).name());
        assertEquals("Books", response.get(1).name());
        verify(categoryRepository).findAll();
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponseDTO response = categoryService.findById(categoryId);

        assertEquals(categoryId, response.id());
        assertEquals("Electronics", response.name());
        assertEquals("General electronic products", response.description());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void findByIdShouldThrowWhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(categoryId));

        assertEquals("category not found!", exception.getMessage());
    }

    @Test
    void updateShouldPersistPartialChanges() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponseDTO response = categoryService.update(categoryId, new CategoryUpdateDTO("  Devices  ", null));

        assertEquals(categoryId, response.id());
        assertEquals("Devices", response.name());
        assertEquals("General electronic products", response.description());
        assertEquals("Devices", category.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void updateShouldKeepExistingValuesWhenFieldsAreNull() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponseDTO response = categoryService.update(categoryId, new CategoryUpdateDTO(null, null));

        assertEquals("Electronics", response.name());
        assertEquals("General electronic products", response.description());
        verify(categoryRepository, never()).existsByName(any());
    }

    @Test
    void updateShouldThrowWhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.update(categoryId, new CategoryUpdateDTO("Name", "Description")));

        assertEquals("category not found!", exception.getMessage());
    }

    @Test
    void updateShouldThrowWhenNameIsBlank() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> categoryService.update(categoryId, new CategoryUpdateDTO("   ", null)));

        assertEquals("Category name invalid!", exception.getMessage());
    }

    @Test
    void updateShouldThrowWhenDescriptionIsBlank() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> categoryService.update(categoryId, new CategoryUpdateDTO(null, "   ")));

        assertEquals("Description invalid!", exception.getMessage());
    }

    @Test
    void updateShouldThrowWhenUpdatedNameAlreadyExists() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Devices")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> categoryService.update(categoryId, new CategoryUpdateDTO("Devices", null)));

        assertEquals("Category name already exists!", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteShouldRemoveCategory() {
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        Category category = new Category("Electronics", "General electronic products");
        ReflectionTestUtils.setField(category, "id", categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.delete(categoryId);

        verify(categoryRepository).delete((Category) any());
    }

    @Test
    void deleteShouldThrowWhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(categoryId));

        assertEquals("Category not found!", exception.getMessage());
    }
}
