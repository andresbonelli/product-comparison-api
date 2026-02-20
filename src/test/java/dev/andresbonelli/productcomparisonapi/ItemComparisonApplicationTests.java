package dev.andresbonelli.productcomparisonapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration Tests
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser("USER")
class ProductComparisonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void completeFlow() throws Exception {
        // 1. Create new product
        ProductDTO newProduct = new ProductDTO(
                null,
                "Test Integration Product",
                "http://test.com/integration.jpg",
                "Test description",
                new BigDecimal("599.99"),
                4.3,
                "Test specs"
                );

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Integration Product"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        ProductDTO created = objectMapper.readValue(responseBody, ProductDTO.class);
        Long productId = created.id();

        // 2. Search by ID
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Test Integration Product"));

        // 3. List all products (should refresh cache and include new one)
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(greaterThanOrEqualTo(4))));

        // 4. Advanced search
        mockMvc.perform(get("/api/products/advancedSearch")
                        .param("minRating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(greaterThanOrEqualTo(1))));

        // 5. Compare products
        mockMvc.perform(get("/api/products/compare")
                        .param("ids", productId.toString(), "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    void initData_shouldLoadProducts() throws Exception {
        // Check 3 initial products are loaded
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.products[*].name",
                        hasItems(
                                containsString("Dell XPS"),
                                containsString("Samsung"),
                                containsString("Sony")
                        )));
    }

    @Test
    void checkSwaggerUI() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void checkApiDocs() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists());
    }

    @Test
    void errorHandling_nonexistingProduct() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/products/99999"));
    }

    @Test
    void errorHandling_invalidRating() throws Exception {
        mockMvc.perform(get("/api/products/advancedSearch")
                        .param("minRating", "10.0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void createProduct_validationError() throws Exception {
        ProductDTO invalidProduct = new ProductDTO(
                null,
                "", 
                "",
                "",
                new BigDecimal("-10.00"), 
                6.0, 
                ""
                );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation error"));
    }

    @Test
    void productComparison() throws Exception {
        mockMvc.perform(get("/api/products/compare")
                        .param("ids", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[2].id").exists());
    }

    @Test
    void productComparison_nonExisting() throws Exception {
        mockMvc.perform(get("/api/products/compare")
                        .param("ids", "1", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.details", containsString("99999")));
    }

}
