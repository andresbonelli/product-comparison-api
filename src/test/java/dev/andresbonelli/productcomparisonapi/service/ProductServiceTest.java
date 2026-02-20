package dev.andresbonelli.productcomparisonapi.service;

import static org.junit.jupiter.api.Assertions.*;

import dev.andresbonelli.productcomparisonapi.api.dto.ProductDTO;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductSearchCriteria;
import dev.andresbonelli.productcomparisonapi.domain.entity.Product;
import dev.andresbonelli.productcomparisonapi.domain.exception.ResourceNotFoundException;
import dev.andresbonelli.productcomparisonapi.domain.repository.ProductRepository;
import dev.andresbonelli.productcomparisonapi.utils.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDTO testProductDTO;
    private ProductSearchCriteria searchCriteria;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .imageUrl("http://test.com/image.jpg")
                .description("Producto de prueba")
                .price(new BigDecimal("99.99"))
                .rating(4.5)
                .specifications("Specs de prueba")
                .build();

        testProductDTO = new ProductDTO(
                1L,
                "Test Product",
                "http://test.com/image.jpg",
                "Producto de prueba",
                new BigDecimal("99.99"),
                4.5,
                "Specs de prueba"
        );

        searchCriteria = new ProductSearchCriteria(
                "id",
                "asc",
                null,
                null,
                null,
                null);
    }

    @Test
    void getAllProducts() {
        // Arrange
        List<Product> products = Collections.singletonList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);

        // Act
        var resultPage = productService.getAllProducts(1,10);
        List<ProductDTO> result = resultPage.products();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProductDTO.name(), result.getFirst().name());
    }

    @Test
    void getProductById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

        // Act
        ProductDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testProductDTO.id(), result.id());
        assertEquals(testProductDTO.name(), result.name());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_notFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void getProductsByIds() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Product> products = Arrays.asList(testProduct, testProduct);
        List<ProductDTO> expectedDTOs = Arrays.asList(testProductDTO, testProductDTO);

        when(productRepository.findAllById(ids)).thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(expectedDTOs);

        // Act
        List<ProductDTO> result = productService.getProductsByIds(ids);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAllById(ids);
    }

    @Test
    void getProductsByIds_emptyList() {
        // Arrange
        List<Long> emptyIds = List.of();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsByIds(emptyIds));
        verify(productRepository, never()).findAllById(any());
    }

    @Test
    void getProductsByIds_productMissing() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        when(productRepository.findAllById(ids)).thenReturn(Collections.singletonList(testProduct));
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductsByIds(ids));
        verify(productRepository, times(1)).findAllById(ids);
    }

    @Test
    void searchByName() {
        // Arrange
        String searchTerm = "Test";
        searchCriteria.setName(searchTerm);
        Page<Product> products = new PageImpl<>(Collections.singletonList(testProduct));

        when(productRepository.findAdvanced(eq(searchTerm), any(), any(), any(), any(Pageable.class)))
                .thenReturn(products);
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

        // Act
        List<ProductDTO> result = productService.advancedSearch(1, 10, searchCriteria)
                .products();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchByMinRating() {
        // Arrange
        Double minRating = 4.0;
        searchCriteria.setMinRating(minRating);
        Page<Product> products = new PageImpl<>(Collections.singletonList(testProduct));

        when(productRepository.findAdvanced(any(), any(), any(), eq(minRating), any(Pageable.class)))
                .thenReturn(products);
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

        // Act
        List<ProductDTO> result = productService.advancedSearch(1,10,searchCriteria)
                .products();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchByMinRating_invalidRating() {
        // Act & Assert
        searchCriteria.setMinRating(-1.0);
        assertThrows(IllegalArgumentException.class, () -> productService.advancedSearch(1,10,searchCriteria));
        searchCriteria.setMinRating(6.0);
        assertThrows(IllegalArgumentException.class, () -> productService.advancedSearch(1, 10, searchCriteria));
        verify(productRepository, never()).findByRatingGreaterThanEqual(any());
    }

    @Test
    void searchByPriceRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        searchCriteria.setMinPrice(minPrice);
        searchCriteria.setMaxPrice(maxPrice);
        Page<Product> products = new PageImpl<>(Collections.singletonList(testProduct));

        when(productRepository.findAdvanced(any(), eq(minPrice), eq(maxPrice), any(),any(Pageable.class)))
                .thenReturn(products);
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

        // Act
        List<ProductDTO> result = productService.advancedSearch(1,10,searchCriteria)
                .products();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void createProduct_ok() {
        // Arrange
        when(productMapper.toEntity(testProductDTO)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

        // Act
        ProductDTO result = productService.createProduct(testProductDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testProductDTO.name(), result.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_ok() {
        // Arrange
        ProductDTO updatedProductDTO = new ProductDTO(
                1L,
                "Updated Product",
                testProduct.getImageUrl(),
                testProduct.getDescription(),
                testProduct.getPrice(),
                testProduct.getRating(),
                testProduct.getSpecifications()
        );
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .imageUrl(testProduct.getImageUrl())
                .description(testProduct.getDescription())
                .price(testProduct.getPrice())
                .rating(testProduct.getRating())
                .specifications(testProduct.getSpecifications())
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toEntity(updatedProductDTO)).thenReturn(updatedProduct);
        doAnswer(a->{
            Product product = a.getArgument(0);
            assertEquals(updatedProduct.getName(), product.getName());
            return updatedProduct;
        }).when(productRepository).save(any(Product.class));
        when(productMapper.toDTO(updatedProduct)).thenReturn(updatedProductDTO);

        // Act
        ProductDTO result = productService.updateProduct(1L, updatedProductDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProductDTO.name(), result.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_notFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(999L, testProductDTO));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void deleteProduct_ok() {
        productService.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }
}