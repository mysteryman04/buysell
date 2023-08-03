package com.example.BuySellApplication.repositories;

import com.example.BuySellApplication.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitle(String title);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.user WHERE p.id = :productId")
    Product getProductByIdEagerly(Long productId);
}