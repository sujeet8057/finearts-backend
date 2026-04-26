package com.college.LNCT.controller;

import com.college.LNCT.dto.CreateProductRequest;
import com.college.LNCT.dto.ProductDto;
import com.college.LNCT.dto.UpdateProductRequest;
import com.college.LNCT.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    // Get all products (public)
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        log.info("Fetching all products");
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Get product by ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        try {
            ProductDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create product (admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createProduct(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Double price,
            @RequestParam(required = false) MultipartFile image) {

        log.info("Creating product: {}", name);

        try {
            CreateProductRequest request = CreateProductRequest.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .build();

            productService.createProduct(request, image);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully");

        } catch (Exception e) {
            log.error("Error creating product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Update product (admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Double price,
            @RequestParam(required = false) MultipartFile image) {

        log.info("Updating product with ID: {}", id);

        try {
            UpdateProductRequest request = UpdateProductRequest.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .build();

            productService.updateProduct(id, request, image);
            return ResponseEntity.ok("Product updated successfully");

        } catch (Exception e) {
            log.error("Error updating product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete product (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);

        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting product", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

