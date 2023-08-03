package com.example.BuySellApplication.functions;

import com.example.BuySellApplication.models.Product;
import com.example.BuySellApplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInteractionRepository extends JpaRepository<ProductInteraction, Long> {
    List<ProductInteraction> findByUser(User user);

    List<ProductInteraction> findByProduct(Product product);

    List<ProductInteraction> findByUserAndProduct(User user, Product product);
}
