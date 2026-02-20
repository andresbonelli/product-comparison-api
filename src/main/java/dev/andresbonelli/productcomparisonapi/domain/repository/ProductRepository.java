package dev.andresbonelli.productcomparisonapi.domain.repository;

import dev.andresbonelli.productcomparisonapi.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Partial case-insensitive search by product name (containing the specified string)
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search for products with a rating greater than or equal to the specified rating
     */
    List<Product> findByRatingGreaterThanEqual(Double rating);

    /**
     * Custom query to find products within a specified price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    @Query(value = """
        SELECT * FROM product p 
        WHERE (:name IS NULL OR p.name LIKE %:name%)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:minRating IS NULL OR p.rating >= :minRating)
        """,
            countQuery = """
        SELECT count(*) FROM product p 
        WHERE (:name IS NULL OR p.name LIKE %:name%)
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:minRating IS NULL OR p.rating >= :minRating)
        """,
            nativeQuery = true)
    Page<Product> findAdvanced(
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable
    );
}
