package com.example.BuySellApplication.functions;

import com.example.BuySellApplication.configurations.DataUnavailableException;
import com.example.BuySellApplication.models.Product;
import com.example.BuySellApplication.models.User;
import com.example.BuySellApplication.services.ProductService;
import com.example.BuySellApplication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ProductService productService;
    private final UserService userService; // Inject the UserService here

    @GetMapping("/recommendations")
    public String showRecommendations(Model model, Principal principal) {
        User currentUser = userService.getUserByPrincipal(principal);
        if (currentUser == null) {
            // Handle the case when the user is not logged in
            return "redirect:/login"; // Or return an appropriate view for non-logged-in users
        }

        List<Long> recommendedProductIds = recommendationService.getRecommendedProductIds(currentUser);

        // Fetch product details for recommended product IDs
        List<Product> recommendedProducts = productService.getProductsByIds(recommendedProductIds);

        model.addAttribute("recommendedProducts", recommendedProducts);
        model.addAttribute("user", currentUser);

        return "recommendations";
    }




    @GetMapping("/product-info/{productId}")
    public String getProductInfo(@PathVariable Long productId, Principal principal) {
        // Your logic to fetch the product details and render the product-info page

        // Get the current user (if logged in)
        User currentUser = userService.getUserByPrincipal(principal);
        if (currentUser != null) {
            // Record the user interaction in the recommendation service and update JSON file
            recommendationService.recordUserInteractionAndWriteToJson(currentUser.getId(), productId);
        }

        // Return the view for the product-info page
        return "product-info";
    }

    @ExceptionHandler(DataUnavailableException.class)
    public ResponseEntity<String> handleDataUnavailableException(DataUnavailableException ex) {
        // Custom response for data unavailability
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
