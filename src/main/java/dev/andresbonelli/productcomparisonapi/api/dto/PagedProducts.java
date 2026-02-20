package dev.andresbonelli.productcomparisonapi.api.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import java.util.List;

@Schema(
        name = "PagedProducts",
        description = "Paginated wrapper for lists of entities"
)
public record PagedProducts<T> (
    @ArraySchema(
            schema = @Schema(description = "List of products", implementation = ProductDTO.class)
    )
    List<T> products,
    @Schema(description = "Pagination metadata")
    PaginationDetails pagination
) {
    public PagedProducts(Page<T> page) {
        this(
                page.getContent(),
                new PaginationDetails(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isLast()
                )
        );
    }

    public record PaginationDetails (
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
    ) {}
}
