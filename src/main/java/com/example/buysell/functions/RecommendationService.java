package com.example.buysell.functions;

import com.example.buysell.configurations.DataUnavailableException;
import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import com.example.buysell.repositories.ProductRepository;
import com.example.buysell.repositories.UserRepository;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<Product> products = productRepository.findAll();
        List<User> users = userRepository.findAll();
        if (!isDataAvailableForRecommendation()) {
            throw new DataUnavailableException("Insufficient data for recommendation.");
        }

        // Creating a two-dimensional array to represent the user-item interaction matrix
        double[][] userItemInteraction = new double[users.size()][products.size()];

        // Filling the user-item interaction matrix with ratings
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            for (int j = 0; j < products.size(); j++) {
                Product product = products.get(j);
                // Replace this with the actual method to fetch ratings from your data
                // For example, you could use product.getRatingsForUser(user) if such a method exists
                // Here, I'm assuming a random rating between 1 and 5 for demonstration purposes
                double rating = Math.random() * 5 + 1;
                userItemInteraction[i][j] = rating;
            }
        }

        // Step 2: SVD-based Matrix Factorization
        RealMatrix interactionMatrix = new Array2DRowRealMatrix(userItemInteraction);
        SingularValueDecomposition svd = new SingularValueDecomposition(interactionMatrix);

        int k = 2; // Number of latent factors
        RealMatrix U = svd.getU().getSubMatrix(0, users.size() - 1, 0, k - 1);
        RealMatrix sigma = svd.getS().getSubMatrix(0, k - 1, 0, k - 1);
        RealMatrix VT = svd.getVT().getSubMatrix(0, k - 1, 0, products.size() - 1);

        // Find the index of the target user in the users list
        int targetUserIndex = users.indexOf(targetUser);
        if (targetUserIndex == -1) {
            // The target user is not found in the list of users
            return new ArrayList<>();
        }

        // Step 3: Generate Recommendations for the target user
        RealMatrix predictedRatings = U.multiply(sigma).multiply(VT);
        double[] recommendations = predictedRatings.getRow(targetUserIndex);

        // Sort the recommendations in descending order
        int[] sortedIndices = new int[recommendations.length];
        for (int i = 0; i < recommendations.length; i++) {
            sortedIndices[i] = i;
        }
        for (int i = 0; i < recommendations.length - 1; i++) {
            for (int j = i + 1; j < recommendations.length; j++) {
                if (recommendations[sortedIndices[i]] < recommendations[sortedIndices[j]]) {
                    int temp = sortedIndices[i];
                    sortedIndices[i] = sortedIndices[j];
                    sortedIndices[j] = temp;
                }
            }
        }

        // Extract the recommended product IDs from the sorted indices
        List<Long> recommendedProductIds = new ArrayList<>();
        for (int i = 0; i < sortedIndices.length; i++) {
            int itemIndex = sortedIndices[i];
            recommendedProductIds.add(products.get(itemIndex).getId());
        }

        return recommendedProductIds;
    }
    public boolean isDataAvailableForRecommendation() {
        List<Product> products = productRepository.findAll();
        List<User> users = userRepository.findAll();

        return products.size() > 1 && users.size() > 1;
    }

}
