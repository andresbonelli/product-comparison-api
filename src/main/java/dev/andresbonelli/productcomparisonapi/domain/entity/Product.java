package dev.andresbonelli.productcomparisonapi.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is mandatory")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Image URL is mandatory")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Product price is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price should be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Product rating is mandatory")
    @DecimalMin(value = "0.0", message = "Minimum rating is 0")
    @DecimalMax(value = "5.0", message = "Maximum rating is 5")
    @Column(nullable = false)
    private Double rating;

    @NotBlank(message = "Product specifications is mandatory")
    @Column(nullable = false, length = 2000)
    private String specifications;
}
