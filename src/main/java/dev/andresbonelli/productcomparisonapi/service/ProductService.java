package dev.andresbonelli.productcomparisonapi.service;

import dev.andresbonelli.productcomparisonapi.api.dto.PagedProducts;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductDTO;
import dev.andresbonelli.productcomparisonapi.api.dto.ProductSearchCriteria;
import dev.andresbonelli.productcomparisonapi.config.CacheConfig;
import dev.andresbonelli.productcomparisonapi.domain.entity.Product;
import dev.andresbonelli.productcomparisonapi.domain.exception.ResourceNotFoundException;
import dev.andresbonelli.productcomparisonapi.domain.repository.ProductRepository;
import dev.andresbonelli.productcomparisonapi.utils.ProductMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing products
 * Performs basic read/write operations and provides search functionality
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @PersistenceContext
    private final EntityManager entityManager;


    /**
     * Get all products paginated
     */
    @Cacheable(value = CacheConfig.PRODUCTS_CACHE, key = "#page + '-' + #size")
    public PagedProducts<ProductDTO> getAllProducts(int page, int size) {
        page-=1;
        Pageable pageable = PageRequest.of(page, size);
        return new PagedProducts<>(productRepository.findAll(pageable).map(productMapper::toDTO));
    }

    /**
     * Advanced Product search engine
     */
    public PagedProducts<ProductDTO> advancedSearch(
            int page, int size, ProductSearchCriteria criteria
    ) {
        page-=1;
        validateQuery(criteria);
        Sort sort =
                criteria.getSortDir().equalsIgnoreCase("desc")
                        ? Sort.by(criteria.getSortBy()).descending()
                        : Sort.by(criteria.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> result = productRepository.findAdvanced(
                criteria.getName(), criteria.getMinPrice(), criteria.getMaxPrice(), criteria.getMinRating(), pageable
        );

        return new PagedProducts<>(result.map(productMapper::toDTO));
    }

    /**
     * Get one product by id
     * @throws ResourceNotFoundException if product is not found
     */
    public ProductDTO getProductById(Long id) {
        log.info("Searching product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Product", id));
        return productMapper.toDTO(product);
    }

    /**
     * Get multiple products by ids
     * @throws IllegalArgumentException in case of an empty list of IDs
     * @throws ResourceNotFoundException if one or more products are not found
     */
    public List<ProductDTO> getProductsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID list should not be empty");
        }

        log.info("Getting multiple products. IDs: {}", ids);
        List<Product> products = productRepository.findAllById(ids);

        // Check all products were found
        if (products.size() != ids.size()) {
            List<Long> foundIds = products.stream()
                    .map(Product::getId)
                    .toList();
            List<Long> missingIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            log.warn("Could not find products with the following IDs: {}", missingIds);
            throw new ResourceNotFoundException(
                    "Could not find all products. Missing IDs: " + missingIds
            );
        }

        return productMapper.toDTOList(products);
    }

    /**
     * Create a new product
     */
    @Transactional
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating new product: {}", productDTO.name());
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    /**
     * Update existing product
     */
    @Transactional
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("Product", id));

        Product updatedProduct = productMapper.toEntity(productDTO);
        updatedProduct.setId(existingProduct.getId());

        Product savedProduct = productRepository.save(updatedProduct);
        return productMapper.toDTO(savedProduct);
    }

    /**
     * Delete a product
     */
    @Transactional
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
    }


    @Transactional
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    public void deleteAll() {
        log.info("Deleting all products...");
        entityManager.createNativeQuery("TRUNCATE TABLE product").executeUpdate();
    }

    @Transactional
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    public int loadSampleProducts() {
        log.info("Loading sample products...");
        Product p1 = Product.builder()
                .name("Dell XPS 15 Laptop")
                .imageUrl("https://example.com/images/dell-xps-15.jpg")
                .description("High-performance premium laptop powered by an 11th Gen Intel Core i7 processor, " +
                        "ideal for creative professionals and developers. Features a 4K OLED touchscreen display.")
                .price(new BigDecimal("1299.99"))
                .rating(4.5)
                .specifications("Processor: Intel Core i7-11800H, RAM: 16GB DDR4, " +
                        "Storage: 512GB NVMe SSD, Display: 15.6\" 4K OLED (3840x2160), " +
                        "Graphics Card: NVIDIA GeForce RTX 3050 Ti 4GB, " +
                        "Weight: 2.0 kg, Battery: up to 8 hours")
                .build();

        Product p2 = Product.builder()
                .name("Samsung Galaxy S23 Ultra")
                .imageUrl("https://example.com/images/samsung-s23-ultra.jpg")
                .description("Samsung's flagship smartphone featuring a 200MP camera, " +
                        "integrated S Pen, and Dynamic AMOLED 2X display. Power and elegance in a single device.")
                .price(new BigDecimal("1199.99"))
                .rating(4.8)
                .specifications("Processor: Snapdragon 8 Gen 2, RAM: 12GB, " +
                        "Storage: 256GB, Display: 6.8\" Dynamic AMOLED 2X (3088x1440) 120Hz, " +
                        "Main Camera: 200MP + 12MP Ultra Wide + 10MP Telephoto (3x) + 10MP Telephoto (10x), " +
                        "Battery: 5000mAh with 45W fast charging, S Pen included")
                .build();

        Product p3 = Product.builder()
                .name("Sony WH-1000XM5")
                .imageUrl("https://example.com/images/sony-wh1000xm5.jpg")
                .description("Wireless headphones with industry-leading noise cancellation. " +
                        "Premium sound with LDAC technology and up to 30 hours of battery life.")
                .price(new BigDecimal("399.99"))
                .rating(4.7)
                .specifications("Type: Closed-back over-ear, Connectivity: Bluetooth 5.2, LDAC, " +
                        "Noise Cancellation: Next-generation Active Noise Cancelling (ANC), " +
                        "Battery: up to 30 hours with ANC on, 40 hours without ANC, " +
                        "Fast Charging: 3 minutes = 3 hours of playback, " +
                        "Drivers: 30mm, Weight: 250g, Multifunction touch controls")
                .build();

        List<Product> defaults = List.of(p1, p2, p3);
        return productRepository.saveAll(defaults).size();
    }


    private void validateQuery(ProductSearchCriteria criteria) {
        Double minRating = criteria.getMinRating();
        BigDecimal minPrice = criteria.getMinPrice();
        BigDecimal maxPrice = criteria.getMaxPrice();
        String sortBy = criteria.getSortBy();
        String sortDir = criteria.getSortDir();
        if (null != minPrice && minPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Minimum price should be greater than zero");
        }
        if (null != maxPrice && null != minPrice && maxPrice.compareTo(minPrice) < 0) {
            throw new IllegalArgumentException("Maximum price should be greater than minimum price");
        }
        if (null != minRating && (minRating < 0 || minRating > 5)) {
            throw new IllegalArgumentException("Rating should be between 0 and 5");
        }
        if (null != sortBy && !List.of("id", "name", "price", "rating").contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sorting criteria");
        }
        if (null != sortDir && !List.of("asc", "desc").contains(sortDir)) {
            throw new IllegalArgumentException("Invalid sorting direction. Use 'asc' or 'desc'");
        }
    }
}
