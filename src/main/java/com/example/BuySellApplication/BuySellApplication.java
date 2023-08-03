package com.example.BuySellApplication;

import com.example.BuySellApplication.functions.RecommendationService;
import com.example.BuySellApplication.models.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BuySellApplication {
    @Autowired
    private RecommendationService recommendationService;

    public static void main(String[] args) {
        SpringApplication.run(BuySellApplication.class, args);
    }

    @PostConstruct
    public void init() {
        recommendationService.init();
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            // Create a sample target user for testing
            User targetUser = new User();
            targetUser.setId(1L); // Set the user ID

            // Get recommended product IDs for the target user
            List<Long> recommendedProductIds = recommendationService.getRecommendedProductIds(targetUser);

            // Print the recommended product IDs
            System.out.println("Recommended Product IDs for User " + targetUser.getId() + ":");
            for (Long productId : recommendedProductIds) {
                System.out.println(productId);
            }
        };
    }
}
