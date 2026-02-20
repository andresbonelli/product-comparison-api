package dev.andresbonelli.productcomparisonapi.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for standard error responses.
 * Provides a standardized way to represent error responses in the API.
 */
@Schema(description = "Standard error response")
public record ErrorResponse (

    @Schema(description = "HTTP Status code", example = "404")
    int status,

    @Schema(description = "Custom error message",
            example = "Product not found")
    String message,

    @Schema(description = "Aditional details",
            example = "A product with the specified ID does not exist")
    String details,

    @Schema(description = "Error timestamp")
    LocalDateTime timestamp,

    @Schema(description = "Request path originating error",
            example = "/api/products/999")
    String path
) {}
