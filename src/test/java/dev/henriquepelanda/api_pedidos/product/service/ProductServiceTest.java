package dev.henriquepelanda.api_pedidos.product.service;

import dev.henriquepelanda.api_pedidos.category.entity.Category;
import dev.henriquepelanda.api_pedidos.category.repository.CategoryRepository;
import dev.henriquepelanda.api_pedidos.common.exception.BusinessException;
import dev.henriquepelanda.api_pedidos.common.exception.InvalidRequestException;
import dev.henriquepelanda.api_pedidos.common.exception.ResourceNotFoundException;
import dev.henriquepelanda.api_pedidos.product.dto.ProductFilterDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductRequestDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductResponseDTO;
import dev.henriquepelanda.api_pedidos.product.dto.ProductUpdateDTO;
import dev.henriquepelanda.api_pedidos.product.entity.Product;
import dev.henriquepelanda.api_pedidos.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryRepository);
    }

    @Test
    void createShouldPersistProduct() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        ProductRequestDTO request = new ProductRequestDTO(
                "  T-Shirt  ",
                "  Basic black t-shirt  ",
                new BigDecimal("79.90"),
                categoryId,
                10
        );
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        when(productRepository.existsByName("T-Shirt")).thenReturn(false);
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productRepository.save(productCaptor.capture())).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", productId);
            return saved;
        });

        ProductResponseDTO response = productService.create(request);

        assertEquals(productId, response.id());
        assertEquals("T-Shirt", response.name());
        assertEquals("Basic black t-shirt", response.description());
        assertEquals(new BigDecimal("79.90"), response.price());
        assertEquals(categoryId, response.categoryId());
        assertEquals(10, response.stockQuantity());
        verify(productRepository).existsByName("T-Shirt");
        verify(categoryRepository).existsById(categoryId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createShouldThrowWhenNameIsBlank() {
        ProductRequestDTO request = new ProductRequestDTO(
                "   ",
                "Description",
                new BigDecimal("79.90"),
                UUID.randomUUID(),
                10
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> productService.create(request));

        assertEquals("Product name cannot be blank!", exception.getMessage());
        verifyNoInteractions(productRepository, categoryRepository);
    }

    @Test
    void createShouldThrowWhenDescriptionIsBlank() {
        ProductRequestDTO request = new ProductRequestDTO(
                "T-Shirt",
                "   ",
                new BigDecimal("79.90"),
                UUID.randomUUID(),
                10
        );

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> productService.create(request));

        assertEquals("Product description cannot be blank!", exception.getMessage());
        verifyNoInteractions(productRepository, categoryRepository);
    }

    @Test
    void createShouldThrowWhenProductNameAlreadyExists() {
        UUID categoryId = UUID.randomUUID();
        ProductRequestDTO request = new ProductRequestDTO(
                "T-Shirt",
                "Description",
                new BigDecimal("79.90"),
                categoryId,
                10
        );

        when(productRepository.existsByName("T-Shirt")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.create(request));

        assertEquals("Product name already exists!", exception.getMessage());
        verify(productRepository).existsByName("T-Shirt");
        verifyNoInteractions(categoryRepository);
        verify(productRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();
        ProductRequestDTO request = new ProductRequestDTO(
                "T-Shirt",
                "Description",
                new BigDecimal("79.90"),
                categoryId,
                10
        );

        when(productRepository.existsByName("T-Shirt")).thenReturn(false);
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.create(request));

        assertEquals("Category not found!", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenPriceIsNotPositive() {
        UUID categoryId = UUID.randomUUID();
        ProductRequestDTO request = new ProductRequestDTO(
                "T-Shirt",
                "Description",
                new BigDecimal("0"),
                categoryId,
                10
        );

        when(productRepository.existsByName("T-Shirt")).thenReturn(false);
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.create(request));

        assertEquals("Price must be greater than zero!", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenStockIsNegative() {
        UUID categoryId = UUID.randomUUID();
        ProductRequestDTO request = new ProductRequestDTO(
                "T-Shirt",
                "Description",
                new BigDecimal("79.90"),
                categoryId,
                -1
        );

        when(productRepository.existsByName("T-Shirt")).thenReturn(false);
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.create(request));

        assertEquals("Stock must be greater than or equal to zero!", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void findAllShouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product first = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(first, "id", UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761"));

        Product second = new Product("Pants", "Blue jeans", new BigDecimal("129.90"), categoryId, 5);
        ReflectionTestUtils.setField(second, "id", UUID.fromString("7c61e1a2-1a8b-4c0c-8db1-2f6cb0e2d222"));

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(first, second), pageable, 2));

        Page<ProductResponseDTO> response = productService.findAll(new ProductFilterDTO("shirt", categoryId), pageable);

        assertEquals(2, response.getTotalElements());
        assertEquals("T-Shirt", response.getContent().get(0).name());
        assertEquals("Pants", response.getContent().get(1).name());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findByIdShouldReturnMappedResponse() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.findById(productId);

        assertEquals(productId, response.id());
        assertEquals("T-Shirt", response.name());
        assertEquals("Basic black t-shirt", response.description());
        verify(productRepository).findById(productId);
    }

    @Test
    void findByIdShouldThrowWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.findById(productId));

        assertEquals("Product not found!", exception.getMessage());
    }

    @Test
    void updateShouldPersistPartialChanges() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        UUID newCategoryId = UUID.fromString("a3d0f3b5-4f6d-4f16-90f0-8e9f7f4dd111");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("Jacket")).thenReturn(false);
        when(categoryRepository.existsById(newCategoryId)).thenReturn(true);
        when(productRepository.save(product)).thenReturn(product);

        ProductResponseDTO response = productService.update(productId, new ProductUpdateDTO("  Jacket  ", null, new BigDecimal("99.90"), newCategoryId, 12));

        assertEquals(productId, response.id());
        assertEquals("Jacket", response.name());
        assertEquals(new BigDecimal("99.90"), response.price());
        assertEquals(newCategoryId, response.categoryId());
        assertEquals(12, response.stockQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void updateShouldKeepExistingValuesWhenFieldsAreNull() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        ProductResponseDTO response = productService.update(productId, new ProductUpdateDTO(null, null, null, null, null));

        assertEquals("T-Shirt", response.name());
        assertEquals("Basic black t-shirt", response.description());
        assertEquals(new BigDecimal("79.90"), response.price());
        assertEquals(categoryId, response.categoryId());
        assertEquals(10, response.stockQuantity());
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void updateShouldThrowWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, new ProductUpdateDTO("Name", null, null, null, null)));

        assertEquals("Product not found!", exception.getMessage());
    }

    @Test
    void updateShouldThrowWhenUpdatedNameAlreadyExists() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("Jacket")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.update(productId, new ProductUpdateDTO("Jacket", null, null, null, null)));

        assertEquals("Product name already exists!", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenPriceIsNotPositive() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.update(productId, new ProductUpdateDTO(null, null, new BigDecimal("0"), null, null)));

        assertEquals("Price must be greater than zero!", exception.getMessage());
    }

    @Test
    void updateShouldThrowWhenStockIsNegative() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.update(productId, new ProductUpdateDTO(null, null, null, null, -1)));

        assertEquals("Stock must be greater than or equal to zero!", exception.getMessage());
    }

    @Test
    void updateShouldThrowWhenCategoryDoesNotExist() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");
        UUID newCategoryId = UUID.fromString("a3d0f3b5-4f6d-4f16-90f0-8e9f7f4dd111");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.existsById(newCategoryId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, new ProductUpdateDTO(null, null, null, newCategoryId, null)));

        assertEquals("Category not found!", exception.getMessage());
    }

    @Test
    void deleteShouldRemoveProduct() {
        UUID productId = UUID.fromString("116527c3-f6ea-4ae8-a6a5-2baef7bb3761");
        UUID categoryId = UUID.fromString("61661601-7f45-499c-bc9c-59918bd68934");

        Product product = new Product("T-Shirt", "Basic black t-shirt", new BigDecimal("79.90"), categoryId, 10);
        ReflectionTestUtils.setField(product, "id", productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.delete(productId);

        verify(productRepository).delete((Product) any());
    }

    @Test
    void deleteShouldThrowWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.delete(productId));

        assertEquals("Product not found!", exception.getMessage());
    }
}
