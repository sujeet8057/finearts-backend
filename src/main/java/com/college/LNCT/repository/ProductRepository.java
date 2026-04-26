package com.college.LNCT.repository;

import com.college.LNCT.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    Product findByIdAndIsActiveTrue(Long id);
}
