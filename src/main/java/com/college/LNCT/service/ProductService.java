package com.college.LNCT.service;

import com.college.LNCT.dto.CreateProductRequest;
import com.college.LNCT.dto.ProductDto;
import com.college.LNCT.dto.UpdateProductRequest;
import com.college.LNCT.entity.Product;
import com.college.LNCT.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Get all products
    public List<ProductDto> getAllProducts() {
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get product by ID
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdAndIsActiveTrue(id);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        return convertToDto(product);
    }

    // Create product
    public ProductDto createProduct(CreateProductRequest request, MultipartFile image) {
        try {
            Product product = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .isActive(true)
                    .build();

            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);
                product.setImagePath(imagePath);
                product.setImageUrl("/uploads/products/" + Paths.get(imagePath).getFileName());
            }

            Product savedProduct = productRepository.save(product);
            log.info("Product created: {}", savedProduct.getId());
            return convertToDto(savedProduct);

        } catch (IOException e) {
            log.error("Error creating product", e);
            throw new RuntimeException("Error uploading image: " + e.getMessage());
        }
    }

    // Update product
    public ProductDto updateProduct(Long id, UpdateProductRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        try {
            if (image != null && !image.isEmpty()) {
                // Delete old image if exists
                if (product.getImagePath() != null) {
                    deleteImage(product.getImagePath());
                }
                String imagePath = saveImage(image);
                product.setImagePath(imagePath);
                product.setImageUrl("/uploads/products/" + Paths.get(imagePath).getFileName());
            }

            Product updatedProduct = productRepository.save(product);
            log.info("Product updated: {}", id);
            return convertToDto(updatedProduct);

        } catch (IOException e) {
            log.error("Error updating product", e);
            throw new RuntimeException("Error uploading image: " + e.getMessage());
        }
    }

    // Delete product (soft delete)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(false);
        productRepository.save(product);

        // Delete image file
        if (product.getImagePath() != null) {
            deleteImage(product.getImagePath());
        }

        log.info("Product deleted: {}", id);
    }

    // Helper methods
    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }

    private String saveImage(MultipartFile file) throws IOException {
    Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
    if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
    }

    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    Path filePath = uploadPath.resolve(fileName);

    Files.copy(file.getInputStream(), filePath);
    log.info("Image saved: {}", fileName);
    return fileName;  // ← return ONLY filename, not full path
}

    private void deleteImage(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Image deleted: {}", imagePath);
            }
        } catch (IOException e) {
            log.error("Error deleting image: {}", imagePath, e);
        }
    }
}
