package dev.andresbonelli.productcomparisonapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchCriteria {
        private String sortBy;
        private String sortDir;
        private String name;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Double minRating;
}
