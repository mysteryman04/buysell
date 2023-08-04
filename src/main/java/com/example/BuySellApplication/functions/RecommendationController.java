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
import java.util.List;


@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/recommendations")
    public String showRecommendations(Model model, Principal principal) {
        User currentUser = userService.getUserByPrincipal(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Long> recommendedProductIds = recommendationService.getRecommendedProductIds(currentUser);

        List<Product> recommendedProducts = productService.getProductsByIds(recommendedProductIds);

        model.addAttribute("recommendedProducts", recommendedProducts);
        model.addAttribute("user", currentUser);

        return "recommendations";
    }




    @GetMapping("/product-info/{productId}")
    public String getProductInfo(@PathVariable Long productId, Principal principal) {

        User currentUser = userService.getUserByPrincipal(principal);
        if (currentUser != null) {
            // Record the user interaction in the recommendation service and update JSON file
            recommendationService.recordUserInteractionAndWriteToJson(currentUser.getId(), productId);
        }
        return "product-info";
    }

    @ExceptionHandler(DataUnavailableException.class)
    public ResponseEntity<String> handleDataUnavailableException(DataUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
