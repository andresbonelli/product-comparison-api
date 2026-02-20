package dev.andresbonelli.productcomparisonapi.api.controller;

import dev.andresbonelli.productcomparisonapi.api.dto.ErrorResponse;
import dev.andresbonelli.productcomparisonapi.api.dto.PagedProducts;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductDTO;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductSearchCriteria;
import dev.andresbonelli.productcomparisonapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for handling product-related operations.
 * Provides endpoints for retrieving, creating, and searching products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API that supplies product operations")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtain a single product by ID",
            description = "Returns the specific details for a desired product."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found/ non existent",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Returns the complete list of products, paginated"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paged product list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedProducts.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "One or more products not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<PagedProducts<ProductDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var result = productService.getAllProducts(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/advancedSearch")
    @Operation(
            summary = "Advanced product search operation",
            description = "Returns a list of products filtered by price/rating, paginated, and sorted"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paged product list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedProducts.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "One or more products not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<PagedProducts<ProductDTO>> getAllAdvanced(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating
    ) {
        var result = productService.advancedSearch(
                page, size,
                new ProductSearchCriteria(sortBy,sortDir, name, minPrice, maxPrice, minRating)
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/compare")
    @Operation(
            summary = "Get multiple products",
            description = "Returns a list of 2 or more products to compare. " +
                    "Independent IDs are passed as a list of parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All requested products retrieved succesfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID list invalid or empty",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ProductDTO>> getSome(
            @Parameter(description = "The list of Product IDs to compare", example = "1,2,3")
            @RequestParam List<Long> ids) {
        var result = productService.getProductsByIds(ids);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(
            summary = "Create new product",
            description = "Persists a new product in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO productDTO) {
        var result = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping
    @Operation(
            summary = "Update existing product",
            description = "Updates existing product information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ProductDTO> updateProduct(
            @Valid @RequestBody ProductDTO productDTO) {
        var result = productService.updateProduct(productDTO.id(), productDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a product",
            description = "Permanently removes a product entry from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}





