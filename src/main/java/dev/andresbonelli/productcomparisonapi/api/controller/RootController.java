package dev.andresbonelli.productcomparisonapi.api.controller;

import dev.andresbonelli.productcomparisonapi.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/api/root")
@AllArgsConstructor
@Slf4j
public class RootController {

    private final ProductService productService;


    @PostMapping("/reset-db")
    public ResponseEntity<?> resetDatabase() {
        productService.deleteAll();
        int loadCount = productService.loadSampleProducts();
        log.info("Database reset complete. Inserted {} sample products.",loadCount);
        return ResponseEntity.ok().build();

    }
}
