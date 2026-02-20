package dev.andresbonelli.productcomparisonapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andresbonelli.productcomparisonapi.api.dto.PagedProducts;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductDTO;
import dev.andresbonelli.productcomparisonapi.domain.exception.ResourceNotFoundException;
import dev.andresbonelli.productcomparisonapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(ProductController.class)
@WithMockUser(roles = "USER")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDTO testProductDTO;
    private List<ProductDTO> productList;
    private Page<ProductDTO> productPage;

    @BeforeEach
    void setUp() {
        testProductDTO = new ProductDTO(
                1L,
                "Test Product",
                "http://test.com/image.jpg",
                "test description",
                new BigDecimal("99.99"),
                4.5,
                "test specs"
                );

        productList = List.of(testProductDTO);
        productPage = new PageImpl<>(productList);
    }

    @Test
    void getAllProducts() throws Exception {
        // Arrange
        var result = new PagedProducts<>(productPage);
        when(productService.getAllProducts(0,10)).thenReturn(result);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // buscar dentro del contenido paginado!
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Test Product"))
                .andExpect(jsonPath("$.products[0].price").value(99.99));
    }

    @Test
    void getProductById() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(testProductDTO);

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProductById_notFound() throws Exception {
        // Arrange
        when(productService.getProductById(999L))
                .thenThrow(ResourceNotFoundException.byId("Producto", 999L));

        // Act & Assert
        mockMvc.perform(get("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void compareProducts() throws Exception {
        // Arrange
        when(productService.getProductsByIds(anyList())).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/products/compare")
                        .param("ids", "1", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductDTO.class)))
                .thenReturn(testProductDTO);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void updateProduct() throws Exception {
        // Arrange
        ProductDTO updatedProductDTO = new ProductDTO(
                1L,
                "Updated Product",
                testProductDTO.imageUrl(),
                testProductDTO.description(),
                testProductDTO.price(),
                testProductDTO.rating(),
                testProductDTO.specifications()
        );
        when(productService.updateProduct(anyLong(), any(ProductDTO.class)))
                .thenReturn(updatedProductDTO);

        // Act & Assert
        mockMvc.perform(put("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}