package com.example.BuySellApplication.functions;

import com.example.BuySellApplication.configurations.DataUnavailableException;
import com.example.BuySellApplication.models.Product;
import com.example.BuySellApplication.models.User;
import com.example.BuySellApplication.repositories.ProductRepository;
import com.example.BuySellApplication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final String userItemInteractionsFilePath = "data/user_item_interactions.json";
    private final String itemAttributesFilePath = "item_attributes.json";

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductInteractionRepository productInteractionRepository;
    private final ResourceLoader resourceLoader;


    private Map<Long, Map<Long, Double>> userItemInteractions;
    private Map<Long, Map<String, Object>> itemAttributes;

    public void init() {
        try {
            // Load the resource using the ResourceLoader
            Resource resource = resourceLoader.getResource("classpath:data/user_item_interactions.json");

            // Use the resource's InputStream to read the file
            try (InputStream inputStream = resource.getInputStream()) {
                // Your logic to process the JSON data from the InputStream
                ObjectMapper objectMapper = new ObjectMapper();
                List<UserItemInteraction> userItemInteractions = objectMapper.readValue(inputStream, new TypeReference<List<UserItemInteraction>>() {});
                // Do something with userItemInteractions
                for (UserItemInteraction interaction : userItemInteractions) {
                    // Process each user-item interaction
                    System.out.println(interaction.getUserId() + " - " + interaction.getItemId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Map<String, Object>> getUserItemInteractionsFromJSON() {
        try {
            Resource resource = new ClassPathResource("data/user_item_interactions.json");
            InputStream inputStream = resource.getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<>() {};
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return new ArrayList<>();
        }
    }
    private Map<Long, Map<String, Object>> getItemAttributesFromJSON() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(Objects.requireNonNull(classLoader.getResource(itemAttributesFilePath)).getFile());
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<Map<Long, Map<String, Object>>> typeReference = new TypeReference<>() {};
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            // Handle the exception appropriately (e.g., log the error, return a default value, etc.)
            e.printStackTrace();
            return null;
        }
    }

    public List<Long> getRecommendedProductIds(User targetUser) {
        if (userItemInteractions == null || itemAttributes == null) {
            throw new DataUnavailableException("Insufficient data for recommendation.");
        }

        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();

        if (!isDataAvailableForRecommendation(users, products)) {
            throw new DataUnavailableException("Insufficient data for recommendation.");
        }

        int numUsers = users.size();
        int numProducts = products.size();

        // Fetch user-item interactions from the database
        Map<Long, Map<Long, Double>> userItemInteractions = getUserItemInteractions(users, products);
        List<Long> recommendedProductIds = new ArrayList<>();
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

        for (int i = 0; i < recommendations.length; i++) {
            recommendedProductIds.add(products.get(i).getId());
        }

        return recommendedProductIds;
    }

    // Other methods and helper functions (not shown here)

    private boolean isDataAvailableForRecommendation(List<User> users, List<Product> products) {
        return users.size() > 1 && products.size() > 1;
    }

    // ...

    private Map<Long, Map<Long, Double>> getUserItemInteractions(List<User> users, List<Product> products) {
        Map<Long, Map<Long, Double>> userItemInteractions = new HashMap<>();
        for (User user : users) {
            Map<Long, Double> itemInteractions = new HashMap<>();
            for (Product product : products) {
                double interactionValue = getProductInteractionValue(user, product);
                itemInteractions.put(product.getId(), interactionValue);
            }
            userItemInteractions.put(user.getId(), itemInteractions);
        }
        return userItemInteractions;
    }

    private double getProductInteractionValue(User user, Product product) {
        // Your implementation to calculate interaction value based on JSON data
        // For example, you can use the itemAttributes map to fetch item data
        Map<String, Object> itemData = itemAttributes.get(product.getId());
        // Calculate the interaction value based on item data and user data
        // For simplicity, let's assume a random interaction value here (0.0 to 5.0)
        return Math.random() * 5.0;
    }

// ...

    private Map<Long, Map<Long, Double>> getUserItemInteractionsFromJSON(String jsonFileName) throws IOException {
        File file = new File(jsonFileName);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<Map<Long, Map<Long, Double>>>() {});
    }

    private Map<Long, Map<String, Object>> getItemAttributesFromJSON(String jsonFileName) throws IOException {
        File file = new File(jsonFileName);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file, new TypeReference<Map<Long, Map<String, Object>>>() {});
    }

    private static class UserItemInteraction {
        private String userId;
        private String itemId;

        // Getters and setters (or use Lombok annotations to generate them)

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }
}
