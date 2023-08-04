package com.example.BuySellApplication.functions;

import com.example.BuySellApplication.configurations.DataUnavailableException;
import com.example.BuySellApplication.models.Product;
import com.example.BuySellApplication.models.User;
import com.example.BuySellApplication.repositories.ProductRepository;
import com.example.BuySellApplication.repositories.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;



@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final String userItemInteractionsFilePath = "data/user_item_interactions.json";
    private final String itemAttributesFilePath = "data/item_attributes.json";

    private Map<Long, Map<Long, Double>> userItemInteractions;
    private Map<Long, Map<String, Object>> itemAttributes;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void initializeRecommendationData() {
        init();
    }

    public List<Long> getRecommendedProductIds(User targetUser) {
        List<Product> products = targetUser.getProducts();
        if (userItemInteractions == null || itemAttributes == null) {
            throw new DataUnavailableException("Insufficient data for recommendation.");
        }

        int numUsers = 1; // Number of users will always be 1 since we are fetching only one user
        int numProducts = products.size();

        if (!isDataAvailableForRecommendation(Collections.singletonList(targetUser))) {
            throw new DataUnavailableException("Insufficient data for recommendation.");
        }

        // Fetch user-item interactions from the JSON file
        List<Long> recommendedProductIds = new ArrayList<>();
        double[][] userItemInteraction = new double[numUsers][numProducts];
        for (int i = 0; i < numUsers; i++) {
            Map<Long, Double> userInteractions = userItemInteractions.get(targetUser.getId());
            if (userInteractions == null) {
                continue; // Skip users with no interactions
            }

            for (int j = 0; j < numProducts; j++) {
                Product product = products.get(j);
                double interaction = userInteractions.getOrDefault(product.getId(), 0.0);
                userItemInteraction[i][j] = interaction;
            }
        }

        // Perform Singular Value Decomposition (SVD) using Arrays
        double[][] U = new double[numUsers][numUsers];
        double[][] sigma = new double[numUsers][numProducts];
        double[][] VT = new double[numProducts][numProducts];

        // Perform your own SVD implementation using Arrays here
        // ...

        // Find the index of the target user in the products list
        int targetProductIndex = -1;
        for (int j = 0; j < numProducts; j++) {
            Product product = products.get(j);
            if (product.getId().equals(targetUser.getId())) {
                targetProductIndex = j;
                break;
            }
        }
        if (targetProductIndex == -1 || targetProductIndex >= numProducts) {
            // The target user's products are not found in the list of products, or the index is out of range
            return new ArrayList<>();
        }

        // Generate Recommendations for the target user using Arrays
        double[] predictedRatings = new double[numProducts];

        // Perform your own matrix multiplication and SVD calculations here
        // ...

        // Sort the recommendations in descending order
        Map<Double, Long> sortedRecommendations = new TreeMap<>(Collections.reverseOrder());
        for (int i = 0; i < numProducts; i++) {
            sortedRecommendations.put(predictedRatings[i], products.get(i).getId());
        }

        recommendedProductIds.addAll(sortedRecommendations.values());

        return recommendedProductIds;
    }

    public void init() {
        try {
            // Load the user-item interactions JSON data
            Resource userItemInteractionsResource = new ClassPathResource(userItemInteractionsFilePath);
            try (InputStream userItemInteractionsInputStream = userItemInteractionsResource.getInputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                userItemInteractions = objectMapper.readValue(userItemInteractionsInputStream, new TypeReference<Map<Long, Map<Long, Double>>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                userItemInteractions = new HashMap<>(); // Initialize as empty if loading fails
            }

            // Load the item attributes JSON data
            Resource itemAttributesResource = new ClassPathResource(itemAttributesFilePath);
            try (InputStream itemAttributesInputStream = itemAttributesResource.getInputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                itemAttributes = objectMapper.readValue(itemAttributesInputStream, new TypeReference<Map<Long, Map<String, Object>>>() {});
            } catch (IOException e) {
                e.printStackTrace();
                itemAttributes = new HashMap<>(); // Initialize as empty if loading fails
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDataAvailableForRecommendation(List<User> users) {
        if (users.isEmpty()) {
            return false;
        }

        // Check if there is at least one product
        for (User user : users) {
            if (user.getProducts() != null && !user.getProducts().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void recordUserInteractionAndWriteToJson(Long userId, Long productId) {
        // Record user interaction (similar to the previous method)
        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user != null && product != null) {
            ProductInteraction interaction = new ProductInteraction();
            interaction.setUser(user);
            interaction.setProduct(product);
            interaction.setInteractionValue(1.0); // 1.0 indicates the user visited the page
            interaction.setInteractionTimestamp(new Date()); // Set the interaction timestamp

            // Save the new interaction to the userItemInteractions map
            userItemInteractions.computeIfAbsent(userId, k -> new HashMap<>()).put(productId, 1.0); // 1.0 indicates the user visited the page

            // Write the updated user-item interactions back to the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            File interactionsFile = new File(userItemInteractionsFilePath.substring("classpath:".length()));
            try {
                FileUtils.forceMkdirParent(interactionsFile);
                objectMapper.writeValue(interactionsFile, userItemInteractions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
