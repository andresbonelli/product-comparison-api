package dev.andresbonelli.productcomparisonapi.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Represents a product for comparison")
public record ProductDTO (

    @Schema(description = "Product unique ID (numerical)", example = "1")
    Long id,

    @Schema(description = "Product name", example = "Laptop Dell XPS 15")
    String name,

    @Schema(description = "Product image URL",
            example = "https://example.com/images/laptop.jpg")
    String imageUrl,

    @Schema(description = "Detailed description",
            example = "High-performance premium powered by an 11th Gen Intel Core i7 processor")
    String description,

    @Schema(description = "Product price in US dollars", example = "1299.99")
    BigDecimal price,

    @Schema(description = "Average product rating (0-5)", example = "4.5")
    Double rating,

    @Schema(description = "Technical specifications",
            example = "RAM: 16GB, SSD: 512GB, Screen: 15.6\" 4K")
    String specifications
) {}