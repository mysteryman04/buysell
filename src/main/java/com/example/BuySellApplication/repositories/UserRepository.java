package com.example.BuySellApplication.repositories;

import com.example.BuySellApplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.products WHERE u.id = :userId")
    User findUserWithProductsById(Long userId);
}