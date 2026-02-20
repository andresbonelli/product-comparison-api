package dev.andresbonelli.productcomparisonapi.utils;

import dev.andresbonelli.productcomparisonapi.api.dto.ProductDTO;
import dev.andresbonelli.productcomparisonapi.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods to convert a Product entity to a ProductDTO and vice versa,
 */
@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (null == product) {
            return null;
        }
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getDescription(),
                product.getPrice(),
                product.getRating(),
                product.getSpecifications()
        );
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        return Product.builder()
                .id(dto.id())
                .name(dto.name())
                .imageUrl(dto.imageUrl())
                .description(dto.description())
                .price(dto.price())
                .rating(dto.rating())
                .specifications(dto.specifications())
                .build();
    }

    public List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
