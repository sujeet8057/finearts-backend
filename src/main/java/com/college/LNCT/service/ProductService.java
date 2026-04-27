package com.college.LNCT.service;

import com.college.LNCT.dto.CreateProductRequest;
import com.college.LNCT.dto.ProductDto;
import com.college.LNCT.dto.UpdateProductRequest;
import com.college.LNCT.entity.Product;
import com.college.LNCT.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService; // ✅ Replaced file.upload-dir

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
                // ✅ Upload to Cloudinary, get public HTTPS URL
                String imageUrl = cloudinaryService.uploadImage(image);
                product.setImageUrl(imageUrl);
                product.setImagePath(imageUrl); // ✅ store Cloudinary URL in both fields
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
                // ✅ Delete old image from Cloudinary if exists
                if (product.getImagePath() != null) {
                    cloudinaryService.deleteImage(product.getImagePath());
                }

                // ✅ Upload new image to Cloudinary
                String imageUrl = cloudinaryService.uploadImage(image);
                product.setImageUrl(imageUrl);
                product.setImagePath(imageUrl);
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

        // ✅ Delete image from Cloudinary
        if (product.getImagePath() != null) {
            cloudinaryService.deleteImage(product.getImagePath());
        }

        log.info("Product deleted: {}", id);
    }

    // Helper method
    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }
}