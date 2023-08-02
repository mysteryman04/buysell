package com.example.buysell.functions;

import com.example.buysell.configurations.DataUnavailableException;
import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.repositories.ProductRepository;
import com.example.buysell.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

@Service
public class RecommendationService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecommendationService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Long> getRecommendedProductIds(User targetUser) {
        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();

        if (!isDataAvailableForRecommendation(users, products)) {
            throw new DataUnavailableException("Insufficient data for recommendation.");
        }

        int numUsers = users.size();
        int numProducts = products.size();

        // Fetch user-item interactions from the database
        Map<Long, Map<Long, Double>> userItemInteractions = getUserItemInteractions(users, products);

        // Create the user-item interaction matrix
        double[][] userItemInteraction = new double[numUsers][numProducts];
        for (int i = 0; i < numUsers; i++) {
            User user = users.get(i);
            Map<Long, Double> userInteractions = userItemInteractions.get(user.getId());
            if (userInteractions == null) {
                continue; // Skip users with no interactions
            }

            for (int j = 0; j < numProducts; j++) {
                Product product = products.get(j);
                double interaction = userInteractions.getOrDefault(product.getId(), 0.0);
                userItemInteraction[i][j] = interaction;
            }
        }

        // Perform Singular Value Decomposition (SVD)
        RealMatrix interactionMatrix = new Array2DRowRealMatrix(userItemInteraction);
        SingularValueDecomposition svd = new SingularValueDecomposition(interactionMatrix);

        int numLatentFactors = 2; // Number of latent factors
        RealMatrix U = svd.getU().getSubMatrix(0, numUsers - 1, 0, numLatentFactors - 1);
        RealMatrix sigma = svd.getS().getSubMatrix(0, numLatentFactors - 1, 0, numLatentFactors - 1);
        RealMatrix VT = svd.getVT().getSubMatrix(0, numLatentFactors - 1, 0, numProducts - 1);

        // Find the index of the target user in the users list
        int targetUserIndex = users.indexOf(targetUser);
        if (targetUserIndex == -1) {
            // The target user is not found in the list of users
            return new ArrayList<>();
        }

        // Generate Recommendations for the target user
        RealMatrix predictedRatings = U.multiply(sigma).multiply(VT);
        double[] recommendations = predictedRatings.getRow(targetUserIndex);

        // Sort the recommendations in descending order
        List<Long> recommendedProductIds = new ArrayList<>();
        for (int i = 0; i < recommendations.length; i++) {
            recommendedProductIds.add(products.get(i).getId());
        }

        return recommendedProductIds;
    }

    private boolean isDataAvailableForRecommendation(List<User> users, List<Product> products) {
        return users.size() > 1 && products.size() > 1;
    }

    // Fetch user-item interactions from the database
    private Map<Long, Map<Long, Double>> getUserItemInteractions(List<User> users, List<Product> products) {
        Map<Long, Map<Long, Double>> userItemInteractions = new HashMap<>();

        for (User user : users) {
            Map<Long, Double> userInteractions = new HashMap<>();
            for (Product product : products) {
                double interactionValue = getProductInteractionValue(user, product);
                userInteractions.put(product.getId(), interactionValue);
            }
            userItemInteractions.put(user.getId(), userInteractions);
        }

        return userItemInteractions;
    }

    // Method to fetch the interaction value for a user and product from the database (you need to implement this)
    private double getProductInteractionValue(User user, Product product) {
        // Implement the logic to fetch the interaction value for the user and product from the database
        // This could be based on the number of views, duration, likes, etc.
        // For simplicity, I'll return a random value between 0 and 1 in this example
        return Math.random();
    }
}
